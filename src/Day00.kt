fun main() {
    val day = "00"

    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    val testResult = part1(testInput)

    println("Test input result: $testResult")
    check(testResult == 1234)

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}
