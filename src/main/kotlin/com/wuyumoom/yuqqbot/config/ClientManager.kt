package com.wuyumoom.yuqqbot.config

import com.wuyumoom.yuqqbot.ws.server.ClientHandler

object ClientManager {
    val clientList = mutableListOf<ClientHandler>()
    val QQ = mutableMapOf<String,MutableList<ClientHandler>>()
    val IP = mutableMapOf<String,MutableList<ClientHandler>>()
    val lose = mutableListOf<ClientHandler>()
    fun addQQClient(qq: String,client: ClientHandler){
        if (QQ.containsKey(qq)){
            QQ[qq]?.add(client)
        }else{
            QQ[qq] = mutableListOf(client)
        }
    }
    fun addIpClient(ip: String,client: ClientHandler){
        if (IP.containsKey(ip)){
            IP[ip]?.add(client)
        }else{
            IP[ip] = mutableListOf(client)
        }
    }
}