package mpbostock

import mpbostock.Puzzle01.allAlgorithms
import mpbostock.Puzzle01.findDuplicateSerials
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class Puzzle01Test {
    @Test
    fun `input with no duplicates returns no duplicates`() {
        for (algorithm in allAlgorithms()) {
            val duplicates = findDuplicateSerials("puzzle-1/src/test/kotlin/test-input-no-dupes.csv", algorithm)
            assertTrue(duplicates.isEmpty())
        }
    }
    @Test
    fun `input with duplicates returns duplicate serials`() {
        for (algorithm in allAlgorithms()) {
            val duplicates = findDuplicateSerials("puzzle-1/src/test/kotlin/test-input.csv", algorithm)
            assertEquals(listOf("6449 1XQD 3JNY 7G23", "6441 3YP9 ZMBX VGA5"), duplicates)
        }
    }
}