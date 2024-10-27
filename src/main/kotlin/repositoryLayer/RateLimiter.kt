package krat_os.name_scraper.repositoryLayer

import kotlinx.coroutines.delay

class RateLimiter(
    private val minTimeBetweenRequests: Long = 100L // minimum 100ms between requests
) {
    private var lastRequestTime = 0L

    suspend fun rateLimit() {
        val now = System.currentTimeMillis()
        val timeSinceLastRequest = now - lastRequestTime
        if (timeSinceLastRequest < minTimeBetweenRequests) {
            delay(minTimeBetweenRequests - timeSinceLastRequest)
        }
        lastRequestTime = System.currentTimeMillis()
    }
}
