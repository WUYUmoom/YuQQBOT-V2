package com.wuyumoom.yuqqbot.api.config

import com.wuyumoom.yuqqbot.api.config.serialization.ConfigurationSerializable
import com.wuyumoom.yuqqbot.api.config.serialization.SerializableAs

/**
 * Generic 3D vector for configuration (replaces Bukkit [org.bukkit.util.Vector]).
 */
@SerializableAs("Vector")
data class Vector3(
    val x: Double,
    val y: Double,
    val z: Double
) : ConfigurationSerializable {
    override fun serialize(): Map<String, Any> = mapOf("x" to x, "y" to y, "z" to z)

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, @JvmSuppressWildcards Any?>): Vector3 {
            val x = (args["x"] as Number).toDouble()
            val y = (args["y"] as Number).toDouble()
            val z = (args["z"] as Number).toDouble()
            return Vector3(x, y, z)
        }
    }
}
