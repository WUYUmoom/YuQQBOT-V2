package com.wuyumoom.yuqqbot.api.config.serialization

/**
 * Object that can be serialized to a map and restored via [ConfigurationSerialization].
 */
interface ConfigurationSerializable {
    fun serialize(): Map<String, Any>
}
