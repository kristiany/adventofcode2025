
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

fun main() {
    forLinesIn("day11/input.txt") { input ->

        val graph = input.map { line ->
            val name = line.split(":")[0]
            val outs = line.split(":")[1].trim().split(" ").toSet()
            name to outs
        }.toMap()

        println("graph $graph")
        val allPaths = ArrayList<ArrayList<String>>()
        dfs("you", "out", graph, ArrayList(), allPaths)
        println("Part 1: ${allPaths.size}")

        val elapsed = measureTimeMillis {
            val memo = HashMap<String, Long>()
            val svrdac = dfsCount("svr",
                "dac", graph,
                memo)
            val svrfft = dfsCount("svr",
                "fft", graph,
                memo)
            val dacfft = dfsCount("dac",
                "fft", graph,
                memo)
            val fftdac = dfsCount("fft",
                "dac", graph,
                memo)
            val fftout = dfsCount("fft",
                "out", graph,
                memo)
            val dacout = dfsCount("dac",
                "out", graph,
                memo)
            println("Part 2: ${svrdac * dacfft * fftout + svrfft * fftdac * dacout}")
        }
        println("time: ${elapsed.milliseconds.toString(DurationUnit.SECONDS, 1)}")
    }

}

/*
    DFS & BFS
    https://www.geeksforgeeks.org/dsa/find-paths-given-source-destination/
 */
fun dfs(
    src: String,
    dest: String,
    graph: Map<String, Set<String>>,
    path: ArrayList<String>,
    allPaths: ArrayList<ArrayList<String>>
): ArrayList<ArrayList<String>> {
    //println("path $path")
    path.add(src)
    //println("src $src -> dest $dest")
    if (src == dest) {
        allPaths.add(ArrayList(path))
    } else if (graph.contains(src)) {
        for (adjNode in graph.get(src)!!) {
            dfs(adjNode, dest, graph, path, allPaths)
        }
    }
    path.removeAt(path.size - 1)
    return allPaths
}

fun dfsCount(
    src: String,
    dest: String,
    graph: Map<String, Set<String>>,
    memo: MutableMap<String, Long>
): Long {
    if (src == dest) {
        return 1
    }
    if (memo.containsKey("$src-$dest")) {
        return memo["$src-$dest"]!!
    }
    var pathCount = 0L
    if (graph.contains(src)) {
        for (adjNode in graph.get(src)!!) {
            pathCount += dfsCount(adjNode, dest, graph, memo)
        }
    }
    memo["$src-$dest"] = pathCount
    return pathCount
}
