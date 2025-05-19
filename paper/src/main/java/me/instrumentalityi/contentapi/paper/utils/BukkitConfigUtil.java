package me.instrumentalityi.contentapi.paper.utils;

import me.instrumentalityi.contentapi.paper.ContentAPIPlugin;
import me.instrumentalityi.steampunklib.paper.utils.Configuration;
import me.instrumentalityi.steampunklib.paper.utils.PaperStringUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class BukkitConfigUtil {



    public static Component getComponent(Map<String, Object> config, String path) {
        return translateComponent((String) config.get(path));
    }

    public static Material getMaterial(Map<String, Object> config, String path) {
        String material = (String) config.get(path);

        if(material == null) material = "BARRIER";

        return Material.getMaterial(material.toUpperCase());
    }

    public static List<Component> getComponentList(Map<String, Object> config, String path) {
        MemoryConfiguration memory = new MemoryConfiguration();
        memory.addDefaults(config);

        List<String> list = memory.getStringList(path);
        List<Component> components = new ArrayList<>();

        for(String text : list) {
            components.add(translateComponent(text));
        }

        return components;
    }

    private static Component translateComponent(String text) {
        if(text == null) return null;

        return PaperStringUtil.colorize(text);
    }

    private static final FileFilter YML_FILTER = (file) -> file.getName().endsWith(".yml");

    public static <T> List<Configuration> loadObjectsFromFolder(Class<T> clazz, File dir, String parameter, Consumer<T> mapping, Function<ConfigurationSection, T> creator) {
        if (!dir.exists()) {
            dir.mkdir();
        }

        List<Configuration> configs = new ArrayList<>();

        File[] files = dir.listFiles(YML_FILTER);

        if (files == null) return null;

        for (File file : files) {
            Configuration config = new Configuration(ContentAPIPlugin.getInstance(), file);

            BukkitConfigUtil.loadObjects(clazz, parameter, config, mapping, creator);

            configs.add(config);
        }

        return configs;
    }

    /**
     * Quickly load and map objects from a configuration file
     *
     * @param clazz     Object type
     * @param parameter Configuration Section key
     * @param config    Configuration file
     * @param mapping   Collection Mapper
     * @param creator   Object Creator
     */

    public static <T> void loadObjects(Class<T> clazz, String parameter, Configuration config, Consumer<T> mapping, Function<ConfigurationSection, T> creator) {
        ConfigurationSection objects = config.getConfigurationSection(parameter);

        if (objects == null) {
            objects = config.createSection(parameter);
        }

        for (String key : objects.getKeys(false)) {
            ConfigurationSection section = objects.getConfigurationSection(key);
            if (section == null) continue;

            T object = creator.apply(section);

            mapping.accept(object);
        }
    }
}
