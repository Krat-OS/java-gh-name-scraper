package commons

data class JavaFile(
    val name: String,
    val content: String,
    val path: String
)

data class JavaClassName(
    val fullName: String,
    val words: List<String>
)
