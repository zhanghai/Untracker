package me.zhanghai.android.untracker.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopAppBarContainer(scrollBehavior: TopAppBarScrollBehavior, content: @Composable () -> Unit) {
    val heightOffsetLimit = with(LocalDensity.current) { -64.0.dp.toPx() }
    SideEffect {
        if (scrollBehavior.state.heightOffsetLimit != heightOffsetLimit) {
            scrollBehavior.state.heightOffsetLimit = heightOffsetLimit
        }
    }
    val targetColor =
        if (scrollBehavior.state.overlappedFraction > 0.01f) {
            MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        } else {
            MaterialTheme.colorScheme.surface
        }
    val color by
        animateColorAsState(
            targetValue = targetColor,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        )
    Surface(color = color, content = content)
}
