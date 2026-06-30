package com.wuyumoom.yuqqbot.api.bot.botaction

import com.alibaba.fastjson2.JSONObject
import com.wuyumoom.yuqqbot.api.bot.BotData
import com.wuyumoom.yuqqbot.botaction.base.Params
import com.wuyumoom.yuqqbot.botaction.base.Request
import com.wuyumoom.yuqqbot.botaction.jsondata.AddFriend
import com.wuyumoom.yuqqbot.botaction.jsondata.GroupRequest
import com.wuyumoom.yuqqbot.botaction.jsondata.MessageId

object BotAction {
    //获取登录号信息
    fun getLoginInfo() {
        val request = Request<Any>()
        request.action = "get_login_info"
        BotData.client.send(JSONObject.toJSONString(request))
    }
    //发送消息
    fun sendMsg(message_type: String, user_id: Long, message: String, auto_escape: Boolean, group_id: Long? = null){
        val paramsRequest: Request<Params> = Request()
        paramsRequest.action = "send_msg"
        val params = Params()
        params.message = message
        params.user_id = user_id
        params.group_id = group_id
        params.message_type = message_type
        params.isAuto_escape = auto_escape
        paramsRequest.params = params
        BotData.client.send(JSONObject.toJSONString(paramsRequest))
    }
    //撤回消息
    fun deleteMsg(message_id: Long){
        val paramsRequest: Request<MessageId> = Request()
        paramsRequest.action = "delete_msg"
        val messageId = MessageId()
        messageId.message_id = message_id
        paramsRequest.params = messageId
        BotData.client.send(JSONObject.toJSONString(paramsRequest))
    }
    //设置是否同意好友添加
    fun setFriendAddRequest(approve: Boolean ,remark: String= "", flag: String) {
        val paramsRequest: Request<AddFriend> = Request()
        paramsRequest.action = "set_friend_add_request"
        val groupRequest = AddFriend()
        groupRequest.approve = approve
        groupRequest.flag = flag
        groupRequest.remark = remark
        paramsRequest.params = groupRequest
        BotData.client.send(JSONObject.toJSONString(paramsRequest))
    }
    //处理加群请求／邀请
    fun setGroupAddRequest(approve: Boolean, reason: String = "", sub_type: String = "", flag: String) {
        val request: Request<GroupRequest> = Request()
        request.action = "set_group_add_request"
        val groupRequest = GroupRequest()
        groupRequest.approve = approve
        groupRequest.flag = flag
        groupRequest.reason = reason
        groupRequest.sub_type = sub_type
        request.params = groupRequest
        BotData.client.send(JSONObject.toJSONString(request))
    }

}