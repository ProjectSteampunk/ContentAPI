package me.instrumentalityi.contentapi.paper.content;

import me.instrumentalityi.contentapi.paper.utils.FileUtil;
import me.instrumentalityi.steampunklib.paper.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ContentLoader {

    private static final long AUTO_SAVE = 5*60*20;

    private final @NotNull ContentModule module;

    private Map<File, byte[]> lastUpdate;
    private List<Configuration> files;

    public ContentLoader(@NotNull ContentModule module) {
        this.module = module;

        JavaPlugin plugin = module.getPlugin();
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::save, AUTO_SAVE, AUTO_SAVE);
    }

    public void load() {
        this.load(this.retrieveContentFolder(module.getPlugin()));
    }

    public void load(@NotNull File folder) {
        JavaPlugin plugin = module.getPlugin();

        FileReturn fileReturn = this.getFileReturn(plugin, folder);

        this.files = fileReturn.files();
        for(ConfigurationSection section : fileReturn.keys()) {
            Content content = module.loadContent(section);

            plugin.getLogger().info("Registered content " + content.getId());
        }

        this.lastUpdate = FileUtil.getYamlFileHashes(folder);
    }

    public void reload() {
        if(!this.hasContentChanged()) {
            module.getPlugin().getLogger().info("No change in contents found");
            return;
        }

        module.getPlugin().getLogger().info("Reloading contents");
        this.load();
    }

    public void save() {
        for(Configuration file : this.files) {
            for(String key : file.getKeys(false)) {
                ConfigurationSection config = file.getConfigurationSection(key);
                if(config == null) continue;

                String type = config.getString("type");
                if(type == null) continue;

                ContentRepository<?> repo = this.module.getRepository(type);
                if(repo == null) continue;

                Content content = repo.getContent(config.getName());
                if(content == null) continue;

                content.write(config);
            }

            file.save();
        }

        File folder = this.retrieveContentFolder(module.getPlugin());
        this.lastUpdate = FileUtil.getYamlFileHashes(folder);

        this.module.getPlugin().getLogger().info("Saved contents");
    }

    public boolean hasContentChanged() {
        File folder = this.retrieveContentFolder(module.getPlugin());
        return FileUtil.hasFilesChanged(this.lastUpdate, FileUtil.getYamlFileHashes(folder));
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

    private FileReturn getFileReturn(@NotNull JavaPlugin plugin, @NotNull File folder) {
        List<Configuration> files = FileUtil.getConfigurationsRecursively(plugin, folder);
        List<ConfigurationSection> keys = files.stream()
                .flatMap(configuration -> {
                    Set<String> vals = configuration.getKeys(false);

                    return vals.stream().map(configuration::getConfigurationSection);
                }).filter(Objects::nonNull).toList();

        return new FileReturn(files, keys);
    }

    public record FileReturn(@NotNull List<Configuration> files, @NotNull List<ConfigurationSection> keys) {
    }
}
