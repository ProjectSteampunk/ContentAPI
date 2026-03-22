package me.instrumentalityi.contentapi.paper.content;

import me.instrumentalityi.contentapi.paper.ContentAPIPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

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

    public @NotNull Content load(@NotNull ConfigurationSection config) {
        T content = this.assemble(config);
        this.register(content);

        return content;
    }

    public void register(@NotNull T content) {
        this.contents.put(content.getId(), content);
    }

    public @NotNull T assemble(@NotNull ConfigurationSection config) {
        T content = this.provider.apply(config.getName());
        content.read(config);

        return content;
    }

}
