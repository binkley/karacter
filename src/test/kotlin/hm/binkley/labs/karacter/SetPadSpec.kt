package hm.binkley.labs.karacter

import hm.binkley.labs.karacter.Karacter.Companion.newKaracter
import hm.binkley.labs.karacter.SetPad.Full.Companion.MAX
import hm.binkley.labs.karacter.SetPad.Full.Companion.UNLIMITED
import org.amshove.kluent.`should be empty`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.`should throw the Exception`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.`with message`
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object SetPadSpec : Spek({
    describe("A empty container pad") {
        class TestSetPad(karacter: Karacter)
            : SetPad<ScratchPad>(karacter, "Test set pad", UNLIMITED)

        val (setpad, _) = newKaracter(::TestSetPad)
        val scratchpad = setpad.keep(::ScratchPad)

        it("should complain on 'size'") {
            {
                setpad.size
            } `should throw the Exception` UnsupportedOperationException::class `with message` "Use toMap().size or toSet().size"
        }

        it("should complain on 'isEmpty'") {
            {
                setpad.isEmpty()
            } `should throw the Exception` UnsupportedOperationException::class `with message` "Use toMap().isEmpty() or toSet().isEmpty()"
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
            : SetPad<ScratchPad>(karacter, "Test set pad", UNLIMITED)

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
            : SetPad<ScratchPad>(karacter, "Test set pad", UNLIMITED)

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
 - 3. Scratch {foo=bar}
 - 1. Scratch {}"""
        }
    }

    describe("A recycled container pad") {
        class TestSetPad(karacter: Karacter)
            : SetPad<ScratchPad>(karacter, "Test set pad", UNLIMITED)

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
            : SetPad<ScratchPad>(karacter, "Test set pad", MAX(1))

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
