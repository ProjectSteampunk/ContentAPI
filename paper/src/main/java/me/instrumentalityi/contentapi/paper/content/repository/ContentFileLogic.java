package me.instrumentalityi.contentapi.paper.content.repository;

import me.instrumentalityi.contentapi.paper.ContentAPIPlugin;
import me.instrumentalityi.contentapi.paper.content.Content;
import me.instrumentalityi.contentapi.paper.utils.FileUtil;
import me.instrumentalityi.steampunklib.paper.utils.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ContentFileLogic<T extends Content> {

    private final @NotNull ContentRepository<T> repo;

    private Configuration config;
    private byte[] lastUpdateHash;

    public ContentFileLogic(@NotNull ContentRepository<T> repo) {
        this.repo = repo;
    }

    public void loadConfig(@NotNull Configuration config) {
        this.loadConfig(config, this.hashFile(config));
    }

    public void loadConfig(@NotNull Configuration config, byte[] hash) {
        this.config = config;
        this.config.load();
        this.lastUpdateHash = hash;

        List<ConfigurationSection> keys = this.getFileReturn(config).keys();
        if(keys.isEmpty()) {
            ContentAPIPlugin.getInstance().getLogger().info("No content loaded for " + this.repo.getTag());
        }

        for(ConfigurationSection c : keys) {
            Content content = this.repo.loadContent(c);
            ContentAPIPlugin.getInstance().getLogger().info("Loaded content " + content.getId());
        }
    }

    public void saveContent() {
        //TODO: Wipe any file entries which aren't registered as content
        Collection<T> contents = this.repo.getContents();
        this.wipeLegacy(contents);

        for(Content content : contents) {
            this.saveContent(content);
        }
        this.config.save();
    }

    public void saveContent(@NotNull Content content) {
        ConfigurationSection config = this.config.getConfigurationSection(content.getId());
        if(config == null) {
            config = this.config.createSection(content.getId());
        }

        content.write(config);
    }

    // Removes any keys in the file which are not loaded in the plugin
    private void wipeLegacy(@NotNull Collection<T> contents) {
        Set<String> currentKeys = contents.stream().map(Content::getId).collect(Collectors.toSet());
        Set<String> fileKeys = this.config.getKeys(false);

        Set<String> legacyKeys = new HashSet<>(fileKeys);
        legacyKeys.removeAll(currentKeys);

        for(String key : legacyKeys) {
            this.config.set(key, null);
        }
    }

    public boolean reloadContent() {
        byte[] hash = this.hashFile(config);
        if(Arrays.equals(this.lastUpdateHash, hash)) return false;

        this.loadConfig(this.config, hash);
        return true;
    }

    private byte[] hashFile(@NotNull Configuration config) {
        return FileUtil.hashFile(config.getFile());
    }

    private FileReturn getFileReturn(@NotNull Configuration config) {
        List<ConfigurationSection> keys = config.getKeys(false).stream()
                .map(config::getConfigurationSection)
                .filter(Objects::nonNull).toList();

        return new FileReturn(keys);
    }

    public record FileReturn(@NotNull List<ConfigurationSection> keys) {
    }
}
