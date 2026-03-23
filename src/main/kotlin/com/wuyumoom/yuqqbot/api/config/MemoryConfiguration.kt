package com.wuyumoom.yuqqbot.api.config

import kotlin.collections.iterator

/**
 * [Configuration] stored only in memory (no I/O).
 */
open class MemoryConfiguration : MemorySection, Configuration {
    private var defaults: Configuration? = null
    @JvmField
    protected var memoOptions: MemoryConfigurationOptions? = null

    constructor() : super()

    constructor(defaults: Configuration?) : super() {
        this.defaults = defaults
    }

    override fun addDefault(path: String, value: Any?) {
        requireNotNull(path) { "Path may not be null" }
        if (defaults == null) {
            defaults = MemoryConfiguration()
        }
        defaults!!.set(path, value)
    }

    override fun addDefaults(defaults: Map<String, Any?>) {
        requireNotNull(defaults) { "Defaults may not be null" }
        for ((k, v) in defaults) {
            addDefault(k, v)
        }
    }

    override fun addDefaults(defaults: Configuration) {
        requireNotNull(defaults) { "Defaults may not be null" }
        addDefaults(defaults.getValues(true))
    }

    override fun setDefaults(defaults: Configuration) {
        requireNotNull(defaults) { "Defaults may not be null" }
        this.defaults = defaults
    }

    override fun getDefaults(): Configuration? = defaults

    override fun getParent(): ConfigurationSection? = null

    override fun options(): MemoryConfigurationOptions {
        if (memoOptions == null) {
            memoOptions = MemoryConfigurationOptions(this)
        }
        return memoOptions!!
    }
}
