package tachiyomi.presentation.core.components.material

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.WavyProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Thin wrapper around the M3E determinate LinearWavyProgressIndicator.
 * Use when download progress percentage is known (1–99%).
 *
 * @see [androidx.compose.material3.LinearWavyProgressIndicator]
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LinearWavyProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = WavyProgressIndicatorDefaults.indicatorColor,
    trackColor: Color = WavyProgressIndicatorDefaults.trackColor,
) {
    androidx.compose.material3.LinearWavyProgressIndicator(
        progress = progress,
        modifier = modifier,
        color = color,
        trackColor = trackColor,
    )
}

/**
 * Thin wrapper around the M3E indeterminate LinearWavyProgressIndicator.
 * Use when a download is queued or at 0% (duration unknown).
 *
 * @see [androidx.compose.material3.LinearWavyProgressIndicator]
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LinearWavyProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = WavyProgressIndicatorDefaults.indicatorColor,
    trackColor: Color = WavyProgressIndicatorDefaults.trackColor,
) {
    androidx.compose.material3.LinearWavyProgressIndicator(
        modifier = modifier,
        color = color,
        trackColor = trackColor,
    )
}

/**
 * Thin wrapper around the M3E determinate CircularWavyProgressIndicator.
 * Use when download progress percentage is known (1–99%).
 *
 * @see [androidx.compose.material3.CircularWavyProgressIndicator]
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CircularWavyProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = WavyProgressIndicatorDefaults.indicatorColor,
    trackColor: Color = WavyProgressIndicatorDefaults.trackColor,
) {
    androidx.compose.material3.CircularWavyProgressIndicator(
        progress = progress,
        modifier = modifier,
        color = color,
        trackColor = trackColor,
    )
}
