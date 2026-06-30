package com.wuyumoom.yuqqbot

import com.wuyumoom.yuqqbot.api.bot.BotData
import com.wuyumoom.yuqqbot.api.event.EventManager
import com.wuyumoom.yuqqbot.cmd.Command
import com.wuyumoom.yuqqbot.config.ConfigManager
import com.wuyumoom.yuqqbot.event.BOTEvent
import com.wuyumoom.yuqqbot.ws.server.ClientHandler
import mu.KotlinLogging
import java.net.ServerSocket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

object YuQQBot {
    private val logger = KotlinLogging.logger {}

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val LOGO = arrayOf(
        "=====================================================",
        "&c __   __      ___   ___  ____        _   ",
        "&c \\ \\ / /   _ / _ \\ / _ \\| __ )  ___ | |_ ",
        "&c  \\ V / | | | | | | | | |  _ \\ / _ \\| __|",
        "&c   | || |_| | |_| | |_| | |_) | (_) | |_ ",
        "&c   |_| \\__,_|\\__\\_\\\\__\\_\\____/ \\___/ \\__|",
        "&e&l语之验证QQ系统 &6&l启动完成！",
        "&e&l作者 : 姬无语 &6&lQQ1841375451",
        "=====================================================",
    )
    @JvmStatic
    fun main(args: Array<String>) {
        val stringList = ConfigManager.config.getStringList("bot.admin")
        for (admin in stringList) {
            BotData.admin.add(admin.toLong())
        }
        BotData.clientInit()

        //注册事件监听器
        EventManager.registerEvents(BOTEvent())
        ConfigManager.loadCDK()
        ConfigManager.startAutoSaveTask()
        Command.start()
        val executor = Executors.newCachedThreadPool()
        sendMessage(LOGO)
        val int = ConfigManager.config.getInt("VerifySocket.port")
        val verifySocket = ServerSocket(int)
        executor.submit<Any> {
            sendMessage("&a验证服务端已启动，监听端口: &f$int")
            while (true) {
                try {
                    val socket = verifySocket.accept()
                    executor.execute(ClientHandler(socket = socket))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    private fun sendMessage(message: Array<String>) {
        for (msg in message) {
            val string = "[${
                LocalDateTime.now().format(formatter)
            } INFO] ${convertColorCodes("&f[&e语之BOT&f]")} ${convertColorCodes(msg)}"
            println(string)
            // 移除颜色代码后记录日志
            logger.info(removeColorCodes(string))
        }
    }
    fun sendMessage(message: String) {
        val string = "[${
            LocalDateTime.now().format(formatter)
        } INFO] ${convertColorCodes("&f[&e语之BOT&f]")} ${convertColorCodes(message)}"
        println(string)
        // 移除颜色代码后记录日志
        logger.info(removeColorCodes(string))
    }

    private fun removeColorCodes(text: String): String {
        var result = text
        result = result.replace(Regex("\u001B\\[[0-9;]*m"), "")
        return result
    }

    /**
     * 将Minecraft颜色代码转换为ANSI颜色代码
     */
    private fun convertColorCodes(text: String): String {
        var result = text
        val colorMap = mapOf(
            "&0" to "\u001B[30m", // 黑色
            "&1" to "\u001B[34m", // 深蓝色
            "&2" to "\u001B[32m", // 深绿色
            "&3" to "\u001B[36m", // 湖蓝色
            "&4" to "\u001B[31m", // 深红色
            "&5" to "\u001B[35m", // 紫色
            "&6" to "\u001B[33m", // 金色
            "&7" to "\u001B[37m", // 灰色
            "&8" to "\u001B[90m", // 深灰色
            "&9" to "\u001B[94m", // 蓝色
            "&a" to "\u001B[92m", // 绿色
            "&b" to "\u001B[96m", // 天蓝色
            "&c" to "\u001B[91m", // 红色
            "&d" to "\u001B[95m", // 粉红色
            "&e" to "\u001B[93m", // 黄色
            "&f" to "\u001B[97m", // 白色
            "&l" to "\u001B[1m",  // 粗体
            "&m" to "\u001B[9m",  // 删除线
            "&n" to "\u001B[4m",  // 下划线
            "&o" to "\u001B[3m",  // 斜体
            "&r" to "\u001B[0m"   // 重置
        )

        for ((minecraftCode, ansiCode) in colorMap) {
            result = result.replace(minecraftCode, ansiCode)
        }

        // 在字符串末尾添加重置代码
        if (result.contains("\u001B[")) {
            result += "\u001B[0m"
        }

        return result
    }
}