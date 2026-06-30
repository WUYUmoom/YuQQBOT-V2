package com.wuyumoom.yuqqbot.api.bot.jsonaction.base

class BaseMessage(
    var type: String = "",// "text" 或 "at"
    var data: BaseData? = null
)