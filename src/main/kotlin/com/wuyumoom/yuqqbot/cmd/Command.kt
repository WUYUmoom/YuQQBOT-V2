package com.wuyumoom.yuqqbot.cmd

import com.wuyumoom.yuqqbot.YuQQBot
import com.wuyumoom.yuqqbot.api.bot.botaction.BotAction
import com.wuyumoom.yuqqbot.config.ConfigManager
import com.wuyumoom.yuqqbot.event.BOTEvent
import org.jline.reader.LineReaderBuilder
import kotlin.system.exitProcess

object Command : Thread() {
    override fun run() {
        val build = LineReaderBuilder.builder().build()
        while (true) {
            val readLine = build.readLine()
            if (readLine != null) {
                when (readLine.trim().lowercase()) {
                    "reload" ,"重载" ->{
                        ConfigManager.reload()
                        BOTEvent.reload()
                        YuQQBot.sendMessage("&b重载成功")
                    }
                    "unbind" , "解绑" ->{
                        YuQQBot.sendMessage("&f请输入QQ号:")
                        val readLine1 = build.readLine()
                        if (readLine1 != null){
                            ConfigManager.onUnbind(readLine1)
                        }
                    }
                    "add","添加" -> {
                            YuQQBot.sendMessage("&f请输入插件名称:")
                            val readLine1 = build.readLine()
                            if (readLine1 != null) {
                                if (ConfigManager.cdk.keys.contains(readLine1)) {
                                    YuQQBot.sendMessage("&fQQ")
                                    val qq = build.readLine()
                                    val onAddCDK = ConfigManager.onAddCDK(qq, readLine1)
                                    BotAction.sendMsg("private", 1841375451, onAddCDK, true)
                                } else {
                                    YuQQBot.sendMessage("&c没有这个插件")
                                }
                            }
                        }

                    "stop" -> {
                        YuQQBot.sendMessage("正在关闭程序...")
                        ConfigManager.shutdown()
                        exitProcess(0)
                    }

                    else -> {
                        YuQQBot.sendMessage("未知命令: $readLine")
                    }
                }
            }
        }
    }
}