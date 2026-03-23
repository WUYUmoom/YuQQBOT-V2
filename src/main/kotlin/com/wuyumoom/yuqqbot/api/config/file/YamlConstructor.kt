package com.wuyumoom.yuqqbot.api.config.file

import com.wuyumoom.yuqqbot.api.config.serialization.ConfigurationSerialization
import java.util.LinkedHashMap
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.constructor.SafeConstructor
import org.yaml.snakeyaml.error.YAMLException
import org.yaml.snakeyaml.nodes.Node
import org.yaml.snakeyaml.nodes.Tag
import kotlin.collections.iterator

class YamlConstructor : SafeConstructor(LoaderOptions()) {
    init {
        yamlConstructors[Tag.MAP] = ConstructCustomObject()
    }

    private inner class ConstructCustomObject : ConstructYamlMap() {
        override fun construct(node: Node): Any {
            if (node.isTwoStepsConstruction) {
                throw YAMLException("Unexpected referential mapping structure. Node: $node")
            }
            val raw = super.construct(node) as Map<*, *>
            if (raw.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                val typed = LinkedHashMap<String, Any?>(raw.size)
                for ((k, v) in raw) {
                    typed[k.toString()] = v
                }
                return try {
                    ConfigurationSerialization.deserializeObject(typed)
                        ?: throw YAMLException("Could not deserialize object")
                } catch (ex: IllegalArgumentException) {
                    throw YAMLException("Could not deserialize object", ex)
                }
            }
            return raw
        }

        override fun construct2ndStep(node: Node, `object`: Any) {
            throw YAMLException("Unexpected referential mapping structure. Node: $node")
        }
    }
}
