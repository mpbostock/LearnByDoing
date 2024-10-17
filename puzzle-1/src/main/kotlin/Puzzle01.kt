package mpbostock

import java.io.File
import kotlin.time.measureTimedValue

object Puzzle01 {
    fun interface DuplicateFinder {
        fun findDuplicates(lines: Sequence<Pair<String, String>>): List<String>
    }

    data object GroupByDuplicateFinder : DuplicateFinder {
        override fun findDuplicates(lines: Sequence<Pair<String, String>>): List<String> {
            return lines.groupingBy { it.second }
                .fold(emptyList<String>()) { acc, (serial, _) ->
                    acc + serial
                }.map { it.value }
                .firstOrNull { it.size > 1 } ?: emptyList()
        }
    }

    data class ForEachDuplicateFinder(
        private val existing: HashMap<String, String> = hashMapOf()
    ) : DuplicateFinder {
        override fun findDuplicates(lines: Sequence<Pair<String, String>>): List<String> {
            lines.forEach { pair ->
                val (serial, client) = pair
                val existingSerial = existing.put(client, serial)
                existingSerial?.let {
                    return listOf(it, serial)
                }
            }
            return emptyList()
        }

        override fun toString(): String {
            return "ForEachDuplicateFinder"
        }
    }

    data class FoldDuplicateFinder(
        private val existing: HashMap<String, String> = hashMapOf()
    ) : DuplicateFinder {
        override fun findDuplicates(lines: Sequence<Pair<String, String>>): List<String> {
            return lines.fold(listOf()) { acc, pair ->
                val (serial, client) = pair
                val existingSerial = existing.put(client, serial)
                when {
                    existingSerial != null -> listOf(existingSerial, serial)
                    else -> acc
                }
            }
        }

        override fun toString(): String {
            return "FoldDuplicateFinder"
        }
    }

    data object SortingDuplicateFinder : DuplicateFinder {
        override fun findDuplicates(lines: Sequence<Pair<String, String>>): List<String> {
            val empty = emptyList<String>()
            return lines.sortedBy { it.second }
                .zipWithNext { a, b -> if (a.second == b.second) listOf(a.first, b.first) else empty }
                .firstOrNull { it.isNotEmpty() } ?: empty
        }
    }

    fun findDuplicateSerials(
        filename: String,
        algorithm: DuplicateFinder
    ): List<String> {
        File(filename).useLines { lines ->
            val (duplicates, timeTaken) = measureTimedValue {
                algorithm.findDuplicates(lines.drop(1).map { line ->
                    val serial = line.substring(0, 19)
                    val client = line.substring(21)
//                    val (serial, client) = line.split(',')
                    serial to client
                })
            }
            println("Time taken: $timeTaken")
            println("Duplicates: $duplicates")
            return duplicates
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        data class Puzzle(val description: String, val path: String)

        val puzzles = listOf(
            Puzzle("Standard", "puzzle-1/src/main/kotlin/puzzle.csv"),
            Puzzle("Bonus", "puzzle-1/src/main/kotlin/puzzle_bonus.csv"),
        )
        for (puzzle in puzzles) {
            for (algorithm in allAlgorithms()) {
                println("Running ${puzzle.description} puzzle using $algorithm.")
                findDuplicateSerials(puzzle.path, algorithm)
            }
        }
    }

    fun allAlgorithms() = listOf(
        GroupByDuplicateFinder,
        ForEachDuplicateFinder(),
        FoldDuplicateFinder(),
        SortingDuplicateFinder,
    )
}