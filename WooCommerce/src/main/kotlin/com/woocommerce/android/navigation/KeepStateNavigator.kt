package com.woocommerce.android.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import com.woocommerce.android.ui.base.TopLevelFragment
import java.util.*

private const val TOP_LEVEL_FRAGMENT_TAG_KEY = "top_level_fragment_tag"

/**
 * A custom navigator that keeps the state of toplevel navigation fragments when navigating to other tabs.
 * Based on the solution described here: https://github.com/STAR-ZERO/navigation-keep-fragment-sample
 */
@Navigator.Name("fragment")
class KeepStateNavigator(
    private val context: Context,
    private val manager: FragmentManager, // Should pass childFragmentManager.
    private val containerId: Int
) : FragmentNavigator(context, manager, containerId) {
    private var backStack: ArrayDeque<Int>

    private var tagOfLastTopLevelFragment: String? = null

    init {
        val field = FragmentNavigator::class.java.getDeclaredField("mBackStack")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        backStack = field.get(this) as ArrayDeque<Int>
    }

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        val cls = Class.forName(destination.className)
        if (!TopLevelFragment::class.java.isAssignableFrom(cls)) {
            return super.navigate(destination, args, navOptions, navigatorExtras)
        }

        if (manager.isStateSaved) {
            Log.i(
                KeepStateNavigator::class.simpleName,
                "Ignoring navigate() call: FragmentManager has already" +
                    " saved its state"
            )
            return null
        }

        val tag = destination.id.toString()
        val transaction = manager.beginTransaction()

        manager.findFragmentByTag(tagOfLastTopLevelFragment)?.let {
            transaction.detach(it)
        }

        while (manager.backStackEntryCount >= 1) {
            manager.popBackStackImmediate()
        }

        var fragment = manager.findFragmentByTag(tag)

        if (fragment == null) {
            val className = destination.className
            fragment = manager.fragmentFactory.instantiate(context.classLoader, className)
            transaction.add(containerId, fragment, tag)
        } else {
            transaction.attach(fragment)
        }
        tagOfLastTopLevelFragment = tag

        backStack.clear()
        backStack.add(destination.id)

        transaction.setPrimaryNavigationFragment(fragment)
        transaction.setReorderingAllowed(true)
        transaction.commit()

        return destination
    }

    override fun onSaveState(): Bundle {
        return (super.onSaveState() ?: Bundle()).apply {
            putString(TOP_LEVEL_FRAGMENT_TAG_KEY, tagOfLastTopLevelFragment)
        }
    }

    override fun onRestoreState(savedState: Bundle?) {
        super.onRestoreState(savedState)
        tagOfLastTopLevelFragment = savedState?.getString(TOP_LEVEL_FRAGMENT_TAG_KEY)
    }
}
