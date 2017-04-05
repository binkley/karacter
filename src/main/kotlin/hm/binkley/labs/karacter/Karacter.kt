package hm.binkley.labs.karacter

typealias Rule = (Karacter, String) -> Any
// typealias PadMaker<T : Karacter.EditPad<T>> = (Karacter) -> T

object InternalBug : IllegalStateException("ILLEGAL BUG")

class Karacter private constructor(
        private val cache: MutableMap<String, Any> = mutableMapOf(),
        private val layers: MutableList<EditPad<*>> = mutableListOf(),
        private val rules: MutableMap<String, Rule> = mutableMapOf())
    : Map<String, Any> by cache {
    /** @todo Not concurrency-safe */
    private fun updateCache(layer: EditPad<*>) = apply {
        val keys = cache.keys + layer.keys
        layers.add(0, layer)
        // cache.clear() - TODO: Is Karacter only additive, no keys deleted?
        keys.forEach { cache[it] = value(it) }
    }

    private fun value(key: String) = rules[key]?.invoke(this, key)
            ?: mostRecent(key)

    private fun mostRecent(key: String) = layers(key).first()[key]
            ?: throw InternalBug

    private fun layers(key: String) = layers.filter { key in it }

    private fun copy() = Karacter(cache.toMutableMap(),
            layers.toMutableList(), rules.toMutableMap())

    @Suppress("UNCHECKED_CAST")
    fun <T> values(key: String): List<T> = layers(key).map { it[key] as T }

    fun rule(key: String, rule: Rule) {
        rules[key] = rule
    }

    class ScratchPad(karacter: Karacter) : EditPad<ScratchPad>(karacter)

    open class EditPad<T : EditPad<T>> protected constructor(
            protected val karacter: Karacter)
        : MutableMap<String, Any> by HashMap<String, Any>() {
        fun <U : EditPad<U>> keep(next: (Karacter) -> U): U
                = next(karacter.updateCache(this))

        fun <U : EditPad<U>> discard(next: (Karacter) -> U) = next(karacter)

        fun whatIf(): Karacter = karacter.copy().updateCache(this@EditPad)
    }

    companion object {
        fun <T : EditPad<T>> newKaracter(
                next: (Karacter) -> T): Pair<T, Karacter> {
            val karacter: Karacter = Karacter()
            return next(karacter) to karacter
        }
    }
}
