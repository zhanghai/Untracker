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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import kotlin.math.roundToInt
import me.zhanghai.android.untracker.ui.token.Durations
import me.zhanghai.android.untracker.ui.token.Easings

// See also android:Animation.Activity .
// Modified to be fade through to account for missing expand transition.

fun activityEnter(): EnterTransition =
    fadeIn(animationSpec = tween(delayMillis = 50, durationMillis = 83, easing = LinearEasing)) +
        slideInHorizontally(
            animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
            initialOffsetX = { (0.1f * it).roundToInt() },
        )

fun activityExit(): ExitTransition =
    fadeOut(animationSpec = tween(durationMillis = 50, easing = LinearEasing)) +
        slideOutHorizontally(
            animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
            targetOffsetX = { (-0.1f * it).roundToInt() },
        )

fun activityPopEnter(): EnterTransition =
    fadeIn(animationSpec = tween(delayMillis = 83, durationMillis = 50, easing = LinearEasing)) +
        slideInHorizontally(
            animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
            initialOffsetX = { (-0.1f * it).roundToInt() },
        )

fun activityPopExit(): ExitTransition =
    fadeOut(animationSpec = tween(durationMillis = 83, easing = LinearEasing)) +
        slideOutHorizontally(
            animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
            targetOffsetX = { (0.1f * it).roundToInt() },
        )

// There is no specification for top level transition right now . See also
// https://m3.material.io/styles/motion/transitions/applying-transitions#ab8885f6-5517-419d-80de-bea50cd10467
// .
// This is made the same as fade through just with no scaling, android:integer/config_shortAnimTime
// duration, and linear easing for now.

fun topLevelEnter(): EnterTransition =
    fadeIn(
        animationSpec =
            tween(
                durationMillis = (0.65f * Durations.Short4).roundToInt(),
                delayMillis = (0.35f * Durations.Short4).roundToInt(),
                easing = Easings.Linear,
            )
    )

fun topLevelExit(): ExitTransition =
    fadeOut(
        animationSpec =
            tween(durationMillis = (0.35f * Durations.Short4).roundToInt(), easing = Easings.Linear)
    )

fun topLevelPopEnter(): EnterTransition = topLevelEnter()

fun topLevelPopExit(): ExitTransition = topLevelExit()

// See also com.google.android.material.transition.MaterialFadeThrough .

fun fadeThroughEnter(): EnterTransition =
    fadeIn(
        animationSpec =
            tween(
                durationMillis = (0.65f * Durations.Long1).roundToInt(),
                delayMillis = (0.35f * Durations.Long1).roundToInt(),
                easing = Easings.Emphasized,
            )
    ) +
        scaleIn(
            animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
            initialScale = 0.92f,
        )

fun fadeThroughExit(): ExitTransition =
    fadeOut(
        animationSpec =
            tween(
                durationMillis = (0.35f * Durations.Long1).roundToInt(),
                easing = Easings.Emphasized,
            )
    )

fun fadeThroughPopEnter(): EnterTransition = fadeThroughEnter()

fun fadeThroughPopExit(): ExitTransition = fadeThroughExit()

// See also com.google.android.material.transition.MaterialSharedAxis .

fun sharedAxisXEnter(): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
        initialOffsetX = { it },
    ) + fadeThroughEnter()

fun sharedAxisXExit(): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
        targetOffsetX = { -it },
    ) + fadeThroughExit()

fun sharedAxisXPopEnter(): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
        initialOffsetX = { -it },
    ) + fadeThroughPopEnter()

fun sharedAxisXPopExit(): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(durationMillis = Durations.Long1, easing = Easings.Emphasized),
        targetOffsetX = { it },
    ) + fadeThroughPopExit()
