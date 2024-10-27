package serviceLayer

import commons.*

class NamingStatisticsService {
    fun calculateWordFrequencies(classNames: List<JavaClassName>): Map<String, Int> {
        return classNames
            .flatMap { it.words }
            .groupingBy { it }
            .eachCount()
            .toList()
            .sortedByDescending { it.second }
            .toMap()
    }
}
