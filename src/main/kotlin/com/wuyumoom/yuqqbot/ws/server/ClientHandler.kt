package com.wuyumoom.yuqqbot.ws.server

import com.wuyumoom.yuqqbot.YuQQBot
import com.wuyumoom.yuqqbot.config.ClientManager
import com.wuyumoom.yuqqbot.config.ConfigManager
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

class ClientHandler(
    val socket: Socket,
) : Runnable {
    val ip: String = socket.inetAddress.hostAddress
    var QQ: String = "未设置"
    var CDK: String = "未设置"
    var plugin: String = "未设置"
    val  output = DataOutputStream(socket.getOutputStream())

    override fun run() {
        try {
            ClientManager.clientList.add(this)
            val input = DataInputStream(socket.getInputStream())

            while (!socket.isClosed && socket.isConnected && !Thread.currentThread().isInterrupted) {
                val readUTF = try {
                    input.readUTF()
                } catch (e: IOException) {
                    YuQQBot.sendMessage("&c客户端[$QQ]连接断开: ${e.message}")
                    break
                }

                if (readUTF.isEmpty()) {
                    YuQQBot.sendMessage("&c客户端[$QQ]发送空数据，连接断开")
                    break
                }

                val split: List<String> = readUTF.split(",")
                if (split.size < 3) {
                    YuQQBot.sendMessage("&c请求参数不足&f")
                    continue
                }

                try {
                    YuQQBot.sendMessage("&b收到数据${split}")
                    val valueOf = Type.valueOf(split[0])
                    valueOf.execute(this, split)
                } catch (e: Exception) {
                    ClientManager.lose.add(this)
                    YuQQBot.sendMessage("&c请求参数错误&f: ${e.message}")
                    continue
                }
            }
        } catch (e: IOException) {
            YuQQBot.sendMessage("&c客户端[$QQ]IO异常: ${e.message}")
        } finally {
            onDisconnect()
        }
    }

    fun onDisconnect() {
        YuQQBot.sendMessage("&b客户端[$QQ]已断开连接")
        ClientManager.clientList.remove(this)
        ClientManager.QQ[QQ]?.remove(this)
        ClientManager.IP[ip]?.remove(this)

        if (ClientManager.QQ[QQ]?.isEmpty() == true) {
            ClientManager.QQ.remove(QQ)
        }
        if (ClientManager.IP[ip]?.isEmpty() == true) {
            ClientManager.IP.remove(ip)
        }

        try {
            if (!socket.isClosed) {
                socket.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun verifyCDK() {
        val data = ConfigManager.cdk[plugin] ?: return
        val strings = data.CDK[QQ] ?: return
        val verified = strings.any { cdk ->
            val split = cdk.split(",")
            when (split.size) {
                1 -> split[0] == CDK
                2 -> split[0] == CDK && split[1] == ip
                else -> false
            }
        }
        if (verified) {
            ClientManager.addQQClient(QQ, this)
            ClientManager.addIpClient(ip, this)
            YuQQBot.sendMessage("&b客户端[$QQ],插件${plugin},IP:${ip}验证成功")
            sendMessageToClient("true")
        } else {
            ClientManager.lose.add(this)
            YuQQBot.sendMessage("&c客户端[$QQ],插件${plugin},IP:${ip}验证失败")
            sendMessageToClient("false")
        }
    }
    /**
     * 向客户端发送消息
     * @param message 要发送的消息内容
     * @return 是否发送成功
     */
    fun sendMessageToClient(message: String): Boolean {
        return try {
            if (socket.isClosed) {
                YuQQBot.sendMessage("&c客户端[$QQ]已断开，无法发送消息")
                false
            } else {
                output.writeUTF(message)
                output.flush()
                YuQQBot.sendMessage("&b已向客户端[$QQ]发送消息: $message")
                true
            }
        } catch (e: IOException) {
            YuQQBot.sendMessage("&c向客户端[$QQ]发送消息失败: ${e.message}")
            false
        }
    }
}