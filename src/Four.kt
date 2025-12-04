
fun main() {
    forLinesIn("day04/input.txt") { input ->
        val map = input.toList()
        val rolls = HashSet<Pos>()
        for (y in map.indices) {
            for (x in 0..<map[y].length) {
                if (map[y][x] == '@') {
                    rolls.add(Pos(x, y))
                }
            }
        }
        val rems = getRemovables(rolls, map[0].length, map.size)
        println("Part 1: ${rems.size}")

        val initSize = rolls.size
        for (i in 0..1000) { // Sanity check iterations
            val rems = getRemovables(rolls, map[0].length, map.size)
            if (rems.isEmpty()) {
                break
            }
            //println("$i removing ${rems.size}")
            rolls.removeAll(rems)
        }
        println("Part 2: ${initSize - rolls.size}")
    }
}

fun getRemovables(map: Set<Pos>, w: Int, h: Int): HashSet<Pos> {
    val result = HashSet<Pos>()
    for (p in map) {
        if (adjLessThan4(map, p, w, h)) {
            result.add(p)
        }
    }
    return result
}

fun adjLessThan4(rolls: Set<Pos>, p: Pos, w: Int, h: Int): Boolean {
    var adjs = 0
    if (p.x > 0) {
        if (p.y > 0 && rolls.contains(Pos(p.x - 1, p.y - 1))) ++adjs
        if (rolls.contains(Pos(p.x - 1, p.y))) ++adjs
        if (p.y < h - 1 && rolls.contains(Pos(p.x - 1, p.y + 1))) ++adjs
    }
    if (p.y > 0 && rolls.contains(Pos(p.x, p.y - 1))) ++adjs
    if (p.y < h - 1 && rolls.contains(Pos(p.x, p.y + 1))) ++adjs
    if (p.x < w - 1) {
        if (p.y > 0 && rolls.contains(Pos(p.x + 1, p.y - 1))) ++adjs
        if (rolls.contains(Pos(p.x + 1, p.y))) ++adjs
        if (p.y < h - 1 && rolls.contains(Pos(p.x + 1, p.y + 1))) ++adjs
    }
    return adjs < 4
}

data class Pos(val x: Int, val y: Int) {
}

