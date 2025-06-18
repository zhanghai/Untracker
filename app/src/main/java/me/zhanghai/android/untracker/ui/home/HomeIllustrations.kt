package me.zhanghai.android.untracker.ui.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun shareIllustration(): ImageVector =
    ImageVector.Builder(
            name = "ShareIllustration",
            defaultWidth = 480.dp,
            defaultHeight = 270.dp,
            viewportWidth = 240f,
            viewportHeight = 135f,
        )
        .apply {
            path(fill = SolidColor(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f))) {
                rect(0f, 0f, 240f, 135f)
            }
            path(fill = SolidColor(MaterialTheme.colorScheme.surface)) {
                moveTo(240f, 16f)
                horizontalLineTo(44f)
                arcTo(28f, 28f, 0f, false, false, 16f, 44f)
                verticalLineTo(134f)
                horizontalLineTo(240f)
                close()
            }
            path(fill = SolidColor(MaterialTheme.colorScheme.surfaceVariant)) {
                circle(68f, 68f, 28f)
                roundRect(40f, 110f, 56f, 12f, 2f)
                circle(172f, 68f, 28f)
                roundRect(144f, 110f, 56f, 12f, 2f)
            }
        }
        .build()

@Composable
fun selectIllustration(): ImageVector =
    ImageVector.Builder(
            name = "SelectIllustration",
            defaultWidth = 480.dp,
            defaultHeight = 270.dp,
            viewportWidth = 240f,
            viewportHeight = 135f,
        )
        .apply {
            path(fill = SolidColor(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f))) {
                rect(0f, 0f, 240f, 135f)
            }
            path(fill = SolidColor(MaterialTheme.colorScheme.surface)) {
                rect(38f, 80f, 74f, 16f)
                roundRect(16f, 16f, 208f, 48f, 8f)
            }
            path(fill = SolidColor(MaterialTheme.colorScheme.surfaceVariant)) {
                moveTo(38f, 96f)
                horizontalLineToRelative(-11f)
                arcToRelative(11f, 11f, 0f, true, false, 11f, 11f)
                close()
                moveTo(112f, 96f)
                verticalLineToRelative(11f)
                arcToRelative(11f, 11f, 0f, true, false, 11f, -11f)
                close()
                roundRect(32f, 32f, 32f, 16f, 2f)
                roundRect(80f, 32f, 32f, 16f, 2f)
                roundRect(128f, 32f, 32f, 16f, 2f)
                roundRect(176f, 32f, 32f, 16f, 2f)
            }
        }
        .build()

private fun PathBuilder.circle(cx: Float, cy: Float, radius: Float) {
    moveTo(cx + radius, cy)
    arcToRelative(radius, radius, 0f, false, false, -2 * radius, 0f)
    arcToRelative(radius, radius, 0f, false, false, 2 * radius, 0f)
    close()
}

private fun PathBuilder.rect(x: Float, y: Float, width: Float, height: Float) {
    moveTo(x + width, y)
    horizontalLineToRelative(-width)
    verticalLineToRelative(height)
    horizontalLineToRelative(width)
    close()
}

private fun PathBuilder.roundRect(x: Float, y: Float, width: Float, height: Float, radii: Float) {
    moveTo(x + width, y + radii)
    arcToRelative(radii, radii, 0f, false, false, -radii, -radii)
    horizontalLineToRelative(-(width - 2 * radii))
    arcToRelative(radii, radii, 0f, false, false, -radii, radii)
    verticalLineToRelative(height - 2 * radii)
    arcToRelative(radii, radii, 0f, false, false, radii, radii)
    horizontalLineToRelative(width - 2 * radii)
    arcToRelative(radii, radii, 0f, false, false, radii, -radii)
    close()
}
