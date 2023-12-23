fun main() {
    val day = "21"

    fun part1(input: List<String>): Long {
        val charByPosition = input.toCharsByPosition()
        charByPosition.println()

        val startPosition = charByPosition.filterValues { it == 'S' }.map { it.key }.first().also { it.println() }
        var curNumSteps = 0
        val validPosByNumSteps = mutableListOf(mutableSetOf(startPosition))

        while (curNumSteps <= 64) {
            validPosByNumSteps.add(mutableSetOf())
            validPosByNumSteps[curNumSteps].flatMap { it.allDirections() }
                .filter { it in charByPosition && charByPosition[it] == '.' || charByPosition[it] == 'S' }
                .forEach { validPos ->
                    validPosByNumSteps[curNumSteps + 1].add(validPos)
                }
            curNumSteps++
        }

        (0..6)
            .forEach { println("Iter $it, numPositions: ${validPosByNumSteps[it].size} possible visited = ${validPosByNumSteps[it]}") }

        return validPosByNumSteps[64].size.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

    // Check test inputs
    check(42L, part1(testInput), "Part 1")
    check(testInput.size.toLong(), part2(testInput), "Part 2")

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}