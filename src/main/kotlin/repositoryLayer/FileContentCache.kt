package krat_os.name_scraper.repositoryLayer

import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FileContentCache(private val maxSize: Int = 1000) {
    private val cache = ConcurrentHashMap<String, String>()
    private val mutex = Mutex()

    fun get(key: String): String? = cache[key]

    fun put(key: String, value: String) {
        if (cache.size > maxSize) {
            cleanup()
        }
        cache[key] = value
    }

    suspend fun getOrPutSuspend(key: String, defaultValue: suspend () -> String): String {
        return cache[key] ?: mutex.withLock {
            cache[key] ?: defaultValue().also { value ->
                if (cache.size > maxSize) {
                    cleanup()
                }
                cache[key] = value
            }
        }
    }

    private fun cleanup() {
        cache.keys.take(maxSize / 4).forEach { cache.remove(it) }
    }
}
