package me.instrumentalityi.contentapi.paper.utils;

import me.instrumentalityi.steampunklib.paper.utils.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

public class FileUtil {

    private final static @NotNull MessageDigest DIGEST;

    static {
        try {
            DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public static List<ConfigurationSection> getAllKeys(@NotNull JavaPlugin plugin, @NotNull File folder) {
//        return getConfigurationsRecursively(plugin, folder).stream()
//                .flatMap(configuration -> {
//                    Set<String> keys = configuration.getKeys(false);
//
//                    return keys.stream().map(configuration::getConfigurationSection);
//                }).filter(Objects::nonNull).toList();
//    }

    public static List<Configuration> getConfigurationsRecursively(@NotNull JavaPlugin plugin, @NotNull File folder) {
        return getYamlFilesRecursively(folder).stream().map(file -> new Configuration(plugin, file)).toList();
    }

    public static Map<File, byte[]> getYamlFileHashes(@NotNull File folder) {
        return getYamlFilesRecursively(folder).stream().collect(Collectors.toMap(
                file -> file,
                FileUtil::hashFile
        ));
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

    public static byte[] hashFile(File file) {
        try {
            byte[] data = Files.readAllBytes(file.toPath());

            return DIGEST.digest(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasFilesChanged(Map<File, byte[]> a, Map<File, byte[]> b) {
        if (a.size() != b.size()) return true;

        for (Map.Entry<File, byte[]> entry : a.entrySet()) {
            byte[] other = b.get(entry.getKey());

            if (other == null) return true;
            if (!Arrays.equals(entry.getValue(), other)) return true;
        }

        return false;
    }
}
