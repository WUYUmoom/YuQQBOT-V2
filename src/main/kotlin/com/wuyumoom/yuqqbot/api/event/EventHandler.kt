package com.wuyumoom.yuqqbot.api.event

/**
 * 事件处理方法的注解
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventHandler(
    val priority: EventPriority = EventPriority.NORMAL,
    val ignoreCancelled: Boolean = false
)