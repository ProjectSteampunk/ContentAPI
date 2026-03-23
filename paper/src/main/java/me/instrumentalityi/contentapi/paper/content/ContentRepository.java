package me.instrumentalityi.contentapi.paper.content;

import lombok.Getter;
import me.instrumentalityi.contentapi.paper.ContentAPIPlugin;
import me.instrumentalityi.contentapi.paper.content.interaction.Interactable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class ContentRepository<T extends Content> {

    @Getter
    private final @NotNull String tag;
    private BiFunction<ContentRepository<T>, String, T> provider;

    private final @NotNull Map<String, T> contents = new ConcurrentHashMap<>();

    public ContentRepository(@NotNull String tag) {
        this.tag = tag;
    }

    protected void setProvider(@NotNull BiFunction<ContentRepository<T>, String, T> provider) {
        this.provider = provider;
        ContentAPIPlugin.getInstance().getLogger().info("provider set " + (this.provider != null));
    }

    public @NotNull Content loadContent(@NotNull ConfigurationSection config) {
        T content = this.assembleContent(config);
        this.registerContent(content);

        return content;
    }

    public void registerContent(@NotNull T content) {
        this.contents.put(content.getId(), content);
    }

    public @Nullable T getContent(@NotNull String id) {
        return this.contents.get(id);
    }

    private @NotNull T assembleContent(@NotNull ConfigurationSection config) {
        if(this.provider == null) {
            throw new IllegalStateException("Content repo provider not set");
        }

        T content = this.provider.apply(this, config.getName());
        content.read(config);

        return this.setupExtensions(content);
    }

    private T setupExtensions(T content) {
        this.setupInteractions(content);
        return content;
    }

    private void setupInteractions(Content content) {
        if(!(content instanceof Interactable<?> interactable)) return;

        if(interactable.getHandler() == null) return;

        JavaPlugin plugin = ContentAPIPlugin.getInstance();

        plugin.getServer().getPluginManager().registerEvents(interactable.getHandler(), plugin);
    }

}
