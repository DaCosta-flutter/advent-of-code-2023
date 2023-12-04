fun main() {
    val day = "03"

    data class Point(
        val x: Int,
        val y: Int
    )

    fun Point.adjs(): Set<Point> = (-1..1)
        .flatMap { xInc -> (-1..1).map { yInc -> Point(this.x + xInc, this.y + yInc) } }
        .filter { it.x >= 0 && it.y >= 0 }
        .filterNot { it == this }
        .toSet()

    fun Point.isValidPos(input: List<String>) = this.y < input.size && this.x < input[this.y].length && this.y >= 0 && this.x >= 0

    fun Point.next() = this.copy(x = this.x + 1)

    fun List<String>.at(p: Point) = this[p.y][p.x]

    fun getNumber(startPosition: Point, input: List<String>): Int {
        var isValid = false
        var currentVal = 0
        var numDigits = 0
        var currentPoint = startPosition

        while (currentPoint.isValidPos(input) && input.at(currentPoint).isDigit()) {
            currentVal = input.at(currentPoint).digitToInt() + currentVal * 10
            isValid = isValid || currentPoint.adjs()
                .filter { it.isValidPos(input) }
                .any { adjPoint -> input.at(adjPoint).let { !it.isDigit() && it != '.' } }
            currentPoint = currentPoint.next()
            numDigits++
        }

        return if (isValid) currentVal else 0
    }

    fun parseNumbers(input: List<String>): List<Int> {
        var currentPos = Point(0, 0)
        val listOfNums = mutableListOf<Int>()

        input.forEach {
            while (currentPos.isValidPos(input)) {
                val num = getNumber(currentPos, input)
                if (num != 0) {
                    listOfNums.add(num)
                }
                repeat(num.toString().length) {
                    currentPos = currentPos.next()
                }
            }
            currentPos = currentPos.copy(0, currentPos.y + 1)
        }
        return listOfNums
    }

    fun part1(input: List<String>): Int {
        return parseNumbers(input)
            .sum()
    }

    fun findGears(input: List<String>): List<Point> {
        return input.indices.flatMap { y ->
            input[y].indices.map { x ->
                Point(x, y)
            }
        }
            .filter { input.at(it) == '*' }
    }

    fun findFromMiddleNumber(startPos: Point, input: List<String>): Int {
        var currentPos = startPos
        var prevPos = currentPos.copy(x = currentPos.x - 1)
        while (prevPos.isValidPos(input) && input.at(prevPos).isDigit()) {
            currentPos = prevPos
            prevPos = currentPos.copy(x = currentPos.x - 1)
        }

        return getNumber(currentPos, input)
    }

    fun findGearMultiplier(gearPoint: Point, input: List<String>): Int {
        val toMultiply = gearPoint.adjs()
            .filter { it.isValidPos(input) }
            .filter { input.at(it).isDigit() }
            .map { findFromMiddleNumber(it, input) }
            .toSet()

        return if (toMultiply.size <= 1) 0 else toMultiply.first() * toMultiply.last()

    }

    fun part2(input: List<String>): Int {
        return findGears(input)
            .map { findGearMultiplier(it, input) }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    val testResult = part2(testInput)

    println("Test input: $testResult")
    check(testResult == 467835)

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}
