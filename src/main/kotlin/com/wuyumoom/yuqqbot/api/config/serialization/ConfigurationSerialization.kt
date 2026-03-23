package com.wuyumoom.yuqqbot.api.config.serialization

import com.wuyumoom.yuqqbot.api.config.ConfigColor
import com.wuyumoom.yuqqbot.api.config.Vector3
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Registers [ConfigurationSerializable] types and deserializes tagged maps (`==` key).
 */
class ConfigurationSerialization private constructor(
    private val clazz: Class<out ConfigurationSerializable>
) {
    private fun getMethod(name: String, isStatic: Boolean): Method? {
        return try {
            val method = clazz.getDeclaredMethod(name, Map::class.java)
            when {
                !ConfigurationSerializable::class.java.isAssignableFrom(method.returnType) -> null
                Modifier.isStatic(method.modifiers) != isStatic -> null
                else -> method
            }
        } catch (_: NoSuchMethodException) {
            null
        } catch (_: SecurityException) {
            null
        }
    }

    private fun getConstructor(): Constructor<out ConfigurationSerializable>? {
        return try {
            clazz.getConstructor(Map::class.java)
        } catch (_: NoSuchMethodException) {
            null
        } catch (_: SecurityException) {
            null
        }
    }

    private fun deserializeViaMethod(method: Method, args: Map<String, *>): ConfigurationSerializable? {
        return try {
            val result = method.invoke(null, args) as ConfigurationSerializable?
            if (result == null) {
                log.severe("Could not call method '${method}' of $clazz for deserialization: method returned null")
            }
            result
        } catch (ex: Throwable) {
            val cause = if (ex is InvocationTargetException) ex.cause else ex
            log.log(Level.SEVERE, "Could not call method '${method}' of $clazz for deserialization", cause)
            null
        }
    }

    private fun deserializeViaCtor(
        ctor: Constructor<out ConfigurationSerializable>,
        args: Map<String, *>
    ): ConfigurationSerializable? {
        return try {
            ctor.newInstance(args)
        } catch (ex: Throwable) {
            val cause = if (ex is InvocationTargetException) ex.cause else ex
            log.log(Level.SEVERE, "Could not call constructor '${ctor}' of $clazz for deserialization", cause)
            null
        }
    }

    fun deserialize(args: Map<String, *>): ConfigurationSerializable? {
        requireNotNull(args) { "Args must not be null" }
        var result: ConfigurationSerializable? = null
        var method = getMethod("deserialize", true)
        if (method != null) {
            result = deserializeViaMethod(method, args)
        }
        if (result == null) {
            method = getMethod("valueOf", true)
            if (method != null) {
                result = deserializeViaMethod(method, args)
            }
        }
        if (result == null) {
            val ctor = getConstructor()
            if (ctor != null) {
                result = deserializeViaCtor(ctor, args)
            }
        }
        return result
    }

    companion object {
        const val SERIALIZED_TYPE_KEY: String = "=="

        private val log = Logger.getLogger(ConfigurationSerialization::class.java.name)

        private val aliases = ConcurrentHashMap<String, Class<out ConfigurationSerializable>>()

        init {
            registerClass(Vector3::class.java)
            registerClass(ConfigColor::class.java)
        }

        fun deserializeObject(args: Map<String, *>, clazz: Class<out ConfigurationSerializable>): ConfigurationSerializable? {
            return ConfigurationSerialization(clazz).deserialize(args)
        }

        fun deserializeObject(args: Map<String, *>): ConfigurationSerializable? {
            val clazz: Class<out ConfigurationSerializable>
            if (args.containsKey(SERIALIZED_TYPE_KEY)) {
                val alias = args[SERIALIZED_TYPE_KEY] as? String
                    ?: throw IllegalArgumentException("Cannot have null alias")
                clazz = getClassByAlias(alias)
                    ?: throw IllegalArgumentException("Specified class does not exist ('$alias')")
            } else {
                throw IllegalArgumentException("Args doesn't contain type key ('$SERIALIZED_TYPE_KEY')")
            }
            return ConfigurationSerialization(clazz).deserialize(args)
        }

        @JvmStatic
        fun registerClass(clazz: Class<out ConfigurationSerializable>) {
            val delegate = clazz.getAnnotation(DelegateDeserialization::class.java)
            if (delegate == null) {
                registerClass(clazz, getAlias(clazz))
                registerClass(clazz, clazz.name)
            }
        }

        @JvmStatic
        fun registerClass(clazz: Class<out ConfigurationSerializable>, alias: String) {
            aliases[alias] = clazz
        }

        @JvmStatic
        fun unregisterClass(alias: String) {
            aliases.remove(alias)
        }

        @JvmStatic
        fun unregisterClass(clazz: Class<out ConfigurationSerializable>) {
            val it = aliases.iterator()
            while (it.hasNext()) {
                if (it.next().value == clazz) it.remove()
            }
        }

        @JvmStatic
        fun getClassByAlias(alias: String): Class<out ConfigurationSerializable>? = aliases[alias]

        @JvmStatic
        fun getAlias(clazz: Class<out ConfigurationSerializable>): String {
            val delegateAnn = clazz.getAnnotation(DelegateDeserialization::class.java)
            if (delegateAnn != null) {
                val delegateClass = delegateAnn.value.java
                if (delegateClass != clazz) {
                    return getAlias(delegateClass)
                }
            }
            val alias = clazz.getAnnotation(SerializableAs::class.java)
            if (alias != null && alias.value.isNotEmpty()) {
                return alias.value
            }
            return clazz.name
        }
    }
}
