import utils.findLCM
import utils.println
import utils.readInput

enum class Direction { LEFT, RIGHT }

fun main() {
    val day = "08"

    class Node(
        val name: String,
        val leftNode: String,
        val rightNode: String,
    )

    data class Network(
        val instructions: List<Direction>,
        val nodes: Map<String, Node>,
    )

    fun Network.instructionIdxAt(numSteps: Long): Int = (numSteps % instructions.size).toInt()

    fun Network.instructionAt(numSteps: Long): Direction = instructions[this.instructionIdxAt(numSteps)]

    fun Node.getNode(direction: Direction, nodes: Map<String, Node>): Node {
        val nodeStr = if (direction == Direction.LEFT) this.leftNode else this.rightNode
        return nodes[nodeStr]!!
    }

    fun parseInput(input: List<String>): Network {
        val instructions = input[0].map { if (it == 'L') Direction.LEFT else Direction.RIGHT }

        val nodes = input.subList(2, input.size)
            .map { nodeStr ->
                val (nodeName, connectedNodesStr) = nodeStr.split(" = ").let {
                    it[0].trim() to it[1].removePrefix("(").removeSuffix(")")
                }
                val (leftNode, rightNode) = connectedNodesStr.split(", ").let {
                    it[0] to it[1]
                }
                Node(
                    nodeName,
                    leftNode,
                    rightNode
                )
            }
            .associateBy { it.name }
        return Network(instructions, nodes)
    }

    fun Node.numStepsToNodeEndingWith(nodeNameEnding: String, network: Network): Long {
        var countSteps = 0L
        var curNode = this

        while (!curNode.name.endsWith(nodeNameEnding)) {
            curNode = curNode.getNode(network.instructionAt(countSteps), network.nodes)
            countSteps++
        }

        return countSteps
    }

    fun part1(input: List<String>): Long {
        val network = parseInput(input)
        val startNode = network.nodes["AAA"]!!
        return startNode.numStepsToNodeEndingWith("ZZZ", network)
    }

    fun part2(input: List<String>): Long {
        val network = parseInput(input)

        return network.nodes.values
            .filter { it.name.endsWith("A") }
            .map { it.numStepsToNodeEndingWith("Z", network) }
            .let { findLCM(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")
    val testResult = part2(testInput)

    println("Test input result: $testResult")
    check(testResult == 6L)

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}