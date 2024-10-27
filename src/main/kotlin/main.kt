package krat_os.name_scraper

import interfaceLayer.JavaNamingAnalyzer
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    val analyzer = JavaNamingAnalyzer()
    analyzer.analyzeTopRepositories(5)
}