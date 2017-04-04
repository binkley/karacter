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

        it("should have right thing in the edit pad") {
            assertTrue(editpad.containsKey("foo"))
        }
    }

    describe("A character with a committed edit pad changes") {
        var (editpad, karacter) = makeKaracter()

        beforeGroup {
            editpad["foo"] = "bar"
            editpad = editpad.commit()
            editpad["foo"] = "baz"
            editpad = editpad.commit()
        }

        it("should have one thing in the character") {
            assertEquals(1, karacter.size)
        }

        it("should have right thing in the character") {
            assertTrue(karacter.containsKey("foo"))
        }

        it("should have nothing in the edit pad") {
            assertTrue(editpad.isEmpty())
        }

        it("should know all values for a key in the character") {
            assertEquals(listOf("baz", "bar"), karacter.values<String>("foo"))
        }
    }

    describe("A character with several string values") {
        var (editpad, karacter) = makeKaracter()

        beforeGroup {
            editpad["foo"] = "bar"
            editpad = editpad.commit()
            editpad["foo"] = "baz"
            editpad = editpad.commit()
        }

        it("should have all values in character in reverse insert order") {
            assertEquals(listOf("baz", "bar"), karacter.values<String>("foo"))
        }

        it("should have most recent value in character") {
            assertEquals("baz", karacter["foo"])
        }
    }

    describe("A character with several integer values") {
        var (editpad, karacter) = makeKaracter()

        beforeGroup {
            karacter.rule("foo") { karacter, key ->
                karacter.values<Int>(key).sum()
            }
            editpad["foo"] = 3
            editpad = editpad.commit()
            editpad["foo"] = 4
            editpad = editpad.commit()
        }

        it("should have all values in character in reverse insert order") {
            assertEquals(listOf(4, 3), karacter.values<Int>("foo"))
        }

        it("should have summed value in character") {
            assertEquals(7, karacter["foo"])
        }
    }
})
