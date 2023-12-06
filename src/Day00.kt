
fun main() {
    val day = "00"

    fun part1(input: List<String>): Long {
        return input.size.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    val testResult = part1(testInput)

    println("Test input result: $testResult")
    check(testResult == 1234L)

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}