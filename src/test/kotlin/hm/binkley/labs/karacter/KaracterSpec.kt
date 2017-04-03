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

        beforeGroup { karacter["foo"] = "bar" }

        it("should have a value for a known key") {
            assertEquals(karacter["foo"], "bar")
        }

        it("should replace a value for a known key") {
            karacter["foo"] = "baz"
            assertEquals(karacter["foo"], "baz")
        }
    }

    describe("A committed character") {
        val karacter = Karacter()

        beforeGroup {
            karacter["foo"] = "bar"
            karacter.commit()
        }

        it("should have a value for a known key") {
            assertEquals(karacter["foo"], "bar")
        }

        it("should replace a value for a known key") {
            karacter["foo"] = "baz"
            assertEquals(karacter["foo"], "baz")
        }
    }
})
