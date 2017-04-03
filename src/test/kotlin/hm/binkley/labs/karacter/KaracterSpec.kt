package hm.binkley.labs.karacter

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

object KaracterSpek : Spek({
    describe("A new character") {
        var karacter = Karacter()

        it("should be a map") {
            // assertTrue(karacter is Map<String, Any>)
        }

        it("should contain nothing") {
            assertTrue(karacter.isEmpty())
        }

        it("should return null for a new key") {
            assertEquals(null, karacter["foo"])
        }
    }

    describe("A character with data") {
        val karacter = Karacter()

        beforeGroup { karacter["foo"] = "bar" }

        it("should have size one") {
            assertEquals(1, karacter.size())
        }

        it("should have one key") {
            assertEquals(setOf("foo"), karacter.keys())
        }

        it("should have a value for a known key") {
            assertEquals("bar", karacter["foo"])
        }

        it("should return old value for a known key") {
            assertEquals("bar", karacter.set("foo", "baz"))
        }

        it("should replace a value for a known key") {
            karacter["foo"] = "baz"
            assertEquals("baz", karacter["foo"])
        }

        it("should know one value for a replaced known key") {
            karacter["foo"] = "baz"
            assertEquals(listOf("baz"), karacter.values("foo"))
        }
    }

    describe("A committed character") {
        val karacter = Karacter()

        beforeGroup {
            karacter["foo"] = "bar"
            karacter.commit()
        }

        beforeEachTest { karacter.clear() }

        it("should have size one") {
            assertEquals(1, karacter.size())
        }

        it("should have one key") {
            assertEquals(setOf("foo"), karacter.keys())
        }

        it("should have a value for a known key") {
            assertEquals("bar", karacter["foo"])
        }

        it("should replace a value for a known key") {
            karacter["foo"] = "baz"
            assertEquals("baz", karacter["foo"])
        }

        it("should know all values for a known key in most-recent order") {
            karacter["foo"] = "baz"
            assertEquals(listOf("baz", "bar"), karacter.values("foo"))
        }
    }
})
