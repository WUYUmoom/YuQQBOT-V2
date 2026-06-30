package com.wuyumoom.yuqqbot.event

import com.wuyumoom.yuqqbot.YuQQBot
import com.wuyumoom.yuqqbot.api.bot.BotData
import com.wuyumoom.yuqqbot.api.bot.botaction.BotAction
import com.wuyumoom.yuqqbot.api.bot.event.FriendAddEvent
import com.wuyumoom.yuqqbot.api.bot.event.GroupIncreaseEvent
import com.wuyumoom.yuqqbot.api.bot.event.GroupMessageEvent
import com.wuyumoom.yuqqbot.api.bot.event.PrivateMessageEvent
import com.wuyumoom.yuqqbot.api.event.EventHandler
import com.wuyumoom.yuqqbot.api.event.EventPriority
import com.wuyumoom.yuqqbot.api.event.Listener
import com.wuyumoom.yuqqbot.config.ConfigManager

class BOTEvent: Listener {
    companion object{
        var groupJoinMessage: String = ConfigManager.config.getString("VerifySocket.进群消息")?:"[CQ:at,qq=%QQ%] 欢迎加入本群"
        var groupIncreaseCDKAdd: Boolean = ConfigManager.config.getBoolean("VerifySocket.加群添加CDK")
        var unbindMessage: String = ConfigManager.config.getString("VerifySocket.解绑")?:"[CQ:at,qq=%QQ%] 您名下的全部CDK已解绑"
        var getCdkMessage: String = ConfigManager.config.getString("VerifySocket.查询授权")?:"[CQ:at,qq=%QQ%] 请查看私聊！如果没有发送请加我为好友"
        fun reload(){
            groupJoinMessage = ConfigManager.config.getString("VerifySocket.进群消息")?:"[CQ:at,qq=%QQ%] 欢迎加入本群"
            groupIncreaseCDKAdd = ConfigManager.config.getBoolean("VerifySocket.加群添加CDK")
            unbindMessage = ConfigManager.config.getString("VerifySocket.解绑")?:"[CQ:at,qq=%QQ%] 您名下的全部CDK已解绑"
            getCdkMessage = ConfigManager.config.getString("VerifySocket.查询授权")?:"[CQ:at,qq=%QQ%] 请查看私聊！如果没有发送请加我为好友"
        }

    }
    @EventHandler(priority = EventPriority.HIGH)
    fun onGroupMessage(event: GroupMessageEvent) {
        if (!ConfigManager.group.containsKey(event.group_id)){
            return
        }
        if (event.raw_message.contains("解绑")){
            ConfigManager.onUnbind(event.user_id.toString())
            val replace = unbindMessage.replace("%QQ%", event.user_id.toString())
            event.sendMessage(replace)
            return
        }
        if (event.raw_message.contains("查询")||
            event.raw_message.contains("授权")){
            val onGetCDK = ConfigManager.onGetCDK(event.user_id.toString())
            onGetCDK.forEach {
                event.privateChat(it)
            }
            val replace = getCdkMessage.replace("%QQ%", event.user_id.toString())
            event.sendMessage(replace)
            return
        }
    }
    @EventHandler
    fun onGroupIncreaseEvent(event: GroupIncreaseEvent){
        if (!ConfigManager.group.containsKey(event.group_id)){
            return
        }
        if (groupIncreaseCDKAdd){
            val string = ConfigManager.group[event.group_id]
            if (string != null){
                val onAddCDK = ConfigManager.onAddCDK( event.user_id.toString(),string)
                BotAction.sendMsg("private", 1841375451, onAddCDK,true)
            }
        }
        val replace = groupJoinMessage.replace("%QQ%", event.user_id.toString())
        BotAction.sendMsg("group", event.user_id, replace,false, event.group_id)
    }


    @EventHandler(priority = EventPriority.HIGH)
    fun onPrivateMessage(event: PrivateMessageEvent){
        if (BotData.admin.contains(event.user_id)){
            val split = event.raw_message.split(",")
            if (split.size == 3){
                if (split[0] == "add"){
                    val string = ConfigManager.onAddCDK(split[1], split[2])
                    BotAction.sendMsg("private", 1841375451, string,true)
                }
            }
        }
        if (event.raw_message.contains("解绑")){
            ConfigManager.onUnbind(event.user_id.toString())
            val replace = unbindMessage.replace("%QQ%", event.user_id.toString())
            event.sendMessage(replace)
            return
        }
        if (event.raw_message.contains("查询")||
            event.raw_message.contains("授权")){
            val onGetCDK = ConfigManager.onGetCDK(event.user_id.toString())
            onGetCDK.forEach {
                event.sendMessage(it)
            }
            return
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    fun onFriendAdd(event: FriendAddEvent) {
        event.setFriendRequest(true)
    }
}