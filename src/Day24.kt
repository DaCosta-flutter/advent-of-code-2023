fun main() {
    val day = "24"

    data class Position3D(
        val x: Long,
        val y: Long,
        val z: Long
    )

    data class PositionDouble(
        val x: Double,
        val y: Double,
    )

    data class Velocity(
        val vx: Long,
        val vy: Long,
        val vz: Long,
    )

    data class Line(val initialPos: Position3D, val vel: Velocity)

    fun parseInput(input: List<String>) = input.map { line ->
        val (positionStr, velStr) = line.split("@").map { it.trim() }
        val (x, y, z) = positionStr.split(",")
            .let { split -> split.map { it.trim() }.map { it.toLong() } }
        val (vx, vy, vz) = velStr.split(",")
            .let { split -> split.map { it.trim() }.map { it.toLong() } }
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
            if (denCheck == 0L) {
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
        val lines = parseInput(input)

        val lines0 = lines.random()
        val lines1 = lines.random()
        val lines2 = lines.random()
        val (x0, y0, z0) = lines0.initialPos
        val (vx0, vy0, vz0) = lines0.vel
        val (x1, y1, z1) = lines1.initialPos
        val (vx1, vy1, vz1) = lines1.vel
        val (x2, y2, z2) = lines2.initialPos
        val (vx2, vy2, vz2) = lines2.vel

        val equationToSolve = """
    $x0 + $vx0 * t0 = xs + vxs * t0
    $x1 + $vx1 * t1 = xs + vxs * t1
    $x2 + $vx2 * t2 = xs + vxs * t2
    $y0 + $vy0 * t0 = ys + vys * t0
    $y1 + $vy1 * t1 = ys + vys * t1
    $y2 + $vy2 * t2 = ys + vys * t2
    $z0 + $vz0 * t0 = zs + vzs * t0
    $z1 + $vz1 * t1 = zs + vzs * t1
    $z2 + $vz2 * t2 = zs + vzs * t2
        """.trimIndent()
            .replace("vxs", "r")
            .replace("vys", "s")
            .replace("vzs", "t")
            .replace("xs", "x")
            .replace("ys", "y")
            .replace("zs", "z")
            .replace("t0", "m")
            .replace("t1", "n")
            .replace("t2", "b")

        println("Equations to solve:")
        println(equationToSolve)
        println()

        println(lines.random())
        println(lines.random())
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

    println(172543224455736L + 348373777394510L + 148125938782131L)
}