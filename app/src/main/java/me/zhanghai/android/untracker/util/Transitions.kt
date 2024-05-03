/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.zhanghai.android.untracker.util

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import kotlin.math.roundToInt

// See also android:Animation.Activity .
// Modified to be fade through to account for missing expand transition.

fun activityEnter(): EnterTransition =
    slideInHorizontally(
        animationSpec =
            tween(
                durationMillis = MotionTokens.durationLong1,
                easing = MotionTokens.easingEmphasizedInterpolator
            ),
        initialOffsetX = { (0.1f * it).roundToInt() }
    ) +
        fadeIn(
            animationSpec =
                tween(
                    delayMillis = 50,
                    durationMillis = 83,
                    easing = LinearEasing,
                )
        )

fun activityExit(): ExitTransition =
    slideOutHorizontally(
        animationSpec =
            tween(
                durationMillis = MotionTokens.durationLong1,
                easing = MotionTokens.easingEmphasizedInterpolator
            ),
        targetOffsetX = { (-0.1f * it).roundToInt() }
    ) + fadeOut(animationSpec = tween(durationMillis = 50, easing = LinearEasing))

fun activityPopEnter(): EnterTransition =
    slideInHorizontally(
        animationSpec =
            tween(
                durationMillis = MotionTokens.durationLong1,
                easing = MotionTokens.easingEmphasizedInterpolator
            ),
        initialOffsetX = { (-0.1f * it).roundToInt() }
    ) + fadeIn(animationSpec = tween(delayMillis = 83, durationMillis = 50, easing = LinearEasing))

fun activityPopExit(): ExitTransition =
    slideOutHorizontally(
        animationSpec =
            tween(
                durationMillis = MotionTokens.durationLong1,
                easing = MotionTokens.easingEmphasizedInterpolator
            ),
        targetOffsetX = { (0.1f * it).roundToInt() }
    ) +
        fadeOut(
            animationSpec =
                tween(
                    durationMillis = 83,
                    easing = LinearEasing,
                )
        )

// See also com.google.android.material.transition.MaterialFadeThrough .

fun fadeThroughEnter(): EnterTransition =
    fadeIn(
        animationSpec =
            tween(
                durationMillis = (0.65f * MotionTokens.durationLong1).roundToInt(),
                delayMillis = (0.35f * MotionTokens.durationLong1).roundToInt(),
                easing = MotionTokens.easingEmphasizedInterpolator
            )
    )

fun fadeThroughExit(): ExitTransition =
    fadeOut(
        animationSpec =
            tween(
                durationMillis = (0.35f * MotionTokens.durationLong1).roundToInt(),
                easing = MotionTokens.easingEmphasizedInterpolator
            )
    )

fun fadeThroughPopEnter(): EnterTransition =
    fadeIn(
        animationSpec =
            tween(
                durationMillis = (0.65f * MotionTokens.durationLong1).roundToInt(),
                delayMillis = (0.35f * MotionTokens.durationLong1).roundToInt(),
                easing = MotionTokens.easingEmphasizedInterpolator
            )
    )

fun fadeThroughPopExit(): ExitTransition =
    fadeOut(
        animationSpec =
            tween(
                durationMillis = (0.35f * MotionTokens.durationLong1).roundToInt(),
                easing = MotionTokens.easingEmphasizedInterpolator
            )
    )

// See also com.google.android.material.transition.MaterialSharedAxis .

fun sharedAxisXEnter(): EnterTransition =
    slideInHorizontally(
        animationSpec =
            tween(
                durationMillis = MotionTokens.durationLong1,
                easing = MotionTokens.easingEmphasizedInterpolator
            ),
        initialOffsetX = { it }
    ) + fadeThroughEnter()

fun sharedAxisXExit(): ExitTransition =
    slideOutHorizontally(
        animationSpec =
            tween(
                durationMillis = MotionTokens.durationLong1,
                easing = MotionTokens.easingEmphasizedInterpolator
            ),
        targetOffsetX = { -it }
    ) + fadeThroughExit()

fun sharedAxisXPopEnter(): EnterTransition =
    slideInHorizontally(
        animationSpec =
            tween(
                durationMillis = MotionTokens.durationLong1,
                easing = MotionTokens.easingEmphasizedInterpolator
            ),
        initialOffsetX = { -it }
    ) + fadeThroughPopEnter()

fun sharedAxisXPopExit(): ExitTransition =
    slideOutHorizontally(
        animationSpec =
            tween(
                durationMillis = MotionTokens.durationLong1,
                easing = MotionTokens.easingEmphasizedInterpolator
            ),
        targetOffsetX = { it }
    ) + fadeThroughPopExit()

// See also androidx.compose.material3.tokens.MotionTokens
private object MotionTokens {
    const val durationLong1 = 450

    val easingEmphasizedInterpolator =
        PathEasing(
            Path().apply {
                cubicTo(0.05f, 0f, 0.133333f, 0.06f, 0.166666f, 0.4f)
                cubicTo(0.208333f, 0.82f, 0.25f, 1f, 1f, 1f)
            }
        )
}

// See also androidx.core.view.animation.PathInterpolatorApi14
// An alternative is to port androidx.interpolator.view.animation.FastOutExtraSlowInInterpolator
@Immutable
class PathEasing(path: Path) : Easing {
    private val xValues: FloatArray
    private val yValues: FloatArray

    init {
        val pathMeasure = PathMeasure().apply { setPath(path, false) }
        val pathLength = pathMeasure.length
        val numPoints = (pathLength / PathEasingPrecision).toInt() + 1

        xValues = FloatArray(numPoints)
        yValues = FloatArray(numPoints)

        for (i in 0..<numPoints) {
            val distance = i * pathLength / (numPoints - 1)
            val position = pathMeasure.getPosition(distance)
            xValues[i] = position.x
            yValues[i] = position.y
        }
    }

    override fun transform(fraction: Float): Float {
        if (fraction <= 0f) {
            return 0f
        } else if (fraction > 1f) {
            return 1f
        } else {
            // Do a binary search for the correct x to interpolate between.
            var startIndex = 0
            var endIndex = xValues.size - 1
            while (endIndex - startIndex > 1) {
                val midIndex = (startIndex + endIndex) / 2
                if (fraction < xValues[midIndex]) {
                    endIndex = midIndex
                } else {
                    startIndex = midIndex
                }
            }

            val xRange = xValues[endIndex] - xValues[startIndex]
            if (xRange == 0f) {
                return yValues[startIndex]
            }

            val fractionInRange = fraction - xValues[startIndex]
            val yFraction = fractionInRange / xRange

            val startY = yValues[startIndex]
            val endY = yValues[endIndex]
            return startY + yFraction * (endY - startY)
        }
    }
}

private const val PathEasingPrecision = 0.002f
