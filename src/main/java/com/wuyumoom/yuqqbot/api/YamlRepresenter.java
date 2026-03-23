package com.wuyumoom.yuqqbot.api;

import com.wuyumoom.yuqqbot.api.config.ConfigurationSection;
import com.wuyumoom.yuqqbot.api.config.serialization.ConfigurationSerializable;
import com.wuyumoom.yuqqbot.api.config.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Kotlin cannot subclass SnakeYAML's protected inner class {@code SafeRepresenter.RepresentMap}
 * from another package; use this Java subclass (same pattern as Bukkit's YamlRepresenter).
 * Uses {@code Representer(DumperOptions)} for SnakeYAML 2.x compatibility (no no-arg ctor).
 */
public class YamlRepresenter extends Representer {

    public YamlRepresenter() {
        super(new DumperOptions());
        this.multiRepresenters.put(ConfigurationSection.class, new RepresentConfigurationSection());
        this.multiRepresenters.put(ConfigurationSerializable.class, new RepresentConfigurationSerializable());
    }

    private class RepresentConfigurationSection extends RepresentMap {
        @Override
        public Node representData(Object data) {
            return super.representData(((ConfigurationSection) data).getValues(false));
        }
    }

    private class RepresentConfigurationSerializable extends RepresentMap {
        @Override
        public Node representData(Object data) {
            ConfigurationSerializable serializable = (ConfigurationSerializable) data;
            Map<String, Object> values = new LinkedHashMap<String, Object>();
            values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
            values.putAll(serializable.serialize());

            return super.representData(values);
        }
    }
}
