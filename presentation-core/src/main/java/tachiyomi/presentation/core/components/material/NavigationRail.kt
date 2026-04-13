package tachiyomi.presentation.core.components.material

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * M3E Wide Navigation Rail for tablet layouts.
 *
 * @see [androidx.compose.material3.WideNavigationRail]
 */
@Composable
fun NavigationRail(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WideNavigationRailDefaults.windowInsets,
    header: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    androidx.compose.material3.WideNavigationRail(
        modifier = modifier,
        windowInsets = windowInsets,
        header = header,
        content = content,
    )
}
