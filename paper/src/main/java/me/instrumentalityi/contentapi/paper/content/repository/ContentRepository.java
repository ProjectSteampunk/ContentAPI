package me.instrumentalityi.contentapi.paper.content.repository;

import lombok.Getter;
import me.instrumentalityi.contentapi.paper.ContentAPIPlugin;
import me.instrumentalityi.contentapi.paper.content.Content;
import me.instrumentalityi.contentapi.paper.content.interaction.Interactable;
import me.instrumentalityi.contentapi.paper.content.menus.views.MenuView;
import me.instrumentalityi.contentapi.paper.content.menus.views.MenuViewable;
import me.instrumentalityi.contentapi.paper.content.menus.views.RepoView;
import me.instrumentalityi.steampunklib.paper.utils.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class ContentRepository<T extends Content> implements MenuViewable {

    @Getter private final @NotNull Class<T> clazz;
    @Getter private final @NotNull String tag;
    @Getter private final @NotNull ContentFileLogic<T> logic;

    private BiFunction<ContentRepository<T>, String, T> provider;


    private final @NotNull Map<String, T> contents = new ConcurrentHashMap<>();

    public ContentRepository(@NotNull Class<T> clazz, @NotNull String tag) {
        this.clazz = clazz;
        this.tag = tag;
        this.logic = new ContentFileLogic<>(this);
    }

    public void setProvider(@NotNull BiFunction<ContentRepository<T>, String, T> provider) {
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

    public @NotNull Collection<T> getContents() { return this.contents.values(); }

    public @NotNull T createContent(@NotNull String id) {
        if(this.provider == null) {
            throw new IllegalStateException("Content repo provider not set");
        }

        return this.provider.apply(this, id);
    }

    public @NotNull T createAndRegisterContent(@NotNull String id) {
        T content = this.createContent(id);
        this.registerContent(content);
        return content;
    }

    private @NotNull T assembleContent(@NotNull ConfigurationSection config) {
        T content = this.createContent(config.getName());
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

    @Override
    public @NotNull MenuView getView() {
        return new RepoView<>(this);
    }
}
