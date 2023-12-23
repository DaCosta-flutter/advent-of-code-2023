import java.util.LinkedList

typealias Id = Int

fun main() {
    val day = "22"

    data class Block(
        val id: Id,
        val x: IntRange,
        val y: IntRange,
        val z: IntRange,
        val supportedBy: MutableSet<Id> = mutableSetOf(),
        val supporting: MutableSet<Id> = mutableSetOf(),
    )

    fun Block.intersectsOnXY(other: Block) = (this.x intersect other.x).isNotEmpty() && (this.y intersect other.y).isNotEmpty()

    fun IntRange.size() = this.last - this.first + 1

    fun Block.settle(alreadySettledBlocksByZ: Map<Int, List<Block>>): Block {
        var currentZ = z.first
        val supportedBy = mutableSetOf<Int>()
        while (supportedBy.isEmpty() && currentZ > 1) {
            val blocksByZ = alreadySettledBlocksByZ.getOrDefault(currentZ - 1, emptyList())
            blocksByZ
                .forEach { otherBlock ->
                    if (this.intersectsOnXY(otherBlock)) {
                        supportedBy.add(otherBlock.id)
                        otherBlock.supporting.add(this.id)
                    }
                }
            if (supportedBy.isEmpty()) {
                currentZ--
            }
        }

        return this.copy(z = currentZ until (currentZ + z.size()), supportedBy = supportedBy)
    }

    fun parseBlocks(input: List<String>) = input.indices.map { i ->
        val (xStart, yStart, zStart) = input[i].split("~")[0].split(",").let {
            Triple(it[0].toInt(), it[1].toInt(), it[2].toInt())
        }
        val (xEnd, yEnd, zEnd) = input[i].split("~")[1].split(",").let {
            Triple(it[0].toInt(), it[1].toInt(), it[2].toInt())
        }
        Block(i, xStart..xEnd, yStart..yEnd, zStart..zEnd)
    }

    fun List<Block>.settleAll(): Map<Id, Block> {
        val blocksByZ = this.groupBy { it.z.first }.toSortedMap()
        val settledBlocksByLastZ = mutableMapOf<Int, MutableList<Block>>()

        blocksByZ.asSequence().flatMap { it.value }.forEach { block ->
            val settledBlock = block.settle(settledBlocksByLastZ)
            settledBlocksByLastZ.computeIfAbsent(settledBlock.z.last, { mutableListOf() }).add(settledBlock)
        }
        return settledBlocksByLastZ.values.flatten().associateBy { it.id }
    }

    fun Block.numFallenInDesintegration(settledBlocksById: Map<Id, Block>): Int {
        val fallen = mutableSetOf<Id>().apply { add(id) }
        val toCheckThatWillFall = LinkedList<Id>().apply {
            addAll(supporting)
        }

        while (toCheckThatWillFall.isNotEmpty()) {
            val blockToCheck = settledBlocksById[toCheckThatWillFall.pop()]!!
            if (blockToCheck.supportedBy.all { supportedBlockId -> supportedBlockId in fallen }) {
                fallen.add(blockToCheck.id)
                toCheckThatWillFall.addAll(blockToCheck.supporting)
            }
        }

        return fallen.size - 1
    }

    fun part1(input: List<String>): Long {
        val blocks = parseBlocks(input)

        val settledBlockById = blocks.settleAll()

        return settledBlockById.count { (_, settledBlock) ->
            settledBlock.supporting.all { supportedBlockId -> settledBlockById[supportedBlockId]!!.supportedBy.size > 1 }
        }.toLong()
    }

    fun part2(input: List<String>): Long {
        val blocks = parseBlocks(input)

        val settledBlockById = blocks.settleAll()

        settledBlockById[5]!!.numFallenInDesintegration(settledBlockById)
            //.also { println("Desintegrating block F would cause $it fallen blocks") }
        settledBlockById[0]!!.numFallenInDesintegration(settledBlockById)
           // .also { println("Desintegrating block A would cause $it fallen blocks") }
        return settledBlockById.values.sortedBy { it.z.last }
            .sumOf { it.numFallenInDesintegration(settledBlockById).toLong() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

    // Check test inputs
    check(5L, part1(testInput), "Part 1")
    check(7L, part2(testInput), "Part 2")

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}