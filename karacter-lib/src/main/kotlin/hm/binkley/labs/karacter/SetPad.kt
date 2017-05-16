package hm.binkley.labs.karacter

import hm.binkley.labs.karacter.Karacter.MutableEditPad

open class SetPad<T : MutableEditPad>(
        karacter: Karacter,
        name: String,
        private val full: Full,
        private val set: MutableSet<T> = mutableSetOf())
    : MutableEditPad(karacter, name), Set<T> by set {
    /**
     * @throws UnsupportedOperationException Use `toSet().size` or `toMap().size`
     * @see [SetPad.toSet]
     * @see [MutableEditPad.toMap]
     */
    override final val size
        get() = throw UnsupportedOperationException(
                "Use toMap().size or toSet().size")

    /**
     * @throws UnsupportedOperationException Use `toSet().isEmpty()` or `toMap().isEmpty()`
     * @see [SetPad.toSet]
     * @see [MutableEditPad.toMap]
     */
    override final fun isEmpty() = throw UnsupportedOperationException(
            "Use toMap().isEmpty() or toSet().isEmpty()")

    /** @todo When compiler supports lower bounds, use immutable base */
    fun toSet(): Set<T> = set

    fun add(name: String, pad: T) = keep {
        Add(it, name, pad)
    }

    fun remove(name: String, pad: T) = keep {
        Remove(it, name, pad)
    }

    inner class Add(karacter: Karacter, name: String, private val pad: T)
        : MutableEditPad(karacter, name) {
        override fun <U : MutableEditPad> keep(next: (Karacter) -> U): U {
            if (!karacter.toList().contains(pad))
                throw IllegalArgumentException()
            if (full(set)) throw IllegalStateException()
            if (!set.add(pad)) throw IllegalArgumentException()
            return super.keep(next)
        }

        fun add(name: String, pad: T) = keep {
            Add(it, name, pad)
        }

        fun remove(name: String, pad: T) = keep {
            Remove(it, name, pad)
        }
    }

    inner class Remove(karacter: Karacter, name: String, private val pad: T)
        : MutableEditPad(karacter, name) {
        override fun <U : MutableEditPad> keep(next: (Karacter) -> U): U {
            if (!set.remove(pad)) throw IllegalArgumentException()
            return super.keep(next)
        }

        fun add(name: String, pad: T) = keep {
            Add(it, name, pad)
        }

        fun remove(name: String, pad: T) = keep {
            Remove(it, name, pad)
        }
    }

    override fun toString(): String {
        val pads = karacter.toList()
        return set.map { pads.indexOf(it) to it }.
                sortedBy { it.first }.
                map { "\n - (${pads.size - it.first - 1}) -> ${it.second}" }.
                joinToString("", super.toString())
    }

    open class Full(val name: String, checkFull: (Set<*>) -> Boolean)
        : (Set<*>) -> Boolean by checkFull {
        override fun toString() = name

        companion object {
            val UNLIMITED = Full("Unlimited") { false }
            fun max(max: Int) = Full("Max $max") { set -> set.size == max }
        }
    }
}
