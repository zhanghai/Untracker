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

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import kotlin.math.roundToInt
import me.zhanghai.android.untracker.ui.token.Durations
import me.zhanghai.android.untracker.ui.token.Easings

// See also
// https://m3.material.io/styles/motion/transitions/applying-transitions#ab8885f6-5517-419d-80de-bea50cd10467
// .
// See also com.google.android.material.transition.MaterialFadeThrough .
// This is made to use Short4 duration similar to dialog exit transition (
// https://m3.material.io/styles/motion/easing-and-duration/applying-easing-and-duration#e5b958f0-435d-4e84-aed4-8d1ea395fa5c
// ), in order to better match
// the M3 spec video.

fun topLevelTransitionSpec(): ContentTransform =
    ContentTransform(
        fadeIn(
            animationSpec =
                tween(
                    durationMillis = (0.65f * Durations.Short4).roundToInt(),
                    delayMillis = (0.35f * Durations.Short4).roundToInt(),
                    easing = Easings.Emphasized,
                )
        ),
        fadeOut(
            animationSpec =
                tween(
                    durationMillis = (0.35f * Durations.Short4).roundToInt(),
                    easing = Easings.Emphasized,
                )
        ),
    )

fun topLevelPopTransitionSpec(): ContentTransform = topLevelTransitionSpec()

// See also
// https://m3.material.io/styles/motion/transitions/transition-patterns#df9c7d76-1454-47f3-ad1c-268a31f58bad
// .
// See also com.google.android.material.transition.MaterialSharedAxis .
// This is made to use 0.1x distance (instead of a fixed 30dp) for sliding, the same as the fraction
// used in Android Activity transition.

fun sharedAxisXTransitionSpec(): ContentTransform =
    ContentTransform(
        slideInHorizontally(
            animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
            initialOffsetX = { (0.1f * it).roundToInt() },
        ) +
            fadeIn(
                animationSpec =
                    tween(
                        durationMillis = (0.65f * Durations.Long1).roundToInt(),
                        delayMillis = (0.35f * Durations.Long1).roundToInt(),
                        easing = Easings.Emphasized,
                    )
            ),
        slideOutHorizontally(
            animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
            targetOffsetX = { (-0.1f * it).roundToInt() },
        ) +
            fadeOut(
                animationSpec =
                    tween(
                        durationMillis = (0.35f * Durations.Long1).roundToInt(),
                        easing = Easings.Emphasized,
                    )
            ),
    )

fun sharedAxisXPopTransitionSpec(): ContentTransform =
    ContentTransform(
        slideInHorizontally(
            animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
            initialOffsetX = { (-0.1f * it).roundToInt() },
        ) +
            fadeIn(
                animationSpec =
                    tween(
                        durationMillis = (0.65f * Durations.Long1).roundToInt(),
                        delayMillis = (0.35f * Durations.Long1).roundToInt(),
                        easing = Easings.Emphasized,
                    )
            ),
        slideOutHorizontally(
            animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
            targetOffsetX = { (0.1f * it).roundToInt() },
        ) +
            fadeOut(
                animationSpec =
                    tween(
                        durationMillis = (0.35f * Durations.Long1).roundToInt(),
                        easing = Easings.Emphasized,
                    )
            ),
    )
