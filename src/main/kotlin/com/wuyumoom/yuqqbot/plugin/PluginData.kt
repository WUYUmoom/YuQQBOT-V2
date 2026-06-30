package com.wuyumoom.yuqqbot.plugin

import com.wuyumoom.yuqqbot.api.config.ConfigurationSection
import kotlin.collections.MutableMap

class PluginData(
    val Plugin: String,
    val CDK: MutableMap<String, MutableList<String>>
) {
    companion object {
        //创建实例
        fun create(config: ConfigurationSection): PluginData {
            val plugin = config.getName()
            val CDK : MutableMap<String, MutableList<String>> = mutableMapOf()
            config.getKeys(false).forEach { QQ ->
                val list = config.getStringList(QQ)
                CDK[QQ.replace("QQ", "")] = list.toMutableList()
            }
            return PluginData(plugin, CDK)
        }
    }



    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("插件: $Plugin\n")
        CDK.forEach { (string, strings) ->
            sb.append("QQ: $string\n ${strings.joinToString("\n")}\n")
        }
        return sb.toString()
    }

}