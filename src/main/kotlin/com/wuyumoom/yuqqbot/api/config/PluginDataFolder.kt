package com.wuyumoom.yuqqbot.api.config

import java.io.File

/**
 * 在不使用 Bukkit [org.bukkit.plugin.Plugin.getDataFolder] 时，按常见服务端目录约定解析插件数据目录。
 *
 * 约定与 CraftBukkit/Paper 一致：服务器工作目录下的 `plugins/<插件名>/`。
 * 实际是否创建目录由调用方负责（例如 [File.mkdirs]）。
 */
object PluginDataFolder {

    /**
     * `plugins/<pluginName>/`（相对当前工作目录）。
     */
    fun pluginsSubdir(pluginName: String): File =
        File(File("config"), pluginName).normalize()

    /**
     * 从 JVM 当前工作目录 [user.dir] 起拼接路径，例如 `workingDirRelative("plugins", "MyPlugin")`。
     */
    fun workingDirRelative(vararg segments: String): File {
        var base = File(System.getProperty("user.dir") ?: ".")
        for (s in segments) {
            base = File(base, s)
        }
        return base.normalize()
    }

    /**
     * 与 [pluginsSubdir] 相同，显式指定根目录（例如自定义服务器根路径）。
     */
    fun underRoot(serverRoot: File, pluginName: String): File =
        File(File(serverRoot, "plugins"), pluginName).normalize()
}
