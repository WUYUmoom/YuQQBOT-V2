package com.wuyumoom.yuqqbot.api.config.file

import com.wuyumoom.yuqqbot.api.config.MemoryConfiguration
import com.wuyumoom.yuqqbot.api.config.MemoryConfigurationOptions

/**
 * Header and related options for [FileConfiguration].
 */
open class FileConfigurationOptions(
    configuration: MemoryConfiguration
) : MemoryConfigurationOptions(configuration) {
    private var header: String? = null
    private var copyHeader: Boolean = true

    override fun configuration(): FileConfiguration = super.configuration() as FileConfiguration

    override fun copyDefaults(value: Boolean): FileConfigurationOptions {
        super.copyDefaults(value)
        return this
    }

    override fun pathSeparator(value: Char): FileConfigurationOptions {
        super.pathSeparator(value)
        return this
    }

    fun header(): String? = header

    open fun header(value: String?): FileConfigurationOptions {
        header = value
        return this
    }

    fun copyHeader(): Boolean = copyHeader

    open fun copyHeader(value: Boolean): FileConfigurationOptions {
        copyHeader = value
        return this
    }
}
