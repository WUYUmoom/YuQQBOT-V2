package com.wuyumoom.yuqqbot.api.bot.event


import com.wuyumoom.yuqqbot.api.bot.botaction.BotAction
import com.wuyumoom.yuqqbot.api.event.Event

open class RequestEvent(
    var time: Long = 0,
    var self_id: Long = 0,
    var post_type: String = "",
    var request_type: String = "",
    var sub_type: String = "",
    var comment: String = "",
    var flag: String = "",
    var user_id: Long? = null,
    var group_id: Long? = null
): Event()
//好友添加事件
class FriendAddEvent: RequestEvent(){
    //设置是否同意
    fun setFriendRequest(approve: Boolean, reason: String = "") {
        BotAction.setFriendAddRequest(approve, reason, flag)
    }
}
//加群请求事件
class GroupJoinEvent: RequestEvent() {
    fun setGroupRequest(approve: Boolean, reason: String = "") {
        BotAction.setGroupAddRequest(approve, reason, sub_type, flag)
    }
}
// 加群邀请事件
class GroupInviteEvent : RequestEvent() {
    fun setGroupRequest(approve: Boolean, reason: String = "") {
        BotAction.setGroupAddRequest(approve, reason, sub_type, flag)
    }
}