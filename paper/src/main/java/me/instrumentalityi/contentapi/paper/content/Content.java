package me.instrumentalityi.contentapi.paper.content;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public interface Content {
    @NotNull String getId();

    @NotNull ContentRepository<? extends Content> getRepo();

    void write(@NotNull ConfigurationSection config);

    void read(@NotNull ConfigurationSection config);
}
