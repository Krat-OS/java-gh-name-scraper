package interfaceLayer

import repositoryLayer.*
import serviceLayer.*
import kotlinx.coroutines.runBlocking

class JavaNamingAnalyzer {
    private val gitHubRepo: GitHubRepository
    private val classNameAnalyzer: ClassNameAnalyzer
    private val statisticsService: NamingStatisticsService

    constructor(
        gitHubRepo: GitHubRepository = GitHubRepository(),
        classNameAnalyzer: ClassNameAnalyzer = ClassNameAnalyzer(),
        statisticsService: NamingStatisticsService = NamingStatisticsService()
    ) {
        this.gitHubRepo = gitHubRepo
        this.classNameAnalyzer = classNameAnalyzer
        this.statisticsService = statisticsService
    }

    fun analyzeTopRepositories(limit: Int = 5) = runBlocking {
        println("Fetching top $limit Java repositories...")
        val repositories = gitHubRepo.getTopJavaRepositories(limit)

        println("Processing repositories in parallel...")
        val results = gitHubRepo.processRepositoriesInParallel(repositories)

        results.forEach { (repo, javaFiles) ->
            println("\nAnalyzing repository: ${repo.fullName}")
            println("Found ${javaFiles.size} Java files")

            val classNames = javaFiles.flatMap { classNameAnalyzer.extractClassNames(it) }
            println("Found ${classNames.size} classes")

            val wordFrequencies = statisticsService.calculateWordFrequencies(classNames)
            println("\nMost common words in class names:")
            wordFrequencies.entries
                .sortedByDescending { it.value }
                .take(10)
                .forEach { (word, count) ->
                    println("$word: $count times")
                }
            println("---\n")
        }
    }
}
