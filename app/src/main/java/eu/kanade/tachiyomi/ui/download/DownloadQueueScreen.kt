package eu.kanade.tachiyomi.ui.download

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.DragHandle
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.AppBarActions
import eu.kanade.presentation.components.DropdownMenu
import eu.kanade.presentation.components.NestedMenuItem
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.data.download.model.Download
import kotlinx.collections.immutable.persistentListOf
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.Pill
import tachiyomi.presentation.core.components.material.LinearWavyProgressIndicator
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.screens.EmptyScreen

object DownloadQueueScreen : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { DownloadQueueScreenModel() }
        val downloadList by screenModel.state.collectAsState()
        val downloadCount by remember {
            derivedStateOf { downloadList.sumOf { it.items.size } }
        }

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        var fabExpanded by remember { mutableStateOf(true) }

        Scaffold(
            topBar = {
                AppBar(
                    titleContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(MR.strings.label_download_queue),
                                maxLines = 1,
                                modifier = Modifier.weight(1f, false),
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (downloadCount > 0) {
                                val pillAlpha = if (isSystemInDarkTheme()) 0.12f else 0.08f
                                Pill(
                                    text = "$downloadCount",
                                    modifier = Modifier.padding(start = 4.dp),
                                    color = MaterialTheme.colorScheme.onBackground
                                        .copy(alpha = pillAlpha),
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    },
                    navigateUp = navigator::pop,
                    actions = {
                        if (downloadList.isNotEmpty()) {
                            var sortExpanded by remember { mutableStateOf(false) }
                            val onDismissRequest = { sortExpanded = false }
                            DropdownMenu(
                                expanded = sortExpanded,
                                onDismissRequest = onDismissRequest,
                            ) {
                                NestedMenuItem(
                                    text = { Text(text = stringResource(MR.strings.action_order_by_upload_date)) },
                                    children = { closeMenu ->
                                        DropdownMenuItem(
                                            text = { Text(text = stringResource(MR.strings.action_newest)) },
                                            onClick = {
                                                screenModel.reorderQueue(
                                                    { it.chapter.dateUpload },
                                                    true,
                                                )
                                                closeMenu()
                                            },
                                        )
                                        DropdownMenuItem(
                                            text = { Text(text = stringResource(MR.strings.action_oldest)) },
                                            onClick = {
                                                screenModel.reorderQueue(
                                                    { it.chapter.dateUpload },
                                                    false,
                                                )
                                                closeMenu()
                                            },
                                        )
                                    },
                                )
                                NestedMenuItem(
                                    text = { Text(text = stringResource(MR.strings.action_order_by_chapter_number)) },
                                    children = { closeMenu ->
                                        DropdownMenuItem(
                                            text = { Text(text = stringResource(MR.strings.action_asc)) },
                                            onClick = {
                                                screenModel.reorderQueue(
                                                    { it.chapter.chapterNumber },
                                                    false,
                                                )
                                                closeMenu()
                                            },
                                        )
                                        DropdownMenuItem(
                                            text = { Text(text = stringResource(MR.strings.action_desc)) },
                                            onClick = {
                                                screenModel.reorderQueue(
                                                    { it.chapter.chapterNumber },
                                                    true,
                                                )
                                                closeMenu()
                                            },
                                        )
                                    },
                                )
                            }

                            AppBarActions(
                                persistentListOf(
                                    AppBar.Action(
                                        title = stringResource(MR.strings.action_sort),
                                        icon = Icons.AutoMirrored.Outlined.Sort,
                                        onClick = { sortExpanded = true },
                                    ),
                                    AppBar.OverflowAction(
                                        title = stringResource(MR.strings.action_cancel_all),
                                        onClick = { screenModel.clearQueue() },
                                    ),
                                ),
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            floatingActionButton = {
                val isRunning by screenModel.isDownloaderRunning.collectAsState()
                SmallExtendedFloatingActionButton(
                    text = {
                        val id = if (isRunning) MR.strings.action_pause else MR.strings.action_resume
                        Text(text = stringResource(id))
                    },
                    icon = {
                        val icon = if (isRunning) Icons.Outlined.Pause else Icons.Filled.PlayArrow
                        Icon(imageVector = icon, contentDescription = null)
                    },
                    onClick = {
                        if (isRunning) screenModel.pauseDownloads() else screenModel.startDownloads()
                    },
                    expanded = fabExpanded,
                    modifier = Modifier.animateFloatingActionButton(
                        visible = downloadList.isNotEmpty(),
                        alignment = Alignment.BottomEnd,
                    ),
                )
            },
        ) { contentPadding ->
            if (downloadList.isEmpty()) {
                EmptyScreen(
                    stringRes = MR.strings.information_no_downloads,
                    modifier = Modifier.padding(contentPadding),
                )
                return@Scaffold
            }

            DownloadQueueContent(
                downloadList = downloadList,
                contentPadding = contentPadding,
                onFabExpandedChange = { fabExpanded = it },
                onMoveToTop = screenModel::moveToTop,
                onMoveToBottom = screenModel::moveToBottom,
                onMoveSeriesToTop = screenModel::moveSeriesToTop,
                onMoveSeriesToBottom = screenModel::moveSeriesToBottom,
                onCancel = { screenModel.cancel(listOf(it)) },
                onCancelSeries = screenModel::cancelSeries,
                onReorder = screenModel::reorder,
            )
        }
    }
}

// ---- Internal composables ----

private sealed interface DownloadQueueItem {
    val key: String
    val contentType: String

    data class Header(val state: DownloadHeaderUiState) : DownloadQueueItem {
        override val key = "header-${state.sourceId}"
        override val contentType = "header"
    }

    data class Row(val sourceId: Long, val download: Download) : DownloadQueueItem {
        override val key = "download-${download.chapter.id}"
        override val contentType = "row"
    }
}

@Composable
private fun DownloadQueueContent(
    downloadList: List<DownloadHeaderUiState>,
    contentPadding: PaddingValues,
    onFabExpandedChange: (Boolean) -> Unit,
    onMoveToTop: (Download) -> Unit,
    onMoveToBottom: (Download) -> Unit,
    onMoveSeriesToTop: (Download) -> Unit,
    onMoveSeriesToBottom: (Download) -> Unit,
    onCancel: (Download) -> Unit,
    onCancelSeries: (Download) -> Unit,
    onReorder: (List<Download>) -> Unit,
) {
    val flatItems = remember(downloadList) {
        buildList {
            downloadList.forEach { header ->
                add(DownloadQueueItem.Header(header))
                header.items.forEach { add(DownloadQueueItem.Row(header.sourceId, it)) }
            }
        }
    }
    val itemsState = remember(downloadList) { flatItems.toMutableStateList() }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState, contentPadding) { from, to ->
        val fromItem = itemsState.getOrNull(from.index)
        val toItem = itemsState.getOrNull(to.index)
        if (fromItem is DownloadQueueItem.Row &&
            toItem is DownloadQueueItem.Row &&
            fromItem.sourceId == toItem.sourceId
        ) {
            itemsState.apply { add(to.index, removeAt(from.index)) }
            onReorder(itemsState.filterIsInstance<DownloadQueueItem.Row>().map { it.download })
        }
    }

    LaunchedEffect(downloadList) {
        if (!reorderableState.isAnyItemDragging) {
            itemsState.clear()
            itemsState.addAll(flatItems)
        }
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = contentPadding,
    ) {
        items(
            items = itemsState,
            key = { it.key },
            contentType = { it.contentType },
        ) { item ->
            when (item) {
                is DownloadQueueItem.Header -> {
                    DownloadQueueHeader(
                        state = item.state,
                        modifier = Modifier.animateItem(),
                    )
                }
                is DownloadQueueItem.Row -> {
                    ReorderableItem(reorderableState, item.key) {
                        DownloadQueueRow(
                            download = item.download,
                            modifier = Modifier.animateItem(),
                            dragHandle = {
                                IconButton(
                                    modifier = Modifier
                                        .draggableHandle(
                                            onDragStarted = { onFabExpandedChange(false) },
                                            onDragStopped = { onFabExpandedChange(true) },
                                        )
                                        .size(48.dp),
                                    onClick = {},
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DragHandle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            },
                            onMoveToTop = { onMoveToTop(item.download) },
                            onMoveToBottom = { onMoveToBottom(item.download) },
                            onMoveSeriesToTop = { onMoveSeriesToTop(item.download) },
                            onMoveSeriesToBottom = { onMoveSeriesToBottom(item.download) },
                            onCancel = { onCancel(item.download) },
                            onCancelSeries = { onCancelSeries(item.download) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadQueueHeader(
    state: DownloadHeaderUiState,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "${state.sourceName} (${state.items.size})",
        style = MaterialTheme.typography.titleSmall,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun DownloadQueueRow(
    download: Download,
    dragHandle: @Composable () -> Unit,
    onMoveToTop: () -> Unit,
    onMoveToBottom: () -> Unit,
    onMoveSeriesToTop: () -> Unit,
    onMoveSeriesToBottom: () -> Unit,
    onCancel: () -> Unit,
    onCancelSeries: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress by download.progressFlow.collectAsState(initial = download.progress)
    val pages = download.pages
    var menuExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            dragHandle()

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = download.manga.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = download.chapter.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp,
                )
            }

            if (pages != null) {
                Text(
                    text = "${download.downloadedImages}/${pages.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = stringResource(MR.strings.action_menu),
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(MR.strings.action_move_to_top)) },
                        onClick = { onMoveToTop(); menuExpanded = false },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(MR.strings.action_move_to_top_all_for_series)) },
                        onClick = { onMoveSeriesToTop(); menuExpanded = false },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(MR.strings.action_move_to_bottom)) },
                        onClick = { onMoveToBottom(); menuExpanded = false },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(MR.strings.action_move_to_bottom_all_for_series)) },
                        onClick = { onMoveSeriesToBottom(); menuExpanded = false },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(MR.strings.action_cancel)) },
                        onClick = { onCancel(); menuExpanded = false },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(MR.strings.cancel_all_for_series)) },
                        onClick = { onCancelSeries(); menuExpanded = false },
                    )
                }
            }
        }

        if (progress == 0 || pages == null) {
            LinearWavyProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            )
        } else {
            val animatedProgress by animateFloatAsState(
                targetValue = progress / 100f,
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                label = "downloadProgress",
            )
            LinearWavyProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
    }
}
