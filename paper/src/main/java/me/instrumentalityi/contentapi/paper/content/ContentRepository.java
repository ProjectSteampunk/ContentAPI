package me.instrumentalityi.contentapi.paper.content;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ContentRepository<T extends Content> {

    private final @NotNull String tag;
    private final @NotNull Function<String, T> provider;

    private final @NotNull Map<String, T> contents = new ConcurrentHashMap<>();

    public ContentRepository(@NotNull String tag, @NotNull Function<String, T> provider) {
        this.tag = tag;
        this.provider = provider;
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

    public @NotNull T assembleContent(@NotNull ConfigurationSection config) {
        T content = this.provider.apply(config.getName());
        content.read(config);

        return content;
    }

}
