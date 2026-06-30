package com.wuyumoom.yuqqbot.ws.server

enum class Type {
    verify{
        override fun execute(handler: ClientHandler,split: List<String>){
            handler.plugin = split[1]
            handler.QQ = split[2]
            handler.CDK = split[3]
            handler.verifyCDK()
        }
    };
    abstract fun execute(handler: ClientHandler,split: List<String>)
}