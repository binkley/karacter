package hm.binkley.labs.karacter

class Weight(numerator: Int, denominator: Int)
    : Fraction<Weight>(::Weight, numerator, denominator) {

    override fun toString(): String {
        return "${super.toString()}#"
    }

    companion object {
        val WEIGHTLESS = inPounds(0)

        fun inPounds(pounds: Int) = Weight(pounds, 1)
        fun asFraction(numerator: Int, denominator: Int)
                = Weight(numerator, denominator)
    }
}
