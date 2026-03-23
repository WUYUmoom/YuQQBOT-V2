package com.wuyumoom.yuqqbot.api.config

import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.collections.iterator

/**
 * In-memory [ConfigurationSection] backed by a [LinkedHashMap].
 */
open class MemorySection : ConfigurationSection {
    protected val map = LinkedHashMap<String, Any?>()
    private val root: Configuration?
    private val parent: ConfigurationSection?
    private val path: String
    private val fullPath: String

    /**
     * Root section; only valid when this instance is a [Configuration].
     */
    protected constructor() {
        require(this is Configuration) { "Cannot construct a root MemorySection when not a Configuration" }
        path = ""
        fullPath = ""
        parent = null
        root = this as Configuration
    }

    /**
     * Child section under [parent].
     */
    protected constructor(parent: ConfigurationSection, path: String) {
        requireNotNull(parent) { "Parent cannot be null" }
        requireNotNull(path) { "Path cannot be null" }
        this.path = path
        this.parent = parent
        this.root = parent.getRoot()
        requireNotNull(root) { "Path cannot be orphaned" }
        fullPath = createPath(parent, path)
    }

    override fun getKeys(deep: Boolean): Set<String> {
        val result = LinkedHashSet<String>()
        val root = getRoot()
        if (root != null && root.options().copyDefaults()) {
            getDefaultSection()?.getKeys(deep)?.let { result.addAll(it) }
        }
        mapChildrenKeys(result, this, deep, this)
        return result
    }

    override fun getValues(deep: Boolean): Map<String, Any?> {
        val result = LinkedHashMap<String, Any?>()
        val root = getRoot()
        if (root != null && root.options().copyDefaults()) {
            getDefaultSection()?.getValues(deep)?.let { result.putAll(it) }
        }
        mapChildrenValues(result, this, deep, this)
        return result
    }

    override fun contains(path: String): Boolean = get(path) != null

    override fun isSet(path: String): Boolean {
        val root = getRoot() ?: return false
        return if (root.options().copyDefaults()) {
            contains(path)
        } else {
            get(path, null) != null
        }
    }

    override fun getCurrentPath(): String = fullPath

    override fun getName(): String = path

    override fun getRoot(): Configuration? = root

    override fun getParent(): ConfigurationSection? = parent

    override operator fun get(path: String): Any? = get(path, getDefault(path))

    override fun get(path: String, def: Any?): Any? {
        requireNotNull(path) { "Path cannot be null" }
        if (path.isEmpty()) return this

        val root = getRoot() ?: throw IllegalStateException("Cannot access section without a root")
        val separator = root.options().pathSeparator()
        var lastSep = -1
        var section: ConfigurationSection = this
        while (true) {
            val from = lastSep + 1
            val next = path.indexOf(separator, from)
            if (next == -1) break
            section = section.getConfigurationSection(path.substring(from, next)) ?: return def
            lastSep = next
        }
        val key = path.substring(lastSep + 1)
        return if (section === this) {
            map[key] ?: def
        } else {
            section.get(key, def)
        }
    }

    override fun addDefault(path: String, value: Any?) {
        requireNotNull(path) { "Path cannot be null" }
        val root = getRoot() ?: throw IllegalStateException("Cannot add default without root")
        if (root === this) {
            throw UnsupportedOperationException("Unsupported addDefault(String, Object) implementation")
        }
        root.addDefault(createPath(this, path), value)
    }

    override fun getDefaultSection(): ConfigurationSection? {
        val root = getRoot() ?: return null
        val defaults = root.getDefaults() ?: return null
        return if (defaults.isConfigurationSection(fullPath)) {
            defaults.getConfigurationSection(fullPath)
        } else {
            null
        }
    }

    override fun set(path: String, value: Any?) {
        require(path.isNotEmpty()) { "Cannot set to an empty path" }
        val root = getRoot() ?: throw IllegalStateException("Cannot use section without a root")
        val separator = root.options().pathSeparator()
        var lastSep = -1
        var section: ConfigurationSection = this
        while (true) {
            val from = lastSep + 1
            val next = path.indexOf(separator, from)
            if (next == -1) break
            val node = path.substring(from, next)
            val sub = section.getConfigurationSection(node)
            section = sub ?: section.createSection(node)
            lastSep = next
        }
        val key = path.substring(lastSep + 1)
        if (section === this) {
            if (value == null) map.remove(key) else map[key] = value
        } else {
            section.set(key, value)
        }
    }

    override fun createSection(path: String): ConfigurationSection {
        require(path.isNotEmpty()) { "Cannot create section at empty path" }
        val root = getRoot() ?: throw IllegalStateException("Cannot create section without a root")
        val separator = root.options().pathSeparator()
        var lastSep = -1
        var section: ConfigurationSection = this
        while (true) {
            val from = lastSep + 1
            val next = path.indexOf(separator, from)
            if (next == -1) break
            val node = path.substring(from, next)
            val sub = section.getConfigurationSection(node)
            section = sub ?: section.createSection(node)
            lastSep = next
        }
        val key = path.substring(lastSep + 1)
        return if (section === this) {
            val result = MemorySection(this, key)
            map[key] = result
            result
        } else {
            section.createSection(key)
        }
    }

