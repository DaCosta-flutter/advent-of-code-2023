package utils.geometry

import kotlin.math.abs

data class Position(
    val x: Int,
    val y: Int,
)

operator fun Position.rem(sizeToCorrect: Position): Position {
    var newX = x % sizeToCorrect.x
    var newY = y % sizeToCorrect.y

    if (newX < 0) {
        newX += sizeToCorrect.x
    }
    if (newY < 0) {
        newY += sizeToCorrect.y
    }

    return this.copy(newX, newY)
}

operator fun List<List<Char>>.get(pos: Position) = this[pos.y][pos.x]
fun List<String>.atPos(pos: Position) = this[pos.y][pos.x]

fun Position.isInside(list: List<List<Any>>) = x > 0 && y > 0 && y < list.size && x < list[y].size

fun Set<Position>.to2dList(ch: Char = '#', nonFilled: Char = ' '): MutableList<MutableList<Char>> {
    val size = Position(this.maxOf { it.x }, this.maxOf { it.y })

    return (0..size.y).map { y ->
        (0..size.x).map { x ->
            if (Position(x, y) in this) ch else nonFilled
        }.toMutableList()
    }.toMutableList()
}

fun Position.up() = this.copy(y = y - 1)
fun Position.down() = this.copy(y = y + 1)
fun Position.left() = this.copy(x = x - 1)
fun Position.right() = this.copy(x = x + 1)

fun Position.cartesianNeighbours() = setOf(
    this.up(), this.down(), this.left(), this.right()
)

fun Position.neighbours(): Set<Position> = (-1..1)
    .flatMap { xInc -> (-1..1).map { yInc -> Position(this.x + xInc, this.y + yInc) } }
    .filter { it.x >= 0 && it.y >= 0 }
    .filterNot { it == this }
    .toSet()

//fun Position.lineTo(other: Position) =

fun Position.cartesianDistance(other: Position): Int = abs(this.x - other.x) + abs(this.y - other.y)

enum class Direction2D {
    UP, DOWN, RIGHT, LEFT
}

fun Position.moveTo(num: Int, d: Direction2D) = when (d) {
    Direction2D.UP -> copy(y = y - num)
    Direction2D.DOWN -> copy(y = y + num)
    Direction2D.LEFT -> copy(x = x - num)
    Direction2D.RIGHT -> copy(x = x + num)
}

fun Position.lineTo(num: Int, d: Direction2D): List<Position> = buildList {
    var currentPos = this@lineTo
    repeat(num) {
        currentPos = when (d) {
            Direction2D.UP -> currentPos.up()
            Direction2D.DOWN -> currentPos.down()
            Direction2D.RIGHT -> currentPos.right()
            Direction2D.LEFT -> currentPos.left()
        }
        add(currentPos)
    }
}