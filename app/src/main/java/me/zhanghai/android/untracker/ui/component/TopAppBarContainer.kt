package me.zhanghai.android.untracker.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopAppBarContainer(
    scrollBehavior: TopAppBarScrollBehavior,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    content: @Composable () -> Unit,
) {
    val heightOffsetLimit = with(LocalDensity.current) { -64.0.dp.toPx() }
    SideEffect {
        if (scrollBehavior.state.heightOffsetLimit != heightOffsetLimit) {
            scrollBehavior.state.heightOffsetLimit = heightOffsetLimit
        }
    }
    val colorTransitionFraction by
        remember(scrollBehavior) {
            // derivedStateOf to prevent redundant recompositions when the content scrolls.
            derivedStateOf {
                val overlappingFraction = scrollBehavior.state.overlappedFraction
                if (overlappingFraction > 0.01f) 1f else 0f
            }
        }
    val color by
        animateColorAsState(
            targetValue = colors.containerColor(colorTransitionFraction),
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        )
    Surface(color = color, content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
private fun TopAppBarColors.containerColor(colorTransitionFraction: Float): Color =
    lerp(
        containerColor,
        scrolledContainerColor,
        FastOutLinearInEasing.transform(colorTransitionFraction),
    )