    override fun createSection(path: String, map: Map<*, *>): ConfigurationSection {
        val section = createSection(path)
        for ((k, v) in map) {
            val key = k.toString()
            if (v is Map<*, *>) {
                section.createSection(key, v)
            } else {
                section.set(key, v)
            }
        }
        return section
    }

    override fun getString(path: String): String? {
        val def = getDefault(path)
        return getString(path, def?.toString())
    }

    override fun getString(path: String, def: String?): String? {
        val v = get(path, def)
        return v?.toString() ?: def
    }

    override fun isString(path: String): Boolean = get(path) is String

    override fun getInt(path: String): Int {
        val def = getDefault(path)
        return getInt(path, if (def is Number) NumberConversions.toInt(def) else 0)
    }

    override fun getInt(path: String, def: Int): Int {
        val v = get(path, def)
        return if (v is Number) NumberConversions.toInt(v) else def
    }

    override fun isInt(path: String): Boolean = get(path) is Int

    override fun getBoolean(path: String): Boolean {
        val def = getDefault(path)
        return getBoolean(path, (def as? Boolean) ?: false)
    }

    override fun getBoolean(path: String, def: Boolean): Boolean {
        val v = get(path, def)
        return if (v is Boolean) v else def
    }

    override fun isBoolean(path: String): Boolean = get(path) is Boolean

    override fun getDouble(path: String): Double {
        val def = getDefault(path)
        return getDouble(path, if (def is Number) NumberConversions.toDouble(def) else 0.0)
    }

    override fun getDouble(path: String, def: Double): Double {
        val v = get(path, def)
        return if (v is Number) NumberConversions.toDouble(v) else def
    }

    override fun isDouble(path: String): Boolean = get(path) is Double

    override fun getLong(path: String): Long {
        val def = getDefault(path)
        return getLong(path, if (def is Number) NumberConversions.toLong(def) else 0L)
    }

    override fun getLong(path: String, def: Long): Long {
        val v = get(path, def)
        return if (v is Number) NumberConversions.toLong(v) else def
    }

    override fun isLong(path: String): Boolean = get(path) is Long

    override fun getList(path: String): List<*>? {
        val def = getDefault(path)
        return getList(path, if (def is List<*>) def else null)
    }

    override fun getList(path: String, def: List<*>?): List<*>? {
        val v = get(path, def)
        return if (v is List<*>) v else def
    }

    override fun isList(path: String): Boolean = get(path) is List<*>

    override fun getStringList(path: String): List<String> {
        val list = getList(path) ?: return emptyList()
        val out = ArrayList<String>()
        for (o in list) {
            if (o is String || isPrimitiveWrapper(o)) {
                out.add(o.toString())
            }
        }
        return out
    }

    override fun getIntegerList(path: String): List<Int> {
        val list = getList(path) ?: return emptyList()
        val out = ArrayList<Int>()
        for (o in list) {
            when (o) {
                is Int -> out.add(o)
                is String -> try {
                    out.add(o.toInt())
                } catch (_: Exception) {
                }
                is Char -> out.add(o.code)
                is Number -> out.add(o.toInt())
            }
        }
        return out
    }

    override fun getBooleanList(path: String): List<Boolean> {
        val list = getList(path) ?: return emptyList()
        val out = ArrayList<Boolean>()
        for (o in list) {
            when (o) {
                is Boolean -> out.add(o)
                is String -> when (o) {
                    java.lang.Boolean.TRUE.toString() -> out.add(true)
                    java.lang.Boolean.FALSE.toString() -> out.add(false)
                }
            }
        }
        return out
    }

    override fun getDoubleList(path: String): List<Double> {
        val list = getList(path) ?: return emptyList()
        val out = ArrayList<Double>()
        for (o in list) {
            when (o) {
                is Double -> out.add(o)
                is String -> try {
                    out.add(o.toDouble())
                } catch (_: Exception) {
                }
                is Char -> out.add(o.code.toDouble())
                is Number -> out.add(o.toDouble())
            }
        }
        return out
    }

    override fun getFloatList(path: String): List<Float> {
        val list = getList(path) ?: return emptyList()
        val out = ArrayList<Float>()
        for (o in list) {
            when (o) {
                is Float -> out.add(o)
                is String -> try {
                    out.add(o.toFloat())
                } catch (_: Exception) {
                }
                is Char -> out.add(o.code.toFloat())
                is Number -> out.add(o.toFloat())
            }
        }
        return out
    }

    override fun getLongList(path: String): List<Long> {
        val list = getList(path) ?: return emptyList()
        val out = ArrayList<Long>()
        for (o in list) {
            when (o) {
                is Long -> out.add(o)
                is String -> try {
                    out.add(o.toLong())
                } catch (_: Exception) {
                }
                is Char -> out.add(o.code.toLong())
                is Number -> out.add(o.toLong())
            }
        }
        return out
    }

