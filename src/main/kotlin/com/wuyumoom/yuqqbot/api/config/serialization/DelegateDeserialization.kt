package com.wuyumoom.yuqqbot.api.config.serialization

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class DelegateDeserialization(val value: KClass<out ConfigurationSerializable>)
