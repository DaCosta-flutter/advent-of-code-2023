fun main() {
    val day = "11"

    data class Universe(
        val galaxies: Set<Position>
    )

    fun Universe.sumDistanceBetweenAllGalaxies(): Long {
        val allGalaxyPairs = this.galaxies
            .flatMap { galaxy ->
                this.galaxies.map { setOf(it, galaxy) }
            }
            .toSet() // Remove duplicate pairs
            .map { it.first() to it.last() }

        return allGalaxyPairs.sumOf { (gal1, gal2) -> gal1.cartesianDistance(gal2).toLong() }
    }

    fun List<String>.col(colIdx: Int) = this.map { it[colIdx] }

    fun Position.adjustEmptiness(emptyY: Set<Int>, emptyX: Set<Int>, emptinessFactor: Int): Position {
        val yAdjustment = emptyY.count { it < y }.let { if (it > 0) (emptinessFactor - 1) * it else 0 }
        val xAdjustment = emptyX.count { it < x }.let { if (it > 0) (emptinessFactor - 1) * it else 0 }
        return Position(x + xAdjustment, y + yAdjustment)
    }

    fun parseInput(input: List<String>, emptinessFactor: Int): Universe {
        val emptyY = input.indices.filter { input[it].all { ch -> ch == '.' } }.toSet()
        val emptyX = input[0].indices.filter { input.col(it).all { ch -> ch == '.' } }.toSet()

        val galaxies = input.indices
            .flatMap { y ->
                input[y].indices.filter { x -> input[y][x] == '#' }.map { x -> Position(x, y) }
            }
            .map { it.adjustEmptiness(emptyY, emptyX, emptinessFactor) }
            .toSet()
        return Universe(galaxies)
    }

    fun part1(input: List<String>): Long = parseInput(input, emptinessFactor = 2).sumDistanceBetweenAllGalaxies()

    fun part2(input: List<String>): Long = parseInput(input, emptinessFactor = 1_000_000).sumDistanceBetweenAllGalaxies()


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

    // Check test inputs
    check(374L, part1(testInput), "Part 1")
    //check(testInput.size.toLong(), part2(testInput), "Part 2")

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}