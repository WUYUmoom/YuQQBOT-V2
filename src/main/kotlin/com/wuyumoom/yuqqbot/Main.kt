package com.wuyumoom.yuqqbot

import com.wuyumoom.yuqqbot.api.config.PluginConfigurationSupport
import com.wuyumoom.yuqqbot.api.config.PluginDataFolder
import com.wuyumoom.yuqqbot.api.config.file.YamlConfiguration
import java.io.File
import java.util.logging.Logger

fun main() {
    var config: YamlConfiguration = PluginConfigurationSupport.saveDefaultConfig(
        configFile = File(PluginDataFolder.pluginsSubdir(""), "config.yml"),
        defaultFromJar = { Thread.currentThread().contextClassLoader.getResourceAsStream("config.yml") },
        logger = Logger.getLogger("yuqqbot")
    )
}