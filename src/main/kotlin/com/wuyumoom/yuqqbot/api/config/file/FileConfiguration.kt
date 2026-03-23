package com.wuyumoom.yuqqbot.api.config.file

import com.wuyumoom.yuqqbot.api.config.Configuration
import com.wuyumoom.yuqqbot.api.config.InvalidConfigurationException
import com.wuyumoom.yuqqbot.api.config.MemoryConfiguration
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Reader
import java.io.Writer
import java.nio.charset.Charset
import java.util.Base64
import java.nio.file.Files as NioFiles

/**
 * Base class for file-backed [Configuration] implementations.
 */
abstract class FileConfiguration : MemoryConfiguration {
    constructor() : super()
    constructor(defaults: Configuration?) : super(defaults)

    @Throws(IOException::class)
    fun save(file: File) {
        requireNotNull(file) { "File cannot be null" }
        file.parentFile?.let { NioFiles.createDirectories(it.toPath()) }
        val data = saveToString()
        val charset: Charset = if (UTF8_OVERRIDE && !UTF_BIG) Charsets.UTF_8 else Charset.defaultCharset()
        val writer: Writer = OutputStreamWriter(FileOutputStream(file), charset)
        writer.use { it.write(data) }
    }

    @Throws(IOException::class)
    fun save(file: String) {
        requireNotNull(file) { "File cannot be null" }
        save(File(file))
    }

    abstract fun saveToString(): String

    @Throws(FileNotFoundException::class, IOException::class, InvalidConfigurationException::class)
    fun load(file: File) {
        requireNotNull(file) { "File cannot be null" }
        val stream = FileInputStream(file)
        val charset: Charset = if (UTF8_OVERRIDE && !UTF_BIG) Charsets.UTF_8 else Charset.defaultCharset()
        load(InputStreamReader(stream, charset))
    }

    @Deprecated("Does not consider encoding; use load(Reader).")
    @Throws(IOException::class, InvalidConfigurationException::class)
    fun load(stream: InputStream) {
        requireNotNull(stream) { "Stream cannot be null" }
        val charset: Charset = if (UTF8_OVERRIDE) Charsets.UTF_8 else Charset.defaultCharset()
        load(InputStreamReader(stream, charset))
    }

    @Throws(IOException::class, InvalidConfigurationException::class)
    fun load(reader: Reader) {
        val input = reader as? BufferedReader ?: BufferedReader(reader)
        val builder = StringBuilder()
        input.use { r ->
            r.forEachLine { line ->
                builder.append(line)
                builder.append('\n')
            }
        }
        loadFromString(builder.toString())
    }

    @Throws(FileNotFoundException::class, IOException::class, InvalidConfigurationException::class)
    fun load(file: String) {
        requireNotNull(file) { "File cannot be null" }
        load(File(file))
    }

    @Throws(InvalidConfigurationException::class)
    abstract fun loadFromString(contents: String)

    abstract fun buildHeader(): String

    override fun options(): FileConfigurationOptions {
        if (memoOptions == null) {
            memoOptions = FileConfigurationOptions(this)
        }
        return memoOptions as FileConfigurationOptions
    }

    companion object {
        @JvmField
        val UTF8_OVERRIDE: Boolean

        @JvmField
        val UTF_BIG: Boolean

        @JvmField
        val SYSTEM_UTF: Boolean

        init {
            val testBytes = Base64.getDecoder().decode(
                "ICEiIyQlJicoKSorLC0uLzAxMjM0NTY3ODk6Ozw9Pj9AQUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpbXF1eX2BhYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ent8fX4NCg==".toByteArray(Charsets.US_ASCII)
            )
            val testString =
                " !\"#\$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\r\n"
            val defaultCharset = Charset.defaultCharset()
            val resultString = String(testBytes, defaultCharset)
            val trueUTF = defaultCharset.name().contains("UTF")
            UTF8_OVERRIDE = testString != resultString || defaultCharset == Charset.forName("US-ASCII")
            SYSTEM_UTF = trueUTF || UTF8_OVERRIDE
            UTF_BIG = trueUTF && UTF8_OVERRIDE
        }
    }
}
