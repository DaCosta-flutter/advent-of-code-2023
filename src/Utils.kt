import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun check(expected: Any, actual: Any, checkName: String? = null) {
    if (expected != actual) {
        throw Exception("Check ${checkName ?: ""} failed. Expected '$expected', but found '$actual'")
    }
}

data class Position(
    val x: Int,
    val y: Int,
)

fun Position.up() = this.copy(y = y - 1)
fun Position.down() = this.copy(y = y - 1)
fun Position.left() = this.copy(x = y - 1)
fun Position.right() = this.copy(x = y + 1)

fun Position.allDirections() = setOf(
    this.up(), this.down(), this.left(), this.right()
)

fun Position.cartesianDistance(other: Position): Int = abs(this.x - other.x) + abs(this.y - other.y)
