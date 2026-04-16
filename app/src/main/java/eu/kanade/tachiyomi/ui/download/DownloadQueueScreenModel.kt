package eu.kanade.tachiyomi.ui.download

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.download.model.Download
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

data class DownloadHeaderUiState(
    val sourceId: Long,
    val sourceName: String,
    val items: List<Download>,
)

class DownloadQueueScreenModel(
    private val downloadManager: DownloadManager = Injekt.get(),
) : ScreenModel {

    private val _state = MutableStateFlow(emptyList<DownloadHeaderUiState>())
    val state = _state.asStateFlow()

    val isDownloaderRunning = downloadManager.isDownloaderRunning
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        screenModelScope.launch {
            downloadManager.queueState
                .map { downloads ->
                    downloads
                        .groupBy { it.source }
                        .map { entry ->
                            DownloadHeaderUiState(
                                sourceId = entry.key.id,
                                sourceName = entry.key.name,
                                items = entry.value,
                            )
                        }
                }
                .collect { newList -> _state.update { newList } }
        }
    }

    fun startDownloads() {
        downloadManager.startDownloads()
    }

    fun pauseDownloads() {
        downloadManager.pauseDownloads()
    }

    fun clearQueue() {
        downloadManager.clearQueue()
    }

    fun reorder(downloads: List<Download>) {
        downloadManager.reorderQueue(downloads)
    }

    fun cancel(downloads: List<Download>) {
        downloadManager.cancelQueuedDownloads(downloads)
    }

    fun <R : Comparable<R>> reorderQueue(selector: (Download) -> R, reverse: Boolean = false) {
        val newDownloads = _state.value.flatMap { header ->
            header.items.sortedBy(selector).let { if (reverse) it.reversed() else it }
        }
        reorder(newDownloads)
    }

    fun moveToTop(download: Download) {
        val newDownloads = _state.value.flatMap { header ->
            if (header.sourceId == download.source.id) {
                listOf(download) + header.items.filter { it != download }
            } else {
                header.items
            }
        }
        reorder(newDownloads)
    }

    fun moveToBottom(download: Download) {
        val newDownloads = _state.value.flatMap { header ->
            if (header.sourceId == download.source.id) {
                header.items.filter { it != download } + download
            } else {
                header.items
            }
        }
        reorder(newDownloads)
    }

    fun moveSeriesToTop(download: Download) {
        val downloads = _state.value.flatMap { it.items }
        val (selected, others) = downloads.partition { it.manga.id == download.manga.id }
        reorder(selected + others)
    }

    fun moveSeriesToBottom(download: Download) {
        val downloads = _state.value.flatMap { it.items }
        val (selected, others) = downloads.partition { it.manga.id == download.manga.id }
        reorder(others + selected)
    }

    fun cancelSeries(download: Download) {
        val seriesDownloads = _state.value
            .flatMap { it.items }
            .filter { it.manga.id == download.manga.id }
        cancel(seriesDownloads)
    }
}
