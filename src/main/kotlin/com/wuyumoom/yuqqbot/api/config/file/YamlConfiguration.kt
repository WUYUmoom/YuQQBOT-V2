package com.wuyumoom.yuqqbot.api.config.file

import com.wuyumoom.yuqqbot.api.YamlRepresenter
import com.wuyumoom.yuqqbot.api.config.Configuration
import com.wuyumoom.yuqqbot.api.config.ConfigurationSection
import com.wuyumoom.yuqqbot.api.config.InvalidConfigurationException
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.error.YAMLException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Pattern
import kotlin.collections.iterator

/**
 * [Configuration] stored as YAML text.
 */
open class YamlConfiguration : FileConfiguration {
    constructor() : super()
    constructor(defaults: Configuration?) : super(defaults)

    private val loaderOptions = LoaderOptions()
    private val yamlOptions = DumperOptions()
    private val yamlRepresenter = YamlRepresenter()
    private val yaml = Yaml(YamlConstructor(), yamlRepresenter, yamlOptions, loaderOptions)

    override fun saveToString(): String {
        yamlOptions.indent = options().indent()
        yamlOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        yamlOptions.isAllowUnicode = SYSTEM_UTF
        yamlRepresenter.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK

        val header = buildHeader()
        var dump = yaml.dump(getValues(false))
        if (dump == BLANK_CONFIG) {
            dump = ""
        }
        return header + dump
    }

    @Throws(InvalidConfigurationException::class)
    override fun loadFromString(contents: String) {
        requireNotNull(contents) { "Contents cannot be null" }
        val input: Map<*, *>? = try {
            yaml.load(contents) as? Map<*, *>
        } catch (e: YAMLException) {
            throw InvalidConfigurationException(e)
        } catch (e: ClassCastException) {
            throw InvalidConfigurationException("Top level is not a Map.")
        }

        val header = parseHeader(contents)
        if (header.isNotEmpty()) {
            options().header(header)
        }

        if (input != null) {
            convertMapsToSections(input, this)
        }
    }

    protected fun convertMapsToSections(input: Map<*, *>, section: ConfigurationSection) {
        for ((k, v) in input) {
            val key = k.toString()
            if (v is Map<*, *>) {
                convertMapsToSections(v, section.createSection(key))
            } else {
                section.set(key, v)
            }
        }
    }

    protected fun parseHeader(input: String): String {
        val lines = Pattern.compile("\r?\n").split(input, -1)
        val result = StringBuilder()
        var readingHeader = true
        var foundHeader = false
        var i = 0
        while (i < lines.size && readingHeader) {
            val line = lines[i]
            when {
                line.startsWith(COMMENT_PREFIX) -> {
                    if (i > 0) result.append("\n")
                    if (line.length > COMMENT_PREFIX.length) {
                        result.append(line.substring(COMMENT_PREFIX.length))
                    }
                    foundHeader = true
                }
                foundHeader && line.isEmpty() -> result.append("\n")
                foundHeader -> readingHeader = false
            }
            i++
        }
        return result.toString()
    }

    override fun buildHeader(): String {
        var header = options().header()
        if (options().copyHeader()) {
            val def = getDefaults()
            if (def != null && def is FileConfiguration) {
                val defaultsHeader = def.buildHeader()
                if (!defaultsHeader.isNullOrEmpty()) {
                    return defaultsHeader
                }
            }
        }
        if (header == null) return ""
        val builder = StringBuilder()
        val lines = Pattern.compile("\r?\n").split(header, -1)
        var startedHeader = false
        for (i in lines.indices.reversed()) {
            builder.insert(0, "\n")
            if (startedHeader || lines[i].isNotEmpty()) {
                builder.insert(0, lines[i])
                builder.insert(0, COMMENT_PREFIX)
                startedHeader = true
            }
        }
        return builder.toString()
    }

    override fun options(): YamlConfigurationOptions {
        if (memoOptions == null) {
            memoOptions = YamlConfigurationOptions(this)
        }
        return memoOptions as YamlConfigurationOptions
    }

    companion object {
        private const val COMMENT_PREFIX = "# "
        private const val BLANK_CONFIG = "{}\n"
        private val log = Logger.getLogger(YamlConfiguration::class.java.name)

        @JvmStatic
        fun loadConfiguration(file: File): YamlConfiguration {
            requireNotNull(file) { "File cannot be null" }
            val config = YamlConfiguration()
            try {
                config.load(file)
            } catch (_: FileNotFoundException) {
            } catch (ex: IOException) {
                log.log(Level.SEVERE, "Cannot load $file", ex)
            } catch (ex: InvalidConfigurationException) {
                log.log(Level.SEVERE, "Cannot load $file", ex)
            }
            return config
        }

        @Deprecated("Does not properly consider encoding")
        @JvmStatic
        fun loadConfiguration(stream: InputStream): YamlConfiguration {
            requireNotNull(stream) { "Stream cannot be null" }
            val config = YamlConfiguration()
            try {
                config.load(stream)
            } catch (ex: IOException) {
                log.log(Level.SEVERE, "Cannot load configuration from stream", ex)
            } catch (ex: InvalidConfigurationException) {
                log.log(Level.SEVERE, "Cannot load configuration from stream", ex)
            }
            return config
        }

        @JvmStatic
        fun loadConfiguration(reader: Reader): YamlConfiguration {
            requireNotNull(reader) { "Reader cannot be null" }
            val config = YamlConfiguration()
            try {
                config.load(reader)
            } catch (ex: IOException) {
                log.log(Level.SEVERE, "Cannot load configuration from reader", ex)
            } catch (ex: InvalidConfigurationException) {
                log.log(Level.SEVERE, "Cannot load configuration from reader", ex)
            }
            return config
        }
    }
}
