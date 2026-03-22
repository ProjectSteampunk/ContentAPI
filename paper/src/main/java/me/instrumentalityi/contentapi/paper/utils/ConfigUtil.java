package me.instrumentalityi.contentapi.paper.utils;

import me.instrumentalityi.steampunklib.paper.utils.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConfigUtil {

    public static List<ConfigurationSection> getAllKeys(@NotNull JavaPlugin plugin, @NotNull File folder) {
        return getConfigurationsRecursively(plugin, folder).stream()
                .flatMap(configuration -> {
                    Set<String> keys = configuration.getKeys(false);

                    return keys.stream().map(configuration::getConfigurationSection);
                }).filter(Objects::nonNull).toList();
    }

    public static List<Configuration> getConfigurationsRecursively(@NotNull JavaPlugin plugin, @NotNull File folder) {
        return getYamlFilesRecursively(folder).stream().map(file -> new Configuration(plugin, file)).toList();
    }

    public static List<File> getYamlFilesRecursively(@NotNull File folder) {
        return getFilesRecursively(folder, ".yml");
    }

    public static List<File> getFilesRecursively(@NotNull File folder, @NotNull String extension) {
        List<File> files = new ArrayList<>();

        File[] list = folder.listFiles((dir, name) -> name.endsWith(extension));
        if (list == null) return files;

        for (File file : list) {
            if (file.isDirectory()) {
                files.addAll(getFilesRecursively(file, extension));
            } else {
                files.add(file);
            }
        }

        return files;
    }
}
