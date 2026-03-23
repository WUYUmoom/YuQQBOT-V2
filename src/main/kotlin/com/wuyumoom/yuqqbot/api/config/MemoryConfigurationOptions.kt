package com.wuyumoom.yuqqbot.api.config

/**
 * Options for [MemoryConfiguration].
 */
open class MemoryConfigurationOptions(
    configuration: MemoryConfiguration
) : ConfigurationOptions(configuration) {
    override fun configuration(): MemoryConfiguration = super.configuration() as MemoryConfiguration

    override fun copyDefaults(value: Boolean): MemoryConfigurationOptions {
        super.copyDefaults(value)
        return this
    }

    override fun pathSeparator(value: Char): MemoryConfigurationOptions {
        super.pathSeparator(value)
        return this
    }
}
