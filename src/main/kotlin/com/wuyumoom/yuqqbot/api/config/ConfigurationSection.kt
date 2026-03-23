package com.wuyumoom.yuqqbot.api.config

/**
 * A node in a hierarchical key–value configuration (dot-separated paths).
 */
interface ConfigurationSection {
    fun getKeys(deep: Boolean): Set<String>
    fun getValues(deep: Boolean): Map<String, Any?>
    fun contains(path: String): Boolean
    fun isSet(path: String): Boolean
    fun getCurrentPath(): String
    fun getName(): String
    fun getRoot(): Configuration?
    fun getParent(): ConfigurationSection?
    operator fun get(path: String): Any?
    fun get(path: String, def: Any?): Any?
    fun set(path: String, value: Any?)
    fun createSection(path: String): ConfigurationSection
    fun createSection(path: String, map: Map<*, *>): ConfigurationSection

    fun getString(path: String): String?
    fun getString(path: String, def: String?): String?
    fun isString(path: String): Boolean

    fun getInt(path: String): Int
    fun getInt(path: String, def: Int): Int
    fun isInt(path: String): Boolean

    fun getBoolean(path: String): Boolean
    fun getBoolean(path: String, def: Boolean): Boolean
    fun isBoolean(path: String): Boolean

    fun getDouble(path: String): Double
    fun getDouble(path: String, def: Double): Double
    fun isDouble(path: String): Boolean

    fun getLong(path: String): Long
    fun getLong(path: String, def: Long): Long
    fun isLong(path: String): Boolean

    fun getList(path: String): List<*>?
    fun getList(path: String, def: List<*>?): List<*>?
    fun isList(path: String): Boolean

    fun getStringList(path: String): List<String>
    fun getIntegerList(path: String): List<Int>
    fun getBooleanList(path: String): List<Boolean>
    fun getDoubleList(path: String): List<Double>
    fun getFloatList(path: String): List<Float>
    fun getLongList(path: String): List<Long>
    fun getByteList(path: String): List<Byte>
    fun getCharacterList(path: String): List<Char>
    fun getShortList(path: String): List<Short>
    fun getMapList(path: String): List<Map<*, *>>

    fun getVector(path: String): Vector3?
    fun getVector(path: String, def: Vector3?): Vector3?
    fun isVector(path: String): Boolean

    fun getColor(path: String): ConfigColor?
    fun getColor(path: String, def: ConfigColor?): ConfigColor?
    fun isColor(path: String): Boolean

    fun getConfigurationSection(path: String): ConfigurationSection?
    fun isConfigurationSection(path: String): Boolean

    fun getDefaultSection(): ConfigurationSection?
    fun addDefault(path: String, value: Any?)
}
