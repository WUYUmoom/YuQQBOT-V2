package com.wuyumoom.yuqqbot.api.config

import com.wuyumoom.yuqqbot.api.config.file.FileConfiguration
import com.wuyumoom.yuqqbot.api.config.file.YamlConfiguration
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.function.Supplier
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Loads [YamlConfiguration] (org.config) the same way [org.bukkit.plugin.java.JavaPlugin.reloadConfig]
 * loads Bukkit YAML: disk file + optional `config.yml` embedded in the plugin JAR.
 */
class PluginConfigurationSupport private constructor() {
    companion object {


        /**
         * Same as [org.bukkit.plugin.java.JavaPlugin.saveDefaultConfig]: if `configFile` does not exist yet,
         * copies the embedded `config.yml` from the jar to disk (raw bytes, not re-serialized YAML).
         * No-op if the file already exists, if [defaultFromJar] is null, or if [defaultFromJar] returns null.
         */
        @JvmStatic
        fun saveDefaultConfig(
            configFile: File,
            defaultFromJar: Supplier<InputStream>?,
            logger: Logger
        ): YamlConfiguration {
            if (configFile.exists()) {
                return YamlConfiguration.loadConfiguration(configFile)
            }
            val raw = defaultFromJar?.get()
            if (raw == null) {
                return YamlConfiguration.loadConfiguration(configFile)
            }
            raw.use { inStream ->
                try {
                    val target = configFile.absoluteFile
                    target.parentFile?.takeIf { !it.exists() }?.mkdirs()
                    FileOutputStream(target).use { out ->
                        inStream.copyTo(out)
                    }
                } catch (e: IOException) {
                    logger.log(Level.SEVERE, "Could not save default config to " + configFile.absolutePath, e)
                }
            }
            return YamlConfiguration.loadConfiguration(configFile)
        }

        private fun saveNewConfigFile(config: YamlConfiguration, configFile: File, logger: Logger) {
            try {
                val target = configFile.absoluteFile
                target.parentFile?.takeIf { !it.exists() }?.mkdirs()
                config.options().copyDefaults(true)
                config.save(target)
            } catch (e: IOException) {
                logger.log(Level.SEVERE, "Could not create config file " + configFile.absolutePath, e)
            }
        }

        @Suppress("DEPRECATION")
        private fun loadYamlDefaultsFromJar(
            defConfigStream: InputStream,
            strictlyUtf8: Boolean,
            logger: Logger
        ): YamlConfiguration {
            if (strictlyUtf8 || FileConfiguration.UTF8_OVERRIDE) {
                return YamlConfiguration.loadConfiguration(InputStreamReader(defConfigStream, Charsets.UTF_8))
            }

            val contents = defConfigStream.readBytes()
            val text = String(contents, Charset.defaultCharset())
            if (text != String(contents, Charsets.UTF_8)) {
                logger.warning("Default system encoding may have misread config.yml from plugin jar")
            }

            val defConfig = YamlConfiguration()
            return try {
                defConfig.loadFromString(text)
                defConfig
            } catch (e: InvalidConfigurationException) {
                logger.log(Level.SEVERE, "Cannot load configuration from jar", e)
                YamlConfiguration()
            }
        }
    }
}
