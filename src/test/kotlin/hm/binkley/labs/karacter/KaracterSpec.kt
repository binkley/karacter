package hm.binkley.labs.karacter

import hm.binkley.labs.karacter.Karacter.Companion.newKaracter
import hm.binkley.labs.karacter.Karacter.EditPad
import hm.binkley.labs.karacter.Karacter.ScratchPad
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should not be`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object KaracterSpec : Spek({
    describe("A new character and edit pad") {
        val (editpad, karacter) = newKaracter(::ScratchPad)

        it("should have nothing in the character") {
            karacter.`should be empty`()
        }

        it("should have nothing in the edit pad") {
            editpad.`should be empty`()
        }

        it("should start over") {
            val newEditpad = editpad.discard(::ScratchPad)
            newEditpad `should not be` editpad
        }
    }

    describe("A character with edit pad changes") {
        val (editpad, karacter) = newKaracter(::ScratchPad)

        beforeGroup { editpad["foo"] = "bar" }

        it("should have nothing in the character") {
            karacter.`should be empty`()
        }

        it("should have one thing in the edit pad") {
            editpad `should equal` mapOf("foo" to "bar")
        }
    }

    describe("A character with a committed edit pad changes") {
        var (editpad, karacter) = newKaracter(::ScratchPad)

        beforeGroup {
            editpad["foo"] = "bar"
            editpad = editpad.keep(::ScratchPad)
            editpad["foo"] = "baz"
            editpad = editpad.keep(::ScratchPad)
        }

        it("should have one thing in the character") {
            karacter.`should equal`(mapOf("foo" to "baz"))
        }

        it("should have nothing in the edit pad") {
            editpad.`should be empty`()
        }

        it("should know all values for a key in the character") {
            karacter.values<String>("foo") `should equal` listOf("baz", "bar")
        }
    }

    describe("A view of a character with mixed changes") {
        var (editpad, _) = newKaracter(::ScratchPad)

        beforeGroup {
            editpad["foo"] = "bar"
            editpad = editpad.keep(::ScratchPad)
            editpad["foo"] = "baz"
        }

        it("should have a view as if committed") {
            editpad.whatIf() `should equal` mapOf("foo" to "baz")
        }
    }

    describe("A character with several string values") {
        var (editpad, karacter) = newKaracter(::ScratchPad)

        beforeGroup {
            editpad["foo"] = "bar"
            editpad = editpad.keep(::ScratchPad)
            editpad["foo"] = "baz"
            editpad = editpad.keep(::ScratchPad)
        }

        it("should have all values in character in reverse insert order") {
            karacter.values<String>("foo") `should equal` listOf("baz", "bar")
        }

        it("should have most recent value in character") {
            karacter `should equal` mapOf("foo" to "baz")
        }
    }

    describe("A character with several integer values") {
        var (editpad, karacter) = newKaracter(::ScratchPad)

        beforeGroup {
            karacter.rule("foo") { karacter, key ->
                karacter.values<Int>(key).sum()
            }
            editpad["foo"] = 3
            editpad = editpad.keep(::ScratchPad)
            editpad["foo"] = 4
            editpad = editpad.keep(::ScratchPad)
        }

        it("should have all values in character in reverse insert order") {
            karacter.values<Int>("foo") `should equal` listOf(4, 3)
        }

        it("should have summed value in character") {
            karacter `should equal` mapOf("foo" to 7)
        }
    }

    describe("A custom edit pad") {
        class TestPad(karacter: Karacter) : EditPad<TestPad>(karacter) {
            init {
                put("foo", "bar")
            }
        }

        val (editpad, karacter) = newKaracter(::TestPad)
        beforeGroup { editpad.keep(::ScratchPad) }

        it("should behave like a vanilla edit pad") {
            karacter `should equal` mapOf("foo" to "bar")
        }
    }
})