    override fun getByteList(path: String): List<Byte> {
        val list = getList(path) ?: return emptyList()
        val out = ArrayList<Byte>()
        for (o in list) {
            when (o) {
                is Byte -> out.add(o)
                is String -> try {
                    out.add(o.toByte())
                } catch (_: Exception) {
                }
                is Char -> out.add(o.code.toByte())
                is Number -> out.add(o.toByte())
            }
        }
        return out
    }

    override fun getCharacterList(path: String): List<Char> {
        val list = getList(path) ?: return emptyList()
        val out = ArrayList<Char>()
        for (o in list) {
            when (o) {
                is Char -> out.add(o)
                is String -> if (o.length == 1) out.add(o[0])
                is Number -> out.add(o.toInt().toChar())
            }
        }
        return out
    }

    override fun getShortList(path: String): List<Short> {
        val list = getList(path) ?: return emptyList()
        val out = ArrayList<Short>()
        for (o in list) {
            when (o) {
                is Short -> out.add(o)
                is String -> try {
                    out.add(o.toShort())
                } catch (_: Exception) {
                }
                is Char -> out.add(o.code.toShort())
                is Number -> out.add(o.toShort())
            }
        }
        return out
    }

    override fun getMapList(path: String): List<Map<*, *>> {
        val list = getList(path)
        val out = ArrayList<Map<*, *>>()
        if (list == null) return out
        for (o in list) {
            if (o is Map<*, *>) out.add(o)
        }
        return out
    }

    override fun getVector(path: String): Vector3? {
        val def = getDefault(path)
        return getVector(path, if (def is Vector3) def else null)
    }

    override fun getVector(path: String, def: Vector3?): Vector3? {
        val v = get(path, def)
        return if (v is Vector3) v else def
    }

    override fun isVector(path: String): Boolean = get(path) is Vector3

    override fun getColor(path: String): ConfigColor? {
        val def = getDefault(path)
        return getColor(path, if (def is ConfigColor) def else null)
    }

    override fun getColor(path: String, def: ConfigColor?): ConfigColor? {
        val v = get(path, def)
        return if (v is ConfigColor) v else def
    }

    override fun isColor(path: String): Boolean = get(path) is ConfigColor

    override fun getConfigurationSection(path: String): ConfigurationSection? {
        var v: Any? = get(path, null)
        if (v != null) {
            return if (v is ConfigurationSection) v else null
        }
        v = get(path, getDefault(path))
        return if (v is ConfigurationSection) createSection(path) else null
    }

    override fun isConfigurationSection(path: String): Boolean = get(path) is ConfigurationSection

    protected fun isPrimitiveWrapper(input: Any?): Boolean = input is Int || input is Boolean ||
            input is Char || input is Byte || input is Short || input is Double || input is Long || input is Float

    protected fun getDefault(path: String): Any? {
        requireNotNull(path) { "Path cannot be null" }
        val root = getRoot() ?: return null
        val defaults = root.getDefaults() ?: return null
        return defaults.get(createPath(this, path))
    }

    protected fun mapChildrenKeys(
        output: MutableSet<String>,
        section: ConfigurationSection,
        deep: Boolean,
        anchor: ConfigurationSection
    ) {
        if (section is MemorySection) {
            for ((key, value) in section.map) {
                output.add(createPath(section, key, anchor))
                if (deep && value is ConfigurationSection) {
                    mapChildrenKeys(output, value, true, anchor)
                }
            }
        } else {
            for (key in section.getKeys(deep)) {
                output.add(createPath(section, key, anchor))
            }
        }
    }

    protected fun mapChildrenValues(
        output: MutableMap<String, Any?>,
        section: ConfigurationSection,
        deep: Boolean,
        anchor: ConfigurationSection
    ) {
        if (section is MemorySection) {
            for ((key, value) in section.map) {
                output[createPath(section, key, anchor)] = value
                if (value is ConfigurationSection && deep) {
                    mapChildrenValues(output, value, true, anchor)
                }
            }
        } else {
            for ((k, v) in section.getValues(deep)) {
                output[createPath(section, k, anchor)] = v
            }
        }
    }

    override fun toString(): String {
        val r = getRoot()
        return "${this::class.java.simpleName}[path='$fullPath', root='${r?.javaClass?.simpleName}']"
    }

    companion object {
        @JvmStatic
        fun createPath(section: ConfigurationSection?, key: String): String {
            return createPath(section, key, section?.getRoot())
        }

        @JvmStatic
        fun createPath(section: ConfigurationSection?, key: String?, relativeTo: ConfigurationSection?): String {
            requireNotNull(section) { "Cannot create path without a section" }
            val root = section.getRoot() ?: throw IllegalStateException("Cannot create path without a root")
            val separator = root.options().pathSeparator()
            val builder = StringBuilder()
            var parent: ConfigurationSection? = section
            while (parent != null && parent !== relativeTo) {
                if (builder.isNotEmpty()) builder.insert(0, separator)
                builder.insert(0, parent.getName())
                parent = parent.getParent()
            }
            if (!key.isNullOrEmpty()) {
                if (builder.isNotEmpty()) builder.append(separator)
                builder.append(key)
            }
            return builder.toString()
        }
    }
}
