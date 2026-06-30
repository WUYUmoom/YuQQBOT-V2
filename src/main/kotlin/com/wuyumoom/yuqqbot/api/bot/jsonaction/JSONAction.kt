package com.wuyumoom.yuqqbot.api.bot.jsonaction

import com.alibaba.fastjson2.JSONObject
import com.wuyumoom.yuqqbot.api.bot.event.FriendAddEvent
import com.wuyumoom.yuqqbot.api.bot.event.FriendRecallEvent
import com.wuyumoom.yuqqbot.api.bot.event.GroupAdminEvent
import com.wuyumoom.yuqqbot.api.bot.event.GroupBanEvent
import com.wuyumoom.yuqqbot.api.bot.event.GroupCardEvent
import com.wuyumoom.yuqqbot.api.bot.event.GroupDecreaseEvent
import com.wuyumoom.yuqqbot.api.bot.event.GroupIncreaseEvent
import com.wuyumoom.yuqqbot.api.bot.event.GroupInviteEvent
import com.wuyumoom.yuqqbot.api.bot.event.GroupJoinEvent
import com.wuyumoom.yuqqbot.api.bot.event.GroupMessageEvent
import com.wuyumoom.yuqqbot.api.bot.event.GroupRecallEvent
import com.wuyumoom.yuqqbot.api.bot.event.GroupUploadEvent
import com.wuyumoom.yuqqbot.api.bot.event.NoticeFriendAddEvent
import com.wuyumoom.yuqqbot.api.bot.event.OfflineFileEvent
import com.wuyumoom.yuqqbot.api.bot.event.PrivateMessageEvent
import com.wuyumoom.yuqqbot.api.event.EventManager

object JSONAction {
    //解析动作
    fun analysisAction(action: JSONObject, data: String) {
        //获取消息类型
        val postType: String = action.getString("post_type")?: "空"
        if (postType == "空"){
            return
        }
        when (postType) {
            //消息事件
            "message" ->{
                when (action.getString("message_type")){
                    //私聊消息
                    "private" ->{
                        val parseObject = JSONObject.parseObject(data, PrivateMessageEvent::class.java)
                        EventManager.callEvent(parseObject)
                    }
                    //群消息
                    "group" ->{
                        val parseObject = JSONObject.parseObject(data, GroupMessageEvent::class.java)
                        EventManager.callEvent(parseObject)
                    }
                }

            }
            //通知上报 进群消息
            "notice" ->{
                when (action.getString("notice_type")){
                    //私聊消息撤回
                    "friend_recall" ->{
                        val parseObject = JSONObject.parseObject(data, FriendRecallEvent::class.java)
                        EventManager.callEvent(parseObject)
                    }
                    //群消息撤回
                    "group_recall" ->{
                        val parseObject = JSONObject.parseObject(data, GroupRecallEvent::class.java)
                        EventManager.callEvent(parseObject)
                    }
                    //群成员增加
                    "group_increase" ->{
                        val parseObject = JSONObject.parseObject(data, GroupIncreaseEvent::class.java)
                        EventManager.callEvent(parseObject)
                    }
                    //群成员减少
                    "group_decrease" ->{
                        val parseObject = JSONObject.parseObject(data, GroupDecreaseEvent::class.java)
                        EventManager.callEvent(parseObject)
                    }
                    //群管理员变动
                    "group_admin" ->{
                        val parseObject = JSONObject.parseObject(data, GroupAdminEvent::class.java)
                        EventManager.callEvent(parseObject)
                    }
                    //群文件上传
                    "group_upload" ->{
                        val parseObject = JSONObject.parseObject(data, GroupUploadEvent::class.java)
                        EventManager.callEvent(parseObject)
                    }
                    //群禁言
                    "group_ban" ->{
                        val parseObject = JSONObject.parseObject(data, GroupBanEvent::class.java)
                        EventManager.callEvent(parseObject)
                    }
                    //好友添加
                    "friend_add" ->{
                        val parseObject = JSONObject.parseObject(data, FriendAddEvent::class.java)
                        EventManager.callEvent(parseObject)
                    }
                    //群成员名片更新
                    "group_card" ->{
                        val parseObject = JSONObject.parseObject(data, GroupCardEvent::class.java)
                        EventManager.callEvent(parseObject)
                    }
                    //接收离线文件
                    "offline_file" ->{
                        val parseObject = JSONObject.parseObject(data, OfflineFileEvent::class.java)
                        EventManager.callEvent(parseObject)
                    }
                }
            }
            //请求上报 有添加好友事件
            "request" ->{
                when (action.getString("request_type")){
                    //添加好友
                    "friend" ->{
                        val requestEvent = JSONObject.parseObject(data, NoticeFriendAddEvent::class.java)
                        EventManager.callEvent(requestEvent)
                    }
                    //加群请求／邀请
                    "group" ->{
                        val string = action.getString("sub_type")
                        if (string == "invite"){
                            val requestEvent = JSONObject.parseObject(data, GroupInviteEvent::class.java)
                            EventManager.callEvent(requestEvent)
                        }else{
                            val requestEvent = JSONObject.parseObject(data, GroupJoinEvent::class.java)
                            EventManager.callEvent(requestEvent)
                        }
                    }
                }
            }
        }
    }
}