package com.wuyumoom.yuqqbot.config

import com.wuyumoom.yuqqbot.YuQQBot
import com.wuyumoom.yuqqbot.api.config.file.YamlConfiguration
import com.wuyumoom.yuqqbot.plugin.PluginData
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object ConfigManager {
    val currentDir = System.getProperty("user.dir")
    var config = YamlConfiguration.loadConfiguration(File(currentDir, "config.yml"))
    var isDeBug: Boolean = config.getBoolean("bot.isDeBug")
    val cdk = HashMap<String, PluginData>()
    val group: MutableMap<Long, String> = HashMap()
    val cdkDir = File(currentDir, "cdk.yml")
    var cdkYML = YamlConfiguration.loadConfiguration(cdkDir)

    // 加载CDK
    fun loadCDK() {
        config.getConfigurationSection("VerifySocket.group")?.getKeys(false)?.forEach {
            group[it.toLong()] = config.getString("VerifySocket.group.$it") ?: ""
        }
        cdkYML.getKeys(false).forEach { pluginName ->
            val map = cdk[pluginName]
            if (map == null) {
                val configurationSection = cdkYML.getConfigurationSection(pluginName) ?: return@forEach
                cdk[pluginName] = PluginData.create(config = configurationSection)
            }
        }
    }

    // 重载
    fun reload() {
        cdk.clear()
        group.clear()
        cdkYML = YamlConfiguration.loadConfiguration(cdkDir)
        config = YamlConfiguration.loadConfiguration(File(currentDir, "config.yml"))
        isDeBug = config.getBoolean("bot.isDeBug")
    }

    //解绑
    fun onUnbind(QQ: String) {
        cdk.values.forEach { pluginData ->
            pluginData.CDK.forEach { (qq, list) ->
                if (qq == QQ) {
                    val newList = list.map { cdk ->
                        val split = cdk.split(",")
                        split[0]
                    }.toMutableList()

                    pluginData.CDK[qq] = newList
                    cdkYML.set("${pluginData.Plugin}.QQ$QQ", newList)
                }
            }
        }
        YuQQBot.sendMessage("&b用户&2${QQ}&b:解绑成功")
    }
    //获取CDK
    fun onGetCDK(QQ: String): MutableList<String>{
        YuQQBot.sendMessage("&b用户&2${QQ}&b:查询成功")
        val stringList = mutableListOf<String>()
        cdk.values.forEach { pluginData ->
            pluginData.CDK.forEach { (qq, list) ->
                if (qq == QQ) {
                    list.forEach { cdk ->
                        val split = cdk.split(",")
                        val sb = StringBuilder()
                        sb.append("-------------------------------\n")
                        sb.append("[插件] ${pluginData.Plugin}\n")
                        sb.append("授权码：${split[0]}\n")
                        sb.append("绑定QQ：${QQ.removePrefix("QQ")}\n")
                        if (split.size == 2) {
                            sb.append("绑定地址：${split[1]}")
                        } else {
                            sb.append("绑定地址：无绑定")
                        }
                        stringList.add(sb.toString())
                    }
                }
            }
        }
        return stringList
    }
    //添加CDK
    fun onAddCDK(QQ: String,plugin: String) : String{
        val data = cdk[plugin]
        if (data == null){
            YuQQBot.sendMessage("&c没有&f[&b$plugin&f]&c这个插件,用户&f[$QQ&f]&c添加失败")
            return "&c没有&f[&b$plugin&f]&c这个插件,用户&f[$QQ&f]&c添加失败"
        }else{
            val strings = data.CDK[QQ]
            val cdk = generateRandomString(16)
            if (strings == null){
                val list = mutableListOf<String>()
                list.add(cdk)
                data.CDK[QQ] = list
                YuQQBot.sendMessage("&b用户&2${QQ}&b:添加成功")
            }else{
                strings.add(cdk)
            }
            cdkYML.set("$plugin.QQ$QQ",data.CDK[QQ])
            YuQQBot.sendMessage("&b用户")
            YuQQBot.sendMessage("&c${QQ}")
            YuQQBot.sendMessage("&b插件:")
            YuQQBot.sendMessage("&2${plugin}")
            YuQQBot.sendMessage("&bCDK:")
            YuQQBot.sendMessage("&c$cdk")
            YuQQBot.sendMessage("&b添加成功！")
            return "$plugin:\n$cdk\n$QQ"
        }
    }
    fun generateRandomString(length: Int): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val sb = StringBuilder(length)
        val random = Random()
        for (i in 0..<length) {
            val index = random.nextInt(characters.length)
            sb.append(characters[index])
        }
        return sb.toString()
    }
    fun saveCdkToFile() {
        try {
            cdkYML.save(cdkDir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private var saveTask: ScheduledFuture<*>? = null
    private val scheduler = Executors.newScheduledThreadPool(1) { r ->
        val thread = Thread(r, "CDK-Save-Thread")
        thread.isDaemon = true
        thread
    }

    fun startAutoSaveTask() {
        stopAutoSaveTask()

        val intervalMinutes = config.getLong("bot.cdkSaveInterval", 5).toInt()
        saveTask = scheduler.scheduleAtFixedRate(
            {
                try {
                    saveCdkToFile()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            intervalMinutes.toLong(),
            intervalMinutes.toLong(),
            TimeUnit.MINUTES
        )
    }

    private fun stopAutoSaveTask() {
        saveTask?.cancel(false)
        saveTask = null
    }

    fun shutdown() {
        stopAutoSaveTask()
        scheduler.shutdown()
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow()
            }
        } catch (e: InterruptedException) {
            scheduler.shutdownNow()
        }
    }

}