package com.wuyumoom.yuqqbot.api.bot.jsonaction.base

 class BaseData(
    var text: String = "",
    // 只有在 type 为 "text" 时使用
    var qq: String = "",
    // 只有在 type 为 "at" 时使用
    var name: String = ""
    // 只有在 type 为 "at" 时使用
)