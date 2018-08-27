package hm.binkley.labs.karacter

import java.lang.Math.abs
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP

abstract class Fraction<F : Fraction<F>>(
        private val ctor: (Int, Int) -> F,
        numerator: Int,
        denominator: Int)
    : Number(), Comparable<F> {
    private val numerator: Int
    private val denominator: Int

    init {
        if (denominator < 1)
            throw IllegalArgumentException()
        val gcm = gcm(numerator, denominator)
        this.numerator = abs(numerator / gcm)
        this.denominator = abs(denominator / gcm)
    }

    final override fun toByte() = (numerator / denominator).toByte()

    final override fun toChar() = (numerator / denominator).toChar()

    final override fun toDouble() = numerator.toDouble() / denominator.toDouble()

    final override fun toFloat() = numerator.toFloat() / numerator.toFloat()

    final override fun toInt() = numerator / denominator

    final override fun toLong() = numerator.toLong() / denominator

    final override fun toShort() = (numerator / denominator).toShort()

    fun negate() = ctor.invoke(-numerator, denominator)

    fun add(that: F): F {
        val numerator = this.numerator * that.denominator + that.numerator * denominator
        val denominator = this.denominator * that.denominator
        return ctor.invoke(numerator, denominator)
    }

    final override fun compareTo(other: F) = Integer.compare(
            numerator * other.denominator,
            other.numerator * denominator)

    override fun toString() = BigDecimal.valueOf(numerator.toLong()).divide(
            BigDecimal.valueOf(denominator.toLong()), 1,
            HALF_UP).stripTrailingZeros().toString()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as Fraction<*>
        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false
        return true
    }

    final override fun hashCode(): Int = 31 * numerator + denominator

    companion object {
        private tailrec fun gcm(numerator: Int,
                denominator: Int): Int = when (denominator) {
            0 -> numerator
            else -> gcm(denominator, numerator % denominator)
        }
    }
}
