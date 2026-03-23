package com.wuyumoom.yuqqbot.api.config.file

/**
 * YAML dumper options (indent) for [YamlConfiguration].
 */
class YamlConfigurationOptions(
    configuration: YamlConfiguration
) : FileConfigurationOptions(configuration) {
    private var indent: Int = 2

    override fun configuration(): YamlConfiguration = super.configuration() as YamlConfiguration

    override fun copyDefaults(value: Boolean): YamlConfigurationOptions {
        super.copyDefaults(value)
        return this
    }

    override fun pathSeparator(value: Char): YamlConfigurationOptions {
        super.pathSeparator(value)
        return this
    }

    override fun header(value: String?): YamlConfigurationOptions {
        super.header(value)
        return this
    }

    override fun copyHeader(value: Boolean): YamlConfigurationOptions {
        super.copyHeader(value)
        return this
    }

    fun indent(): Int = indent

    fun indent(value: Int): YamlConfigurationOptions {
        require(value >= 2) { "Indent must be at least 2 characters" }
        require(value <= 9) { "Indent cannot be greater than 9 characters" }
        indent = value
        return this
    }
}
