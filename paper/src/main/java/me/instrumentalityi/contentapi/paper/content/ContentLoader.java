package me.instrumentalityi.contentapi.paper.content;

import me.instrumentalityi.steampunklib.paper.utils.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URISyntaxException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ContentLoader {

    private static final long AUTO_SAVE = 5 * 60 * 20;

    private final @NotNull ContentModule module;

    private final @NotNull File folder;

    public ContentLoader(@NotNull ContentModule module) {
        this.module = module;
        this.folder = this.retrieveContentFolder(module.getPlugin());
    }

    public @NotNull Configuration retrieveConfiguration(@NotNull String name) {
        File file = this.ensureFileExists(new File(this.folder, String.format("%s.yml", name)));
        return new Configuration(module.getPlugin(), file);
    }

    public @NotNull Configuration retrieveConfiguration(@NotNull File file) {
        return new Configuration(module.getPlugin(), file);
    }

    private @NotNull File ensureFileExists(@NotNull File file) {
        try {
            if (file.exists()) return file;

            file.createNewFile();
            this.module.getPlugin().getLogger().info("Created new content file: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private @NotNull File retrieveContentFolder(@NotNull JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "content");
        return this.ensureFolderExists(plugin, file);
    }

    private @NotNull File ensureFolderExists(@NotNull JavaPlugin plugin, @NotNull File file) {
        if (!file.exists()) {
            if (!file.mkdirs()) {
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
