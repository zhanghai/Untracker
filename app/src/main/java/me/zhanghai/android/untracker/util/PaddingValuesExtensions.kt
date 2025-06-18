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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

operator fun PaddingValues.plus(paddingValues: PaddingValues): PaddingValues =
    PlusedPaddingValues(this, paddingValues)

@Stable
private class PlusedPaddingValues(
    private val first: PaddingValues,
    private val second: PaddingValues,
) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        first.calculateLeftPadding(layoutDirection) + second.calculateLeftPadding(layoutDirection)

    override fun calculateTopPadding(): Dp =
        first.calculateTopPadding() + second.calculateTopPadding()

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        first.calculateRightPadding(layoutDirection) + second.calculateRightPadding(layoutDirection)

    override fun calculateBottomPadding(): Dp =
        first.calculateBottomPadding() + second.calculateBottomPadding()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is PlusedPaddingValues) {
            return false
        }
        return other.first == first && other.second == second
    }

    override fun hashCode(): Int = first.hashCode() + second.hashCode() * 31

    override fun toString(): String = "($first + $second)"
}

@Composable
fun PaddingValues.copy(
    horizontal: Dp = Dp.Unspecified,
    vertical: Dp = Dp.Unspecified,
): PaddingValues = copy(start = horizontal, top = vertical, end = horizontal, bottom = vertical)

@Composable
fun PaddingValues.copy(
    start: Dp = Dp.Unspecified,
    top: Dp = Dp.Unspecified,
    end: Dp = Dp.Unspecified,
    bottom: Dp = Dp.Unspecified,
): PaddingValues = CopiedPaddingValues(start, top, end, bottom, this)

@Stable
private class CopiedPaddingValues(
    private val start: Dp,
    private val top: Dp,
    private val end: Dp,
    private val bottom: Dp,
    private val paddingValues: PaddingValues,
) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        (if (layoutDirection == LayoutDirection.Ltr) start else end).takeIf { it != Dp.Unspecified }
            ?: paddingValues.calculateLeftPadding(layoutDirection)

    override fun calculateTopPadding(): Dp =
        top.takeIf { it != Dp.Unspecified } ?: paddingValues.calculateTopPadding()

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        (if (layoutDirection == LayoutDirection.Ltr) end else start).takeIf { it != Dp.Unspecified }
            ?: paddingValues.calculateRightPadding(layoutDirection)

    override fun calculateBottomPadding(): Dp =
        bottom.takeIf { it != Dp.Unspecified } ?: paddingValues.calculateBottomPadding()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is CopiedPaddingValues) {
            return false
        }
        return start == other.start &&
            top == other.top &&
            end == other.end &&
            bottom == other.bottom &&
            paddingValues == other.paddingValues
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + top.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + paddingValues.hashCode()
        return result
    }

    override fun toString(): String {
        return "Copied($start, $top, $end, $bottom, $paddingValues)"
    }
}

@Composable fun PaddingValues.offset(all: Dp = 0.dp): PaddingValues = offset(all, all, all, all)

@Composable
fun PaddingValues.offset(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): PaddingValues =
    offset(horizontal, vertical, horizontal, vertical)

@Composable
fun PaddingValues.offset(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
): PaddingValues = OffsetPaddingValues(start, top, end, bottom, this)

@Stable
private class OffsetPaddingValues(
    private val start: Dp,
    private val top: Dp,
    private val end: Dp,
    private val bottom: Dp,
    private val paddingValues: PaddingValues,
) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        paddingValues.calculateLeftPadding(layoutDirection) +
            (if (layoutDirection == LayoutDirection.Ltr) start else end)

    override fun calculateTopPadding(): Dp = paddingValues.calculateTopPadding() + top

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        paddingValues.calculateRightPadding(layoutDirection) +
            (if (layoutDirection == LayoutDirection.Ltr) end else start)

    override fun calculateBottomPadding(): Dp = paddingValues.calculateBottomPadding() + bottom

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is OffsetPaddingValues) {
            return false
        }
        return start == other.start &&
            top == other.top &&
            end == other.end &&
            bottom == other.bottom &&
            paddingValues == other.paddingValues
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + top.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + paddingValues.hashCode()
        return result
    }

    override fun toString(): String {
        return "Offset($start, $top, $end, $bottom, $paddingValues)"
    }
}

fun PaddingValues.asInsets(): WindowInsets = PaddingValuesInsets(this)

@Stable
private class PaddingValuesInsets(private val paddingValues: PaddingValues) : WindowInsets {
    override fun getLeft(density: Density, layoutDirection: LayoutDirection) =
        with(density) { paddingValues.calculateLeftPadding(layoutDirection).roundToPx() }

    override fun getTop(density: Density) =
        with(density) { paddingValues.calculateTopPadding().roundToPx() }

    override fun getRight(density: Density, layoutDirection: LayoutDirection) =
        with(density) { paddingValues.calculateRightPadding(layoutDirection).roundToPx() }

    override fun getBottom(density: Density) =
        with(density) { paddingValues.calculateBottomPadding().roundToPx() }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is PaddingValuesInsets) {
            return false
        }
        return other.paddingValues == paddingValues
    }

    override fun hashCode(): Int = paddingValues.hashCode()

    override fun toString(): String {
        val layoutDirection = LayoutDirection.Ltr
        val start = paddingValues.calculateLeftPadding(layoutDirection)
        val top = paddingValues.calculateTopPadding()
        val end = paddingValues.calculateRightPadding(layoutDirection)
        val bottom = paddingValues.calculateBottomPadding()
        return "PaddingValues($start, $top, $end, $bottom)"
    }
}
