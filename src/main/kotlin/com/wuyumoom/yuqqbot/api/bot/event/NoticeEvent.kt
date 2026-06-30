package com.wuyumoom.yuqqbot.api.bot.event

import com.wuyumoom.yuqqbot.api.event.Event


open class NoticeEvent(
    var time: Long = 0,
    var self_id: Long = 0,
    var post_type: String = "",
    var notice_type: String = "",
    var operator_id: Long = 0,
    var sub_type: String = "",
    var group_id: Long = 0,
    var user_id: Long = 0
): Event()
//好友消息撤回事件
class FriendRecallEvent: NoticeEvent(){
    var message_id : Long = 0
}
//群消息撤回事件
class GroupRecallEvent: NoticeEvent(){
    var message_id : Long = 0
}
//群成员增加事件
class GroupIncreaseEvent: NoticeEvent(){
}
//群成员减少事件
class GroupDecreaseEvent: NoticeEvent(){}
//管理员变动事件
class GroupAdminEvent: NoticeEvent(){}
//群文件上传事件
class GroupUploadEvent: NoticeEvent(){
    var file: Object= Object()
    class Object{
        var id: String = ""
        var size: Long = 0
        var name: String = ""
        var busid: Long = 0
    }
}
//群禁言事件
class GroupBanEvent: NoticeEvent(){
    //禁言时长
    var duration: Long = 0
}
//好友添加事件 目前未知具体作用
class NoticeFriendAddEvent: NoticeEvent(){}
//群成员名片更新事件
class GroupCardEvent: NoticeEvent(){
    //新名片
    var card_new: String = ""
    //旧名片
    var card_old: String = ""
}
//接收离线文件事件
class OfflineFileEvent: NoticeEvent(){
    var file = Object()
    class Object{
        var id: String = ""
        var size: Long = 0
        var name: String = ""
        var busid: Long = 0
    }
}
