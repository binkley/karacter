package hm.binkley.labs.karacter

import hm.binkley.labs.karacter.Karacter.Companion.newKaracter
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should throw`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object SetPadSpec : Spek({
    describe("A empty container pad") {
        class TestSetPad(karacter: Karacter)
            : SetPad<ScratchPad>(karacter, "Test set pad",
                Full("Unlimited") { false })

        val (setpad, _) = newKaracter(::TestSetPad)
        val scratchpad = setpad.keep(::ScratchPad)

        it("should complain on 'size'") {
            { setpad.size } `should throw` UnsupportedOperationException::class
        }

        it("should complain on 'isEmpty'") {
            { setpad.isEmpty() } `should throw` UnsupportedOperationException::class
        }

        it("should start that way as a set") {
            setpad.toSet().`should be empty`()
        }

        it("should start that way as a map") {
            setpad.toMap().`should be empty`()
        }

        it("should complain when adding before keeping") {
            {
                setpad.add(scratchpad)
            } `should throw` IllegalArgumentException::class
        }

        it("should complain when removing") {
            {
                setpad.remove(scratchpad)
            } `should throw` IllegalArgumentException::class
        }
    }

    describe("A partially-filled container pad") {
        class TestSetPad(karacter: Karacter)
            : SetPad<ScratchPad>(karacter, "Test set pad",
                Full("Unlimited") { false })

        val (setpad, _) = newKaracter(::TestSetPad)
        val scratchpad = setpad.keep(::ScratchPad)

        beforeGroup {
            scratchpad.keep(::ScratchPad)
            setpad.add(scratchpad)
        }

        it("should start that way") {
            setpad.toSet().size `should be` 1
        }

        it("should complain when re-adding") {
            {
                setpad.add(scratchpad)
            } `should throw` IllegalArgumentException::class
        }
    }

    describe("A complex container pad") {
        class TestSetPad(karacter: Karacter)
            : SetPad<ScratchPad>(karacter, "Test set pad",
                Full("Unlimited") { false })

        it("should display nicely") {
            val (setpad, _) = newKaracter(::TestSetPad)
            val scratchpadA = setpad.keep(::ScratchPad)
            val scratchpadB = scratchpadA.keep(::ScratchPad)
            val scratchpadC = scratchpadB.keep(::ScratchPad)
            scratchpadC["foo"] = "bar"
            scratchpadC.keep(::ScratchPad)
            setpad.add(scratchpadA)
            setpad.add(scratchpadC)

            setpad.toString() `should equal` """Test set pad {}
3. Scratch {foo=bar}
1. Scratch {}"""
        }
    }

    describe("A recycled container pad") {
        class TestSetPad(karacter: Karacter)
            : SetPad<ScratchPad>(karacter, "Test set pad",
                Full("Unlimited") { false })

        val (setpad, _) = newKaracter(::TestSetPad)
        val scratchpad = setpad.keep(::ScratchPad)

        beforeGroup {
            scratchpad.keep(::ScratchPad)
            setpad.add(scratchpad)
            setpad.remove(scratchpad)
        }

        it("should start that way") {
            setpad.toSet().`should be empty`()
        }
    }

    describe("A full container pad") {
        class TestSetPad(karacter: Karacter)
            : SetPad<ScratchPad>(karacter, "Test set pad",
                Full("Max 1") { set -> set.isNotEmpty() })

        val (setpad, _) = newKaracter(::TestSetPad)
        val scratchpad = setpad.keep(::ScratchPad)

        beforeGroup {
            scratchpad.keep(::ScratchPad)
            setpad.add(scratchpad)
        }

        it("should complain when adding more") {
            {
                val extrapad = scratchpad.keep(::ScratchPad)
                extrapad.keep(::ScratchPad)
                setpad.add(extrapad)
            } `should throw` IllegalStateException::class
        }
    }
})
