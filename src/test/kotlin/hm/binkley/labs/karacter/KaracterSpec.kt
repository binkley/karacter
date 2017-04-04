package hm.binkley.labs.karacter

import hm.binkley.labs.karacter.Karacter.Companion.makeKaracter
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should have key`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object KaracterSpec : Spek({
    describe("A new character and edit pad") {
        val (editpad, karacter) = makeKaracter()

        it("should have a map edit pad") {
            editpad `should be instance of` Map::class
        }

        it("should have nothing in the character") {
            karacter.`should be empty`()
        }

        it("should have nothing in the edit pad") {
            editpad.`should be empty`()
        }
    }

    describe("A character with edit pad changes") {
        val (editpad, karacter) = makeKaracter()

        beforeGroup { editpad["foo"] = "bar" }

        it("should have nothing in the character") {
            karacter.`should be empty`()
        }

        it("should have one thing in the edit pad") {
            editpad.size `should be` 1
        }

        it("should have right thing in the edit pad") {
            editpad `should have key` "foo"
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
            karacter.size `should be` 1
        }

        it("should have right thing in the character") {
            karacter `should have key` "foo"
        }

        it("should have nothing in the edit pad") {
            editpad.`should be empty`()
        }

        it("should know all values for a key in the character") {
            karacter.values<String>("foo") `should equal` listOf("baz", "bar")
        }
    }

    describe("A view of a character with mixed changes") {
        var (editpad, _) = makeKaracter()

        beforeGroup {
            editpad["foo"] = "bar"
            editpad = editpad.commit()
            editpad["foo"] = "baz"
        }

        it("should have a view as if committed") {
            editpad.whatIf()["foo"] `should equal` "baz"
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
            karacter.values<String>("foo") `should equal` listOf("baz", "bar")
        }

        it("should have most recent value in character") {
            karacter["foo"] `should equal` "baz"
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
            karacter.values<Int>("foo") `should equal` listOf(4, 3)
        }

        it("should have summed value in character") {
            karacter["foo"] `should be` 7
        }
    }
})
