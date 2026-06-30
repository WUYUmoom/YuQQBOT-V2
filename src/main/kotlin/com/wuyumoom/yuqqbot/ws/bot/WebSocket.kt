package com.wuyumoom.yuqqbot.ws.bot

import com.alibaba.fastjson2.JSONObject
import com.wuyumoom.yuqqbot.YuQQBot
import com.wuyumoom.yuqqbot.api.bot.BotData
import com.wuyumoom.yuqqbot.api.bot.jsonaction.JSONAction
import com.wuyumoom.yuqqbot.config.ConfigManager
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class WebSocket(serverUri: URI, headers: HashMap<String, String>) : WebSocketClient(serverUri,headers) {
    override fun onOpen(serverHandshake: ServerHandshake?) {
        YuQQBot.sendMessage("&1机器人框架对接成功!")
    }

    override fun onMessage(data: String) {
        if (ConfigManager.isDeBug) {
            YuQQBot.sendMessage("&f接收到QQ数据:\n$data\n") //调试信息
        }
        val json = JSONObject.parseObject(data)
        JSONAction.analysisAction( json,data)

    }
    private val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val taskCounter = AtomicInteger(0)
    override fun onClose(code: Int, reason: String, remote: Boolean) {
        YuQQBot.sendMessage("&c机器人框架已断开连接!")
        executorService.scheduleAtFixedRate({
            val count = taskCounter.incrementAndGet()
            if (count <= 10) {
                YuQQBot.sendMessage("&c正在重连第 &f$count &c次")
                reconnect()
            } else {
                YuQQBot.sendMessage("&c超过最大重连次数，停止重连")
                executorService.shutdown()
            }
        }, 0, BotData.taskTimeout, TimeUnit.SECONDS)
    }
    override fun onError(ex: Exception) {
        ex.printStackTrace()
        YuQQBot.sendMessage("&c对接框架出现错误!")
    }
    fun sendData(data: String) {
        send(data)
    }
}