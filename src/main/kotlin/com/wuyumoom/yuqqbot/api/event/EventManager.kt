package com.wuyumoom.yuqqbot.api.event

import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 事件管理器，负责注册监听器和调用事件
 */
object EventManager {
    private val handlerMap = ConcurrentHashMap<Class<out Event>, MutableList<EventHandlerMethod>>()

    /**
     * 注册事件监听器
     */
    fun registerEvents(listener: Listener) {
        val methods = listener.javaClass.declaredMethods
        for (method in methods) {
            val annotation = method.getAnnotation(EventHandler::class.java)
            if (annotation != null && method.parameterCount == 1) {
                val eventType = method.parameterTypes[0]
                if (Event::class.java.isAssignableFrom(eventType)) {
                    method.isAccessible = true
                    val handlerMethod = EventHandlerMethod(
                        listener,
                        method,
                        annotation.priority,
                        annotation.ignoreCancelled
                    )

                    val handlers = handlerMap.computeIfAbsent(eventType.asSubclass(Event::class.java)) {
                        Collections.synchronizedList(mutableListOf())
                    }

                    handlers.add(handlerMethod)
                    // 按优先级排序
                    handlers.sortBy { it.priority.ordinal }
                }
            }
        }
    }

    /**
     * 调用事件
     */
    fun <T : Event> callEvent(event: T): T {
        val handlers = handlerMap[event.javaClass]
        if (handlers != null) {
            for (handler in handlers) {
                if (event.isCancelled() && handler.ignoreCancelled) {
                    continue
                }
                try {
                    handler.method.invoke(handler.listener, event)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return event
    }

    /**
     * 事件处理器方法包装类
     */
    private class EventHandlerMethod(
        val listener: Listener,
        val method: Method,
        val priority: EventPriority,
        val ignoreCancelled: Boolean
    )
}