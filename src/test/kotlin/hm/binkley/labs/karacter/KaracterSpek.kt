package hm.binkley.labs.karacter

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.Assertions.assertTrue

class KaracterSpek : Spek({
    describe("An empty character") {
        val karacter = Karacter()

        it("should contain nothing") {
            assertTrue(karacter.isEmpty())
        }
    }
})
