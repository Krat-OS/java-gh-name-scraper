package repositoryLayer

import commons.JavaFile
import kotlinx.coroutines.*
import krat_os.name_scraper.repositoryLayer.BatchProcessor
import krat_os.name_scraper.repositoryLayer.FileContentCache
import krat_os.name_scraper.repositoryLayer.GitHubFileRetriever
import krat_os.name_scraper.repositoryLayer.RateLimiter
import org.kohsuke.github.*
import kotlin.coroutines.CoroutineContext

class GitHubRepository(
    private val github: GitHub = GitHubBuilder().build(),
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) {
    private val rateLimiter = RateLimiter()
    private val cache = FileContentCache()
    private val fileRetriever = GitHubFileRetriever(rateLimiter, cache, coroutineContext)
    private val batchProcessor = BatchProcessor(fileRetriever)

    suspend fun getTopJavaRepositories(limit: Int): List<GHRepository> = withContext(coroutineContext) {
        github.searchRepositories()
            .language("java")
            .sort(GHRepositorySearchBuilder.Sort.STARS)
            .list()
            .take(limit)
            .toList()
    }

    suspend fun getJavaFiles(repo: GHRepository): List<JavaFile> = withContext(coroutineContext) {
        try {
            println("\nStarting to process repository: ${repo.fullName}")

            val defaultBranch = repo.getBranch(repo.defaultBranch)
            val tree = repo.getTreeRecursive(defaultBranch.getSHA1(), 1)

            val javaFiles = tree.tree.filter { it.path.endsWith(".java") }
            println("Found ${javaFiles.size} Java files")

            batchProcessor.processBatches(repo, javaFiles)
        } catch (e: Exception) {
            println("Error processing repository ${repo.fullName}: ${e.message}")
            emptyList()
        }
    }

    suspend fun processRepositoriesInParallel(repos: List<GHRepository>): List<Pair<GHRepository, List<JavaFile>>> =
        withContext(coroutineContext) {
            repos.map { repo ->
                async {
                    repo to getJavaFiles(repo)
                }
            }.awaitAll()
        }
}

