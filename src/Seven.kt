fun main() {
    forLinesIn("day07/input.txt") { input ->
        val map = input.toList()
        val sx = map[0].indexOf('S')
        val beams = HashSet<Beam>()
        beams.add(Beam(sx, 1))
        var splits = 0
        for (y in 2..<map.size) {
            for (b in beams.filter { it.y == y - 1 }) {
                if (map[y][b.x] == '^') {
                    if (b.x > 0) beams.add(Beam(b.x - 1, y))
                    if (b.x < map[y].length - 1) beams.add(Beam(b.x + 1, y))
                    ++splits
                }
                else {
                    beams.add(Beam(b.x, y))
                }
            }
        }
        println("Part 1: $splits")

        var timelines = 0L //beams.count { it.y == map.size - 1 }
        val maxy = map.size - 1
        var it = 1
        for (b in beams.filter { it.y == maxy }) {
            println("it $it / ${beams.count { it.y == maxy }}, start beam $b")
            timelines += countPathsRec(HashMap(), b.x, maxy, beams, map)
            ++it
        }
        println("Part 2: $timelines")
    }
}

private data class Beam(val x: Int, val y: Int)

private data class Key(val x: Int, val y: Int)
// DFS
private fun countPathsRec(cache: HashMap<Key, Long>, x: Int, y: Int, beams: Set<Beam>, map: List<String>): Long {
    // Top-most beam reached
    if (y == 1) {
        return 1L
    }
    var result = 0L
    if (x > 0 && map[y][x - 1] == '^') {
        result += getCount(cache, x - 1, y - 1, beams, map)
    }
    if (beams.contains(Beam(x, y - 1))) {
        result += getCount(cache, x, y - 1, beams, map)
    }
    if (x < map[y].length - 1 && map[y][x + 1] == '^') {
        result += getCount(cache, x + 1, y - 1, beams, map)
    }
    return result
}

private fun getCount(
    cache: HashMap<Key, Long>,
    x: Int,
    y: Int,
    beams: Set<Beam>,
    map: List<String>
): Long {
    if (cache.containsKey(Key(x, y))) {
        return cache[Key(x, y)]!!
    }
    val c = countPathsRec(cache, x, y, beams, map)
    cache[Key(x, y)] = c
    return c
}
