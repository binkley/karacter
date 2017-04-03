package hm.binkley.labs.karacter

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class KaracterSpek : Spek({
    describe("A new character") {
        val karacter = Karacter()

        it("should be a map") {
            assertTrue(karacter is Map<String, Any>)
        }

        it("should contain nothing") {
            assertTrue(karacter.isEmpty())
        }
    }

    describe("A character with data") {
        val karacter = Karacter()

        beforeGroup { karacter["foo"] = 3 }

        it("should have a value for a known key") {
            assertEquals(karacter["foo"], 3)
        }
    }
})
