package serviceLayer

import commons.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class ClassNameAnalyzer {
    private val wordPattern = "[A-Z][a-z]+".toRegex()

    fun extractClassNames(javaFile: JavaFile): List<JavaClassName> {
        val classPattern = "class\\s+(\\w+)".toRegex()
        return classPattern.findAll(javaFile.content)
            .map { matchResult ->
                val className = matchResult.groupValues[1]
                JavaClassName(
                    fullName = className,
                    words = splitIntoWords(className)
                )
            }
            .toList()
    }

    private fun splitIntoWords(className: String): List<String> {
        return wordPattern.findAll(className)
            .map { it.value }
            .toList()
    }
}
