package krat_os.name_scraper.repositoryLayer

import commons.JavaFile
import kotlinx.coroutines.*
import org.kohsuke.github.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow

class GitHubFileRetriever(
    private val rateLimiter: RateLimiter,
    private val cache: FileContentCache,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
    private val maxRetries: Int = 3
) {
    suspend fun fetchWithRetry(repo: GHRepository, entry: GHTreeEntry): JavaFile? {
        var retryCount = 0
        var lastException: Exception? = null

        while (retryCount < maxRetries) {
            try {
                rateLimiter.rateLimit()
                val content = getFileContent(repo, entry.path)
                return JavaFile(
                    name = entry.path.substringAfterLast('/'),
                    content = content,
                    path = entry.path
                )
            } catch (e: Exception) {
                lastException = e
                retryCount++
                val waitTime = (2.0.pow(retryCount.toDouble()) * 1000).toLong()
                println("Retry $retryCount for ${entry.path} after ${waitTime}ms delay. Error: ${e.message}")
                delay(waitTime)
            }
        }

        println("Failed to fetch ${entry.path} after $maxRetries retries: ${lastException?.message}")
        return null
    }

    private suspend fun getFileContent(repo: GHRepository, path: String): String {
        return cache.getOrPutSuspend(repo.fullName + ":" + path) {
            withContext(coroutineContext) {
                repo.getFileContent(path).read().bufferedReader().use { it.readText() }
            }
        }
    }
}
