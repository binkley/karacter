package hm.binkley.labs.karacter

import hm.binkley.labs.karacter.Karacter.KaracterMap
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should equal`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object KaracterMapSpec : Spek({
    describe("A new character and edit pad") {
        val map = KaracterMap { ScratchPad(it) }

        it("should be blank") {
            map.`should be empty`()
            map.pad.`should be empty`()
        }
    }

    describe("A character with edit pad changes") {
        val map = KaracterMap { ScratchPad(it) }

        beforeGroup { map.pad["foo"] = "bar" }

        it("should have nothing in the map") {
            map.`should be empty`()
        }

        it("should have one thing in the pad") {
            map.pad `should equal` mapOf("foo" to "bar")
        }
    }

    describe("A character with a kept edit pad changes") {
        val map = KaracterMap { ScratchPad(it) }

        beforeGroup {
            map.pad["foo"] = "bar"
            map.keep { ScratchPad(it) }
            map.pad["foo"] = "baz"
            map.keep { ScratchPad(it) }
        }

        it("should have one thing in the character") {
            map.`should equal`(mapOf("foo" to "baz"))
        }

        it("should have nothing in the edit pad") {
            map.pad.`should be empty`()
        }

        it("should know all values for a key in the character") {
            map.values<String>("foo") `should equal` listOf("baz", "bar")
        }
    }
})
