package com.wuyumoom.yuqqbot.api.bot.event

import com.wuyumoom.yuqqbot.api.bot.botaction.BotAction
import com.wuyumoom.yuqqbot.api.bot.jsonaction.base.BaseMessage
import com.wuyumoom.yuqqbot.api.bot.jsonaction.base.BaseSender
import com.wuyumoom.yuqqbot.api.event.Event

open class MessageEvent(
    var self_id: Long = 0,
    var user_id: Long = 0,
    var time: Long = 0,
    var message_id: Long = 0,
    var real_id: Long = 0,
    var message_seq: Long = 0,
    var message_type: String = "",
    var sender: BaseSender? = null,
    var raw_message: String = "",
    var font: Int = 0,
    var sub_type: String = "",
    var message: Array<BaseMessage>? = null,
    var message_format: String = "",
    var post_type: String = ""
) : Event() {
    override fun toString(): String {
        return "接收QQ:$self_id\n发送QQ:$user_id\n发送时间:$time\n消息id:$message_id\n消息seq:$message_seq\n发送内容:\n$raw_message"
    }
}
//私聊消息事件
class PrivateMessageEvent: MessageEvent(){
    fun sendMessage(message: String, auto_escape: Boolean = false,user_id: Long = this.user_id){
        BotAction.sendMsg("private", user_id, message, auto_escape)
    }
}
//群消息事件
class GroupMessageEvent: MessageEvent(){
    var group_id: Long = 0 // 群组 ID
    fun sendMessage(message: String, auto_escape: Boolean = false, user_id: Long = this.user_id, group_id: Long = this.group_id){
        BotAction.sendMsg("group", user_id, message, auto_escape, group_id)
    }
    fun replyMessage(message: String, user_id: Long = this.user_id, group_id: Long = this.group_id){
        val string = "[CQ:reply,id=$message_id] $message"
        BotAction.sendMsg("group ", user_id, message, false, group_id)
    }
    fun deleteMsg(){
        BotAction.deleteMsg(message_id)
    }
    fun privateChat(message: String, auto_escape: Boolean = false,group_id: Long = this.group_id){
        BotAction.sendMsg("private", user_id, message, auto_escape, group_id)
    }
}
