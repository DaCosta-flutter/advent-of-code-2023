package y2023

import utils.println
import utils.readInput2023
import kotlin.math.min

typealias Matrix = List<List<Boolean>>

fun main() {
    val day = "13"

    fun parseInput(input: List<String>): List<Matrix> {
        val matrices = mutableListOf<Matrix>()
        var idx = 0
        val currentMatrix = mutableListOf<List<Boolean>>()
        while (idx in input.indices) {
            if (input[idx].isBlank()) {
                matrices.add(currentMatrix.toMutableList())
                currentMatrix.clear()
            } else {
                currentMatrix.add(
                    input[idx].map { it == '#' }
                )
            }
            idx++
        }
        matrices.add(currentMatrix.toMutableList())
        return matrices
    }

    fun Matrix.rotate(): Matrix {
        val rotate = mutableListOf<MutableList<Boolean>>()
        for (x in this[0].indices) {
            val line = mutableListOf<Boolean>()
            for (y in this.indices) {
                line.add(this[y][x])
            }
            rotate.add(line)
        }
        return rotate
    }

    fun Matrix.flipAt(y: Int, x: Int): Matrix {
        val newMatrix = this.map {
            it.toMutableList()
        }.toMutableList()

        newMatrix[y][x] = !newMatrix[y][x]
        return newMatrix
    }

    fun Matrix.generateWithSmudges(): Sequence<Matrix> {
        return this.indices.asSequence().flatMap { y ->
            this[y].indices.asSequence().map { x ->
                this.flipAt(y, x)
            }
        }
    }

    fun Matrix.print() {
        this.indices.forEach { y ->
            kotlin.io.println()
            this[y].indices.forEach { x ->
                print(if (this[y][x]) '#' else '.')
            }
        }
    }

    fun Matrix.reflectionIdx(toAvoid: Int = -1): Int? {
        for (i in 1..this[0].lastIndex) {
            if (i == toAvoid) {
                continue
            }
            val reflects = this
                .all { line ->
                    val numPoints = min(i, line.size - i)
                    val before = line.subList(i - numPoints, i)
                    val after = line.subList(i, i + numPoints)
                    before.reversed() == after
                }
            if (reflects) return i
        }
        return null
    }

    data class Reflection(
        val idx: Int,
        val isHorizontal: Boolean,
    )

    fun Reflection.score() = idx * if (isHorizontal) 100 else 1

    fun Matrix.reflection(): Reflection {
        val vertReflectionIdx = this.reflectionIdx()
        val horizontalReflectionIdx = this.rotate().reflectionIdx()
        return vertReflectionIdx?.let { Reflection(it, false) } ?: horizontalReflectionIdx?.let { Reflection(it, true) }!!
    }

    fun Matrix.diffReflection(origReflection: Reflection): Reflection? {
        val vertReflectionToAvoid = if (!origReflection.isHorizontal) origReflection.idx else -1
        val vertReflection = this.reflectionIdx(vertReflectionToAvoid)?.let { Reflection(it, false) }
        val horizReflectionToAvoid = if (origReflection.isHorizontal) origReflection.idx else -1
        val horizontalReflection = this.rotate().reflectionIdx(horizReflectionToAvoid)?.let { Reflection(it, true) }
        if (vertReflection != null) {
            return vertReflection
        }
        if (horizontalReflection != null) {
            return horizontalReflection
        }
        return vertReflection ?: horizontalReflection
    }

    fun part1(input: List<String>): Long {
        val matrices = parseInput(input)

        return matrices.sumOf {
            it.reflection().score().toLong()
        }
    }

    fun part2(input: List<String>): Long {
        val matrices = parseInput(input)

        return matrices.sumOf { origMatrix ->
            val origReflection = origMatrix.reflection()

            origMatrix.generateWithSmudges()
                .mapNotNull { it.diffReflection(origReflection) }
                .first().score().toLong()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput2023("Day${day}_test")

    // Check test inputs
    utils.check(405L, part1(testInput), "Part 1")
    //check(400L, part2(testInput), "Part 2")

    val input = readInput2023("Day${day}")
    part1(input).println()
    part2(input).println()
}