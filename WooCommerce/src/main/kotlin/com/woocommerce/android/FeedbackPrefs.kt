package com.woocommerce.android

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woocommerce.android.FeedbackPrefs.FeedbackPrefKey.FEATURE_FEEDBACK_SETTINGS
import com.woocommerce.android.FeedbackPrefs.FeedbackPrefKey.LAST_FEEDBACK_DATE
import com.woocommerce.android.extensions.greaterThan
import com.woocommerce.android.extensions.pastTimeDeltaFromNowInDays
import com.woocommerce.android.model.FeatureFeedbackSettings
import com.woocommerce.android.util.PreferenceUtils
import java.util.Date

// Guaranteed to hold a reference to the application context, which is safe
@SuppressLint("StaticFieldLeak")
object FeedbackPrefs {
    private lateinit var context: Context
    private val gson by lazy { Gson() }
    private val featureMapTypeToken by lazy { object : TypeToken<Map<String, String>>() {}.type }
    private val sharedPreferences get() = PreferenceManager.getDefaultSharedPreferences(context)

    private const val THREE_MONTHS_IN_DAYS = 90
    private const val SIX_MONTHS_IN_DAYS = 180

    private val featureMap
        get() = getString(FEATURE_FEEDBACK_SETTINGS)
            .takeIf { it.isNotEmpty() }
            ?.let {
                gson.fromJson<Map<String, String>>(it, featureMapTypeToken)
            } ?: mapOf()

    val userFeedbackIsDue: Boolean
        get() = AppPrefs.installationDate?.pastTimeDeltaFromNowInDays greaterThan THREE_MONTHS_IN_DAYS &&
            lastFeedbackDate?.pastTimeDeltaFromNowInDays greaterThan SIX_MONTHS_IN_DAYS

    var lastFeedbackDate: Date?
        get() = getString(LAST_FEEDBACK_DATE)
            .toLongOrNull()
            ?.let { Date(it) }
            ?: Date(0)
        set(value) = value
            ?.time.toString()
            .let { setString(LAST_FEEDBACK_DATE, it) }

    fun init(context: Context) {
        FeedbackPrefs.context = context.applicationContext
    }

    fun getFeatureFeedbackSettings(featureKey: String) =
        featureMap[featureKey]
            ?.let { gson.fromJson(it, FeatureFeedbackSettings::class.java) }

    fun setFeatureFeedbackSettings(featureKey: String, settings: FeatureFeedbackSettings) {
        featureMap
            .toMutableMap()
            .run {
                set(featureKey, gson.toJson(settings))
                toMap()
            }
            .let { gson.toJson(it) }
            .run { setString(FEATURE_FEEDBACK_SETTINGS, this) }
    }

    private fun getString(key: FeedbackPrefKey, defaultValue: String = ""): String {
        return PreferenceUtils.getString(sharedPreferences, key.toString(), defaultValue)
            ?: defaultValue
    }

    private fun setString(key: FeedbackPrefKey, value: String) =
        PreferenceUtils.setString(sharedPreferences, key.toString(), value)

    private enum class FeedbackPrefKey {
        // Date of the last time the user sent feedback on the app
        LAST_FEEDBACK_DATE,

        // A map of every feature feedback the user has given within the app
        FEATURE_FEEDBACK_SETTINGS
    }
}
