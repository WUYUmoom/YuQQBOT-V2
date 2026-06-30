package com.wuyumoom.yuqqbot.api.event

/**
 * 事件基类，所有事件都需要继承此类
 */
abstract class Event {
    private var isCancelled = false

    /**
     * 获取事件是否可被取消
     */
    open fun isCancellable(): Boolean = false

    /**
     * 获取事件是否已被取消
     */
    fun isCancelled(): Boolean = isCancelled

    /**
     * 取消事件（仅对可取消事件有效）
     */
    fun setCancelled(cancel: Boolean) {
        if (isCancellable()) {
            isCancelled = cancel
        }
    }
}