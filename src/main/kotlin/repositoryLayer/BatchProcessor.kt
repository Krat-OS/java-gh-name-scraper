package krat_os.name_scraper.repositoryLayer

import commons.JavaFile
import kotlinx.coroutines.*
import org.kohsuke.github.*

class BatchProcessor(
    private val fileRetriever: GitHubFileRetriever,
    private val batchSize: Int = 10,
    private val delayBetweenBatches: Long = 2000
) {
    suspend fun processBatches(
        repo: GHRepository,
        files: List<GHTreeEntry>
    ): List<JavaFile> = coroutineScope {
        files.chunked(batchSize)
            .flatMapIndexed { index, chunk ->
                println("Processing batch ${index + 1}/${(files.size + batchSize - 1) / batchSize}")

                if (index > 0) {
                    delay(delayBetweenBatches)
                }

                chunk.map { entry ->
                    async {
                        fileRetriever.fetchWithRetry(repo, entry)
                    }
                }.awaitAll().filterNotNull()
            }
    }
}
