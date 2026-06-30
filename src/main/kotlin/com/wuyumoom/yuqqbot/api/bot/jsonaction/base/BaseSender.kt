package com.wuyumoom.yuqqbot.api.bot.jsonaction.base

class BaseSender(
    var user_id: Long = 0,
    var nickname: String = "",
    var card: String = "",
    var role: String = "", // 只有在群组消息中使用
    var title: String = "" // 只有在群组消息中使用
)
