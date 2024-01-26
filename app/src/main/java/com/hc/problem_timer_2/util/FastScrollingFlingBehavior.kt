package com.hc.problem_timer_2.util

import android.util.Log
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.Job
import kotlin.math.abs

@Composable
internal fun getFastScrollingFlingBehavior(onFinished: () -> Job): FlingBehavior {
    val flingSpec = rememberSplineBasedDecay<Float>()
    return remember(flingSpec) {
        FastScrollingFlingBehavior(flingSpec, onFinished)
    }
}

private class FastScrollingFlingBehavior(
    private val flingDecay: DecayAnimationSpec<Float>,
    private val onFinished: () -> Job
) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        var isAnimationRunning = true
        // Prevent very fast scroll
        val newVelocity =
            if (initialVelocity > 0F) minOf(initialVelocity, 15_000F)
            else maxOf(initialVelocity, -15_000F)
        return if (abs(newVelocity) > 1f) {
            var velocityLeft = newVelocity
            var lastValue = 0f
            val animationState = AnimationState(
                initialValue = 0f,
                initialVelocity = newVelocity,
            )
            animationState.animateDecay(flingDecay) {
                val delta = value - lastValue
                val consumed = scrollBy(delta)
                lastValue = value
                velocityLeft = this.velocity
                if (isAnimationRunning != isRunning) {
                    if (!isRunning) {
                        onFinished()
                    }
                    isAnimationRunning = isRunning
                }
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
            velocityLeft
        } else newVelocity
    }
}