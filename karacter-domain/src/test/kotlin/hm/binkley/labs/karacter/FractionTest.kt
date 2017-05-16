package hm.binkley.labs.karacter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class FractionTest {
    @Test
    fun shouldDiplayNicely()
            = assertEquals("0.7", EgFraction(2, 3).toString())

    @Test
    fun shouldDiplayWithoutFractions()
            = assertEquals("1", EgFraction(1, 1).toString())

    @Test
    fun shouldNegate()
            = assertEquals(EgFraction(-3, 2), EgFraction(3, 2).negate())

    @Test
    fun shouldAdd()
            = assertEquals(EgFraction(3, 2), EgFraction(5, 6).
            add(EgFraction(2, 3)))

    @Test
    fun shouldCompareLessThan() {
        assertTrue(EgFraction(2, 3) < EgFraction(4, 3))
    }

    @Test
    fun shouldCompareEqualTo() {
        assertTrue(0 == EgFraction(2, 3).compareTo(EgFraction(2, 3)))
    }

    @Test
    fun shouldCompareGreaterThan() {
        assertTrue(EgFraction(4, 3) > EgFraction(2, 3))
    }

    internal class EgFraction(numerator: Int, denominator: Int)
        : Fraction<EgFraction>(::EgFraction, numerator, denominator)
}
