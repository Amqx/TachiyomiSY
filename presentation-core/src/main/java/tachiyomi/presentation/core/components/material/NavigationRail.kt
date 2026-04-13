package tachiyomi.presentation.core.components.material

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * M3E Wide Navigation Rail for tablet layouts.
 *
 * @see [androidx.compose.material3.WideNavigationRail]
 */
@Composable
fun NavigationRail(
    modifier: Modifier = Modifier,
    containerColor: Color = WideNavigationRailDefaults.containerColor,
    windowInsets: WindowInsets = WideNavigationRailDefaults.windowInsets,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    androidx.compose.material3.WideNavigationRail(
        modifier = modifier,
        containerColor = containerColor,
        windowInsets = windowInsets,
        header = header,
        content = content,
    )
}
