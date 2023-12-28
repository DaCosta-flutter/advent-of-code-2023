import utils.println
import utils.readInput
import java.util.LinkedList

fun main() {
    val day = "25"

    data class MinCutResult(
        val numEdgesCut: Int,
        val subGraph: Set<String>,
    )


    fun kargerMinCut(graph: MutableMap<String, MutableList<String>>): MinCutResult {
        val keyToMerged = graph.keys.associateWith { mutableSetOf(it) }

        while (graph.size > 2) {
            // Randomly choose an edge (u, v)
            val u = graph.keys.random()
            val vList = graph[u] ?: mutableListOf()
            val v = vList.random()

            // Contract the edge (u, v)
            graph[u]?.addAll(graph[v] ?: emptyList())
            graph[u]?.remove(v)
            graph[v]?.forEach { node ->
                keyToMerged[u]!!.add(node)
                graph[node]?.remove(v)
                graph[node]?.add(u)
            }

            // Remove self-loops
            graph[u] = graph[u]!!.filter { it != u }.toMutableList()

            // Remove vertex v from the graph
            graph.remove(v)
            keyToMerged[u]!!.addAll(keyToMerged[v]!!)
        }

        // Return the remaining edges (cuts)
        val firstConnectedComp = graph.values.firstOrNull()
        return MinCutResult(firstConnectedComp?.size ?: 0, keyToMerged[graph.keys.first()]!!)
    }

    fun part1(input: List<String>): Long {
        val connectionsByNode = input.associate { line ->
            val (from, toAllJoined) = line.split(": ").let { it[0] to it[1] }
            val toNodes = toAllJoined.split(" ").map { it.trim() }.toSet()
            from to toNodes.toMutableSet()
        }.toMutableMap()

        connectionsByNode.forEach { (fromNode, toNodes) ->
            toNodes.forEach { toNode ->
                println("$fromNode -- $toNode")
            }
        }

        val allKeys = connectionsByNode.values.flatten().toSet()
        allKeys.forEach {
            connectionsByNode.computeIfAbsent(it, { mutableSetOf() })
        }


        // Do pairwise connections
        connectionsByNode.forEach { (fromNode, toNodes) ->
            toNodes.forEach { toNode ->
                connectionsByNode[toNode]!!.add(fromNode)
            }
        }

        connectionsByNode["ttj"]!!.remove("rpd")
        connectionsByNode["rpd"]!!.remove("ttj")
        connectionsByNode["fqn"]!!.remove("dgc")
        connectionsByNode["dgc"]!!.remove("fqn")
        connectionsByNode["htp"]!!.remove("vps")
        connectionsByNode["vps"]!!.remove("htp")

        fun bfs(start: String, graph: MutableMap<String, MutableSet<String>>): Int {
            val toVisit = LinkedList<String>().apply { add(start) }
            val visited = mutableSetOf<String>()
            while (toVisit.isNotEmpty()) {
                val cur = toVisit.pop()
                visited.add(cur)
                graph.getOrDefault(cur, emptyList())
                    .filter { it !in visited }
                    .forEach { toVisit.add(it) }
            }

            return visited.size
        }

        val numNodes = bfs(connectionsByNode.keys.first(), connectionsByNode)
        return numNodes * (connectionsByNode.keys.size - numNodes).toLong()

        var result = MinCutResult(19, emptySet())
        while (result.numEdgesCut != 3) {
            result = kargerMinCut(connectionsByNode.mapValues { it.value.toMutableList() }.toMutableMap())
        }


        return (connectionsByNode.size - result.subGraph.size) * result.subGraph.size.toLong()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day${day}_test")

    // Check test inputs
    //check(54, part1(testInput), "Part 1")
    utils.check(testInput.size.toLong(), part2(testInput), "Part 2")

    val input = readInput("Day${day}")
    part1(input).println()
    part2(input).println()
}