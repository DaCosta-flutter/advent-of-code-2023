fun main() {
    val day = "24"

    data class Position3D(
        val x: Int,
        val y: Int,
        val z: Int
    )

    data class PositionDouble(
        val x: Double,
        val y: Double,
    )

    data class Velocity(
        val vx: Int,
        val vy: Int,
        val vz: Int,
    )

    data class Line(val initialPos: Position3D, val vel: Velocity)

    fun parseInput(input: List<String>) = input.map { line ->
        val (positionStr, velStr) = line.split("@").map { it.trim() }
        val (x, y, z) = positionStr.split(",")
            .let { split -> split.map { it.trim() }.map { it.toInt() } }
        val (vx, vy, vz) = velStr.split(",")
            .let { split -> split.map { it.trim() }.map { it.toInt() } }
        Line(Position3D(x, y, z), Velocity(vx, vy, vz))
    }

    fun part1(input: List<String>, pointRange: ClosedFloatingPointRange<Double>): Long {
        val lines = parseInput(input)

        fun intersectionPoint2D(line1: Line, line2: Line): PositionDouble? {
            val (x1, y1) = line1.initialPos
            val (x2, y2) = line2.initialPos
            val (vx1, vy1) = line1.vel
            val (vx2, vy2) = line2.vel

            val denCheck = (vx1 * vy2 - vx2 * vy1)
            if (denCheck == 0) {
                return null
            }
            val t2 = (x2 * vy1 - x1 * vy1 - vx1 * y2 + vx1 * y1) / (vx1 * vy2 - vx2 * vy1).toDouble()
            if (t2 < 0) {
                return null
            }
            val x = vx2 * t2 + x2
            val y = vy2 * t2 + y2
            val t1 = (y - y1) / vy1
            if (t1 < 0) {
                return null
            }
            return PositionDouble(x, y)
        }

        val pairwiseIndicesToCheck = lines.indices
            .asSequence()
            .flatMap { idx1 -> lines.indices.map { idx2 -> idx1 to idx2 } }
            .filter { (idx1, idx2) -> idx1 != idx2 }
            .map { (idx1, idx2) -> if (idx1 < idx2) idx1 to idx2 else idx2 to idx1 }
            .toSet()
        //.onEach(::println)

        return pairwiseIndicesToCheck
            .asSequence()
            .map { (idx1, idx2) -> lines[idx1] to lines[idx2] }
            //.onEach { println("Checking interception between ${it.first} and ${it.second}") }
            .mapNotNull { (line1, line2) -> intersectionPoint2D(line1, line2) }
            //.onEach { println("Intersection point is $it") }
            .filter { intersectionPoint -> intersectionPoint.x in pointRange && intersectionPoint.y in pointRange }
            .count().toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

    // Check test inputs
    val testRange = 7.0..27.0
    check(2L, part1(testInput, testRange), "Part 1")
    check(testInput.size.toLong(), part2(testInput), "Part 2")

    val range = 200_000_000_000_000.0..400_000_000_000_000.0

    val input = readInput("Day${day}")
    part1(input, range).println()
    part2(input).println()
}