package hm.binkley.labs.karacter

import hm.binkley.labs.karacter.Karacter.Companion.makeKaracter
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

object KaracterSpec : Spek({
    describe("A new character and edit pad") {
        val (editpad, karacter) = makeKaracter()

        it("should have a map edit pad") {
             assertTrue(editpad is Map<String, Any>)
        }

        it("should have nothing in the character") {
            assertTrue(karacter.isEmpty())
        }

        it("should have nothing in the edit pad") {
            assertTrue(editpad.isEmpty())
        }
    }

    describe("A character with edit pad changes") {
        val (editpad, karacter) = makeKaracter()

        beforeGroup { editpad["foo"] = "bar" }

        it("should have nothing in the character") {
            assertTrue(karacter.isEmpty())
        }

        it("should have one thing in the edit pad") {
            assertEquals(1, editpad.size)
        }
    }

    describe("A character with a committed edit pad") {
        var (editpad, karacter) = makeKaracter()

        beforeGroup {
            editpad["foo"] = "bar"
            editpad = editpad.commit()
        }

        it("should have one thing in the character") {
            assertEquals(1, karacter.size)
        }

        it("should have nothing in the edit pad") {
            assertTrue(editpad.isEmpty())
        }

        it("should know all values for a key in the character") {
            assertEquals(listOf("bar"), karacter.values("foo"))
        }
    }
})
