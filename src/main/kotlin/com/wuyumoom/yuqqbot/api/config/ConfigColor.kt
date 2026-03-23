package com.wuyumoom.yuqqbot.api.config

import com.wuyumoom.yuqqbot.api.config.serialization.ConfigurationSerializable
import com.wuyumoom.yuqqbot.api.config.serialization.SerializableAs

/**
 * ARGB-style color for configuration (replaces Bukkit [org.bukkit.Color]).
 */
@SerializableAs("Color")
data class ConfigColor(
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int = 255
) : ConfigurationSerializable {
    override fun serialize(): Map<String, Any> = mapOf(
        "red" to red,
        "green" to green,
        "blue" to blue,
        "alpha" to alpha
    )

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, @JvmSuppressWildcards Any?>): ConfigColor {
            val red = (args["red"] as Number).toInt()
            val green = (args["green"] as Number).toInt()
            val blue = (args["blue"] as Number).toInt()
            val alpha = (args["alpha"] as? Number)?.toInt() ?: 255
            return ConfigColor(red, green, blue, alpha)
        }
    }
}
