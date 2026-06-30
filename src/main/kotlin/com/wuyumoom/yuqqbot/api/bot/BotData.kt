package com.wuyumoom.yuqqbot.api.bot

import com.wuyumoom.yuqqbot.config.ConfigManager
import com.wuyumoom.yuqqbot.ws.bot.WebSocket
import java.net.URI

object BotData {
    var port = ConfigManager.config.getInt("bot.port")
    var url: String = ConfigManager.config.getString("bot.url") ?: "127.0.0.1"
    //重连时间
    var taskTimeout: Long = ConfigManager.config.getLong("bot.taskTimeout")
    // BOTWebsocket客户端链接
    lateinit  var client: WebSocket
    // BOT验证码
    var verifyKey: String = ConfigManager.config.getString("bot.verifyKey")?:""
    //管理员
    var admin: MutableList<Long> = mutableListOf()

    //连接BOTWebsocket
    fun clientInit(){
        closeWebSocket()
        val headers = HashMap<String, String>()
        if (!verifyKey.isEmpty()){
            headers["Authorization"] = "Bearer $verifyKey"
        }
        try {
            // 使用异步方式执行连接
            Thread {
                // 开始尝试执行以下代码
                client = WebSocket(URI("ws://$url:$port"), headers) // 创建WebSocket实例，使用指定的URI和请求头
                client.connect() // 连接到WebSocket
            }.start()
        }catch (e:Exception){
            // 捕获可能出现的异常
            e.printStackTrace() // 打印异常堆栈信息
        }
    }
    //关闭BOTWebsocket
    fun closeWebSocket(){
        if (::client.isInitialized) {
            client.close()
        }
    }

}