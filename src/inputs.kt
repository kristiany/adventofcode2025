import java.io.File
import java.nio.file.Paths

fun <T> forLinesIn(path: String, consumer: (Sequence<String>) -> T): T {
    return File(Paths.get("inputs/$path").toAbsolutePath().toString()).useLines(block=consumer)
}