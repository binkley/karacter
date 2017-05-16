package hm.binkley.labs.karacter

import hm.binkley.labs.karacter.Karacter.Companion.newKaracter
import hm.binkley.labs.karacter.SetPad.Full.Companion.UNLIMITED
import hm.binkley.labs.karacter.SetPad.Full.Companion.max
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

        val (setpad, _) = newKaracter { TestSetPad(it) }
        val scratchpad = setpad.keep { ScratchPad(it) }

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
                setpad.add("J Random Addition", scratchpad).
                        keep { ScratchPad(it) }
            } `should throw` IllegalArgumentException::class
        }

        it("should complain when removing") {
            {
                setpad.remove("J Random Removal", scratchpad).
                        keep { ScratchPad(it) }
            } `should throw` IllegalArgumentException::class
        }
    }

    describe("A partially-filled container pad") {
        class TestSetPad(karacter: Karacter)
            : SetPad<ScratchPad>(karacter, "Test set pad", UNLIMITED)

        val (setpad, _) = newKaracter { TestSetPad(it) }
        val scratchpad = setpad.keep { ScratchPad(it) }

        beforeGroup {
            scratchpad.keep { ScratchPad(it) }
            setpad.add("J Random Addition", scratchpad).
                    keep { ScratchPad(it) }
        }

        it("should start that way") {
            setpad.toSet().size `should be` 1
        }
    }

    describe("A contaner pad with multiple adds and removals") {
        class TestSetPad(karacter: Karacter)
            : SetPad<ScratchPad>(karacter, "Test set pad", UNLIMITED)

        it("should complain when re-adding") {
            {
                val (setpad, _) = newKaracter { TestSetPad(it) }
                val scratchpad = setpad.keep { ScratchPad(it) }
                setpad.add("J Random Addition", scratchpad).
                        keep { ScratchPad(it) }

                setpad.add("J Other Addition", scratchpad).
                        keep { ScratchPad(it) }
            } `should throw` IllegalArgumentException::class
        }

        it("should complain adding an unkept pad") {
            {
                val (setpad, _) = newKaracter { TestSetPad(it) }
                val scratchpad = setpad.keep { ScratchPad(it) }

                setpad.add("J Other Addition", scratchpad).
                        keep { ScratchPad(it) }
            } `should throw` IllegalArgumentException::class
        }

        it("should complain removing an unadded pad") {
            {
                val (setpad, _) = newKaracter { TestSetPad(it) }
                val scratchpad = setpad.keep { ScratchPad(it) }
                scratchpad.keep { ScratchPad(it) }

                setpad.remove("J Random Removal", scratchpad).
                        keep { ScratchPad(it) }
            } `should throw` IllegalArgumentException::class
        }

        it("should add and remove twice in a row") {
            val (setpad, _) = newKaracter { TestSetPad(it) }
            val scratchpad = setpad.keep { ScratchPad(it) }
            val otherpad = scratchpad.keep { ScratchPad(it) }
            otherpad.keep { ScratchPad(it) }

            setpad.add("J Random Addition", scratchpad).
                    add("J Other Addition", otherpad).
                    keep { ScratchPad(it) }

            setpad.remove("J Random Removal", scratchpad).
                    remove("JU Other Removal", otherpad).
                    keep { ScratchPad(it) }

            setpad.toSet().isEmpty() `should be` true
        }
    }

    describe("A complex container pad") {
        class TestSetPad(karacter: Karacter)
            : SetPad<ScratchPad>(karacter, "Test set pad", UNLIMITED)

        it("should display nicely") {
            val (setpad, _) = newKaracter { TestSetPad(it) }
            val scratchpadA = setpad.keep { ScratchPad(it) }
            val scratchpadB = scratchpadA.keep { ScratchPad(it) }
            val scratchpadC = scratchpadB.keep { ScratchPad(it) }
            val scratchpadD = scratchpadC.keep { ScratchPad(it) }
            scratchpadD.keep { ScratchPad(it) }

            scratchpadC["foo"] = "bar"

            setpad.add("J Random Addition", scratchpadA).
                    add("Ignore #1 Addition", scratchpadB).
                    add("Ignore #2 Addition", scratchpadD).
                    remove("Ignore #1 Removal", scratchpadB).
                    remove("Ignore #2 Removal", scratchpadD).
                    add("J Other Addition", scratchpadC).
                    keep { ScratchPad(it) }

            setpad.toString() `should equal` """
Test set pad {}
 - (3) -> Scratch {foo=bar}
 - (1) -> Scratch {}
""".trim()
        }
    }

    describe("A recycled container pad") {
        class TestSetPad(karacter: Karacter)
            : SetPad<ScratchPad>(karacter, "Test set pad", UNLIMITED)

        val (setpad, _) = newKaracter { TestSetPad(it) }
        val scratchpad = setpad.keep { ScratchPad(it) }

        beforeGroup {
            scratchpad.keep { ScratchPad(it) }
            setpad.add("J Random Addition", scratchpad).
                    remove("J Random Removal", scratchpad).
                    keep { ScratchPad(it) }
        }

        it("should start that way") {
            setpad.toSet().`should be empty`()
        }
    }

    describe("A full container pad") {
        class TestSetPad(karacter: Karacter)
            : SetPad<ScratchPad>(karacter, "Test set pad", max(1))

        val (setpad, _) = newKaracter { TestSetPad(it) }
        val scratchpad = setpad.keep { ScratchPad(it) }

        beforeGroup {
            scratchpad.keep { ScratchPad(it) }
            setpad.add("J Random Addition", scratchpad).
                    keep { ScratchPad(it) }
        }

        it("should complain when adding more") {
            {
                val extrapad = scratchpad.keep { ScratchPad(it) }
                extrapad.keep { ScratchPad(it) }
                setpad.add("J Random Addition", extrapad).
                        keep { ScratchPad(it) }
            } `should throw` IllegalStateException::class
        }
    }
})
