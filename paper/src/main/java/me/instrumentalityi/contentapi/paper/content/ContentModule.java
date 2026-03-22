package me.instrumentalityi.contentapi.paper.content;

import lombok.Getter;
import me.instrumentalityi.contentapi.paper.ContentAPIPlugin;
import me.instrumentalityi.contentapi.paper.content.impl.Item;
import me.instrumentalityi.steampunklib.common.modules.Module;
import me.instrumentalityi.steampunklib.common.modules.exceptions.ModuleStartupException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ContentModule implements Module {

    private Map<String, String> conversions;
    private Map<String, ContentRepository<?>> repositories;

    @Getter
    private final ContentAPIPlugin plugin;
    private final ContentLoader loader;

    public ContentModule(@NotNull ContentAPIPlugin plugin) {
        this.plugin = plugin;
        this.loader = new ContentLoader(this);
    }

    @Override
    public void start() throws ModuleStartupException {
        this.conversions = new ConcurrentHashMap<>();
        this.repositories = new ConcurrentHashMap<>();

        this.registerProducer(Item.ID, Item.class, Item::new);

        this.loader.load();
    }

    @Override
    public void stop() {
        this.conversions.clear();
        this.conversions = null;

        this.repositories.clear();
        this.repositories = null;
    }

    public <T extends Content> void registerProducer(@NotNull String id, Class<T> clazz, @NotNull Function<String, T> provider) {
        this.conversions.put(id, clazz.getSimpleName());
        this.repositories.put(id, new ContentRepository<>(id, provider));
    }

    public @Nullable ContentRepository<?> getRespository(@NotNull String id) {
        return this.repositories.get(id);
    }

    @SuppressWarnings("unchecked")
    public <T extends Content> @Nullable ContentRepository<T> getRepository(@NotNull String id, @NotNull Class<T> clazz) {
        return (ContentRepository<T>) this.getRespository(id);
    }

    public @NotNull Content loadContent(@NotNull ConfigurationSection config) {
        String type = config.getString("type");

        if(type == null) {
            throw new RuntimeException("Content requires a type");
        }

        ContentRepository<?> repo = this.getRespository(type);
        if(repo == null) {
            throw new RuntimeException("Content requires a repository for " + type);
        }

        return repo.loadContent(config);
    }
}
