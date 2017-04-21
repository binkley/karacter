package hm.binkley.labs.karacter

import hm.binkley.labs.karacter.Karacter.Companion.mostRecent
import hm.binkley.labs.karacter.Karacter.Companion.newKaracter
import hm.binkley.labs.karacter.Karacter.EditPad
import hm.binkley.labs.karacter.Karacter.Rule
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be`
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

        it("should display nicely") {
            karacter.toString() `should equal` "All (0): {}"
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

    describe("A character with a multiple committed edit pad changes") {
        var (editpad, karacter) = newKaracter(::ScratchPad)

        beforeGroup {
            editpad["foo"] = "bar"
            editpad["fruit"] = "apple"
            editpad = editpad.keep(::ScratchPad)
        }

        it("should have all things in the character") {
            karacter.`should equal`(mapOf("foo" to "bar", "fruit" to "apple"))
        }

        it("should not mix values") {
            listOf(karacter.values<String>("foo"),
                    karacter.values<String>("fruit")) `should equal`
                    listOf(listOf("bar"), listOf("apple"))
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

    describe("A new character with a string key using default rule") {
        var (editpad, karacter) = newKaracter(::ScratchPad)

        beforeGroup {
            editpad["foo"] = mostRecent("")
            editpad = editpad.keep(::ScratchPad)
        }

        it("should have no values for key") {
            karacter.values<Int>("foo") `should equal` listOf()
        }

        it("should have summed value to zero") {
            karacter `should equal` mapOf("foo" to "")
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

        it("should diplay nicely") {
            karacter.toString() `should equal` """All (2): {foo=baz}
2: Scratch {foo=baz}
1: Scratch {foo=bar}
""".trim()
        }
    }

    describe("A new character with an integer key") {
        val (editpad, karacter) = newKaracter(::ScratchPad)

        beforeGroup {
            editpad["foo"] = Rule("Sum all")
            { _karacter: Karacter, key: String ->
                _karacter.values<Int>(key).sum()
            }
            editpad.keep(::ScratchPad)
        }

        it("should have no values for key") {
            karacter.values<Int>("foo") `should equal` listOf()
        }

        it("should have summed value to zero") {
            karacter `should equal` mapOf("foo" to 0)
        }
    }

    describe("A character with several integer values using a sum rule") {
        var (editpad, karacter) = newKaracter(::ScratchPad)

        beforeGroup {
            editpad["foo"] = Rule("Sum all")
            { _karacter: Karacter, key: String ->
                _karacter.values<Int>(key).sum()
            }
            editpad = editpad.keep(::ScratchPad)
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
        class TestPad(karacter: Karacter)
            : EditPad(karacter, "Test") {
            val foo = 82
        }

        val (editpad, _) = newKaracter(::TestPad)

        it("should behave like a vanilla edit pad") {
            editpad.foo `should equal` 82
        }
    }

    describe("A value with several rules") {
        var (editpad, karacter) = newKaracter(::ScratchPad)

        beforeGroup {
            editpad["foo"] = Rule("Die, die, die")
            { _karacter: Karacter, key: String ->
                if (1 < _karacter.rules<Any>(key).size) // Ignore self
                    throw AssertionError("Earlier rule should be ignored")
            }
            editpad = editpad.keep(::ScratchPad)
            editpad["foo"] = mostRecent("Bob")
            editpad.keep(::ScratchPad)
        }

        it("should use the most recent rule") {
            karacter.rules<String>("foo").
                    map { it.toString() } `should equal`
                    listOf("[Rule: Most recent (default Bob)]",
                            "[Rule: Die, die, die]")
        }
    }

    describe("Keeping a whole pad") {
        val (editpad, karacter) = newKaracter(::ScratchPad)

        class Newby(_karacter: Karacter) : EditPad(_karacter, "Newby") {
            init {
                this["foo"] = 1
                this["bar"] = "BAZ"
            }
        }

        beforeGroup {
            editpad.keep(::Newby).keep(::ScratchPad)
        }

        it("should keep a whole pad") {
            karacter `should equal` mapOf("foo" to 1, "bar" to "BAZ")
        }
    }

    describe("Keeping a custom pad") {
        val (editpad, _) = newKaracter(::ScratchPad)

        class Newby(karacter: Karacter) : EditPad(karacter, "Newby") {
            val foo = 3
        }

        beforeGroup {
            editpad.keep(::Newby).keep(::ScratchPad)
        }

        it("should keep a whole pad") {
            editpad.keep(::Newby).foo `should be` 3
        }
    }
})
