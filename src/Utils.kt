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

fun <V> check(expected: V, actual: V, checkName: String? = null) {
    if (expected != actual) {
        throw Exception("Check ${checkName ?: ""} failed. Expected '$expected', but found '$actual'")
    }
}

fun findLCM(numbers: List<Long>): Long {
    return if (numbers.size == 1) {
        numbers[0]
    } else {
        numbers.fold(1) { acc, num -> findLCMTwoNums(acc, num) }
    }
}

private fun findLCMTwoNums(a: Long, b: Long) = a / findGCD(a, b) * b

fun findGCD(a: Long, b: Long): Long {
    return if (b == 0L) a else findGCD(b, a % b)
}

data class Position(
    val x: Int,
    val y: Int,
)

fun List<List<Char>>.at(pos: Position) = this[pos.y][pos.x]
fun List<String>.atPos(pos: Position) = this[pos.y][pos.x]

fun Position.inside(list: List<List<Any>>) = x > 0 && y > 0 && y < list.size && x < list[y].size

fun Set<Position>.to2dList(ch: Char = '#', nonFilled: Char = ' '): MutableList<MutableList<Char>> {
    val size = Position(this.maxOf { it.x }, this.maxOf { it.y })

    return (0..size.y).map { y ->
        (0..size.x).map { x ->
            if (Position(x, y) in this) ch else nonFilled
        }.toMutableList()
    }.toMutableList()
}


fun List<String>.toCharsByPosition(): Map<Position, Char> =
    this.indices.flatMap { y ->
        this[y].indices.map { x ->
            Position(x, y)
        }
    }.associateWith { this.atPos(it) }

fun Position.up() = this.copy(y = y - 1)
fun Position.down() = this.copy(y = y + 1)
fun Position.left() = this.copy(x = x - 1)
fun Position.right() = this.copy(x = x + 1)

fun Position.allDirections() = setOf(
    this.up(), this.down(), this.left(), this.right()
)

fun Position.cartesianDistance(other: Position): Int = abs(this.x - other.x) + abs(this.y - other.y)
