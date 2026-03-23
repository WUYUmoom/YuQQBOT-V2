package com.wuyumoom.yuqqbot.api.config

/**
 * Path separator and default-copy behaviour for a [Configuration].
 */
open class ConfigurationOptions protected constructor(
    protected val configuration: Configuration
) {
    private var pathSeparator: Char = '.'
    private var copyDefaults: Boolean = false

    open fun configuration(): Configuration = configuration

    fun pathSeparator(): Char = pathSeparator

    open fun pathSeparator(value: Char): ConfigurationOptions {
        pathSeparator = value
        return this
    }

    fun copyDefaults(): Boolean = copyDefaults

    open fun copyDefaults(value: Boolean): ConfigurationOptions {
        copyDefaults = value
        return this
    }
}
