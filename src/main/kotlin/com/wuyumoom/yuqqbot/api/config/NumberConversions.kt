package com.wuyumoom.yuqqbot.api.config

internal object NumberConversions {
    fun toInt(obj: Any?): Int {
        if (obj is Number) return obj.toInt()
        return try {
            obj.toString().toInt()
        } catch (_: Exception) {
            0
        }
    }

    fun toDouble(obj: Any?): Double {
        if (obj is Number) return obj.toDouble()
        return try {
            obj.toString().toDouble()
        } catch (_: Exception) {
            0.0
        }
    }

    fun toLong(obj: Any?): Long {
        if (obj is Number) return obj.toLong()
        return try {
            obj.toString().toLong()
        } catch (_: Exception) {
            0L
        }
    }
}
