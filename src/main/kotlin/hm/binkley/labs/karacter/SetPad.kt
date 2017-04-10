package hm.binkley.labs.karacter

import hm.binkley.labs.karacter.Karacter.EditPad

open class SetPad<T : EditPad>(
        karacter: Karacter,
        name: String,
        private val full: Full,
        private val set: MutableSet<T> = mutableSetOf())
    : EditPad(karacter, name), Set<T> by set {
    override val size
        get() = throw UnsupportedOperationException()

    override fun isEmpty() = throw UnsupportedOperationException()

    fun toSet(): Set<T> = set
    fun toMap(): Map<String, Any> = this

    fun add(pad: T) {
        if (full(set)) throw IllegalStateException()
        if (!set.add(pad)) throw IllegalArgumentException()
    }

    fun remove(pad: T) {
        if (!set.remove(pad)) throw IllegalArgumentException()
    }

    override fun toString() = "${super.toString()} $set"

    open class Full(val name: String, private val full: (Set<*>) -> Boolean)
        : (Set<*>) -> Boolean by full {
        override fun toString() = name
    }
}
