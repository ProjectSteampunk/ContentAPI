package me.instrumentalityi.contentapi.paper.content;

import me.instrumentalityi.contentapi.paper.ContentAPIPlugin;
import me.instrumentalityi.contentapi.paper.utils.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ContentLoader {

    private final @NotNull ContentModule module;

    public ContentLoader(@NotNull ContentModule module) {
        this.module = module;
    }

    public void load() {
        JavaPlugin plugin = module.getPlugin();
        File folder = this.retrieveContentFolder(plugin);

        List<ConfigurationSection> sections = ConfigUtil.getAllKeys(plugin, folder);

        for(ConfigurationSection section : sections) {
            Content content = module.loadContent(section);

            plugin.getLogger().info("Registered content " + content.getId());
        }
    }

    private @NotNull File retrieveContentFolder(@NotNull JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "content");
        return this.ensureFolderExists(plugin, file);
    }

    private @NotNull File ensureFolderExists(@NotNull JavaPlugin plugin, @NotNull File file) {
        if(!file.exists()) {
            if(!file.mkdirs()) {
                throw new RuntimeException("Failed to create content folder");
            }
            this.copyContentFolder(plugin);
        }

        return file;
    }

    private void copyContentFolder(JavaPlugin plugin) {
        try {
            File jarFile = new File(plugin.getClass()
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());

            try (JarFile jar = new JarFile(jarFile)) {
                jar.stream()
                        .filter(entry -> entry.getName().startsWith("content/"))
                        .filter(entry -> !entry.isDirectory())
                        .forEach(entry -> copyEntry(plugin, entry));
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void copyEntry(JavaPlugin plugin, JarEntry entry) {
        File outFile = new File(plugin.getDataFolder(), entry.getName());

        // Don't overwrite existing files
        if (outFile.exists()) return;

        outFile.getParentFile().mkdirs();

        try (InputStream in = plugin.getResource(entry.getName());
             OutputStream out = new FileOutputStream(outFile)) {

            if (in == null) return;

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
