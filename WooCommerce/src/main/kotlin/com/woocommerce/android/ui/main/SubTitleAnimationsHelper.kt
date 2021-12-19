package com.woocommerce.android.ui.main

import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator

class SubTitleAnimationsHelper(
    expandedMargin: Int,
    collapsedMargin: Int,
    duration: Long = 200L,
    listener: ValueAnimator.AnimatorUpdateListener
) {

    val showSubtitleAnimator by lazy {
        createCollapsingToolbarMarginBottomAnimator(
            from = collapsedMargin,
            to = expandedMargin,
            duration = duration,
            listener = listener
        )
    }

    val hideSubtitleAnimator by lazy {
        createCollapsingToolbarMarginBottomAnimator(
            from = expandedMargin,
            to = collapsedMargin,
            duration = duration,
            listener = listener
        )
    }

    private fun createCollapsingToolbarMarginBottomAnimator(
        from: Int,
        to: Int,
        duration: Long,
        listener: ValueAnimator.AnimatorUpdateListener
    ): ValueAnimator {
        return ValueAnimator.ofInt(from, to)
            .also { valueAnimator ->
                valueAnimator.duration = duration
                valueAnimator.interpolator = AccelerateDecelerateInterpolator()
                valueAnimator.addUpdateListener(listener)
            }
    }
}
