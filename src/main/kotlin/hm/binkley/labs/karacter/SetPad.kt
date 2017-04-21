package hm.binkley.labs.karacter

import hm.binkley.labs.karacter.Karacter.MutableEditPad

open class SetPad<T : MutableEditPad>(
        karacter: Karacter,
        name: String,
        private val full: Full,
        private val set: MutableSet<T> = mutableSetOf())
    : MutableEditPad(karacter, name), Set<T> by set {
    /**
     * @throws UnsupportedOperationException Use `toSet().size` or `toMap().size` instead
     * @see [SetPad.toSet]
     * @see [MutableEditPad.toMap]
     */
    override final val size
        get() = throw UnsupportedOperationException()

    /**
     * @throws UnsupportedOperationException Use `toSet().size` or `toMap().size` instead
     * @see [SetPad.toSet]
     * @see [MutableEditPad.toMap]
     */
    override final fun isEmpty() = throw UnsupportedOperationException()

    fun toSet(): Set<T> = set // TODO: Leaky - T's are modifiable

    fun add(pad: T) {
        if (!karacter.toList().contains(pad)) throw IllegalArgumentException()
        if (full(set)) throw IllegalStateException()
        if (!set.add(pad)) throw IllegalArgumentException()
    }

    fun remove(pad: T) {
        if (!set.remove(pad)) throw IllegalArgumentException()
    }

    override fun toString(): String {
        val pads = karacter.toList()
        return set.map { pads.indexOf(it) to it }.
                sortedBy { it.first }.
                map { "\n - ${pads.size - it.first - 1}. ${it.second}" }.
                joinToString("", super.toString())
    }

    open class Full(val name: String, private val full: (Set<*>) -> Boolean)
        : (Set<*>) -> Boolean by full {
        override fun toString() = name

        companion object {
            val UNLIMITED = Full("Unlimited") { false }
            fun MAX(max: Int) = Full("Max $max") { set -> set.size == max }
        }
    }
}
