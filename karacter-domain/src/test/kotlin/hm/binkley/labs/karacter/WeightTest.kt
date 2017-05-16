package hm.binkley.labs.karacter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class WeightTest {
    @Test
    fun shouldDisplayNicelyInPounds()
            = assertEquals("1#", Weight.inPounds(1).toString())

    @Test
    fun shouldDisplayNicelyAsFraction()
            = assertEquals("1.1#", Weight.asFraction(11, 10).toString())
}
