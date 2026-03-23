package com.wuyumoom.yuqqbot.api.config

/**
 * Root configuration: defaults and [ConfigurationOptions].
 */
interface Configuration : ConfigurationSection {
    override fun addDefault(path: String, value: Any?)
    fun addDefaults(defaults: Map<String, Any?>)
    fun addDefaults(defaults: Configuration)
    fun setDefaults(defaults: Configuration)
    fun getDefaults(): Configuration?
    fun options(): ConfigurationOptions
}
