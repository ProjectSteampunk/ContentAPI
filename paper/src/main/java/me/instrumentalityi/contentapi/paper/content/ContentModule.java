package me.instrumentalityi.contentapi.paper.content;

import lombok.Getter;
import me.instrumentalityi.contentapi.paper.ContentAPIPlugin;
import me.instrumentalityi.contentapi.paper.content.impl.Consumable;
import me.instrumentalityi.contentapi.paper.content.impl.Item;
import me.instrumentalityi.contentapi.paper.utils.ClassUtil;
import me.instrumentalityi.steampunklib.common.modules.Module;
import me.instrumentalityi.steampunklib.common.modules.exceptions.ModuleStartupException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class ContentModule implements Module {

    private Map<String, ContentRepository<? extends Content>> repositories;
    private Map<String, String> conversions;

    @Getter private final ContentAPIPlugin plugin;
    @Getter private final ContentLoader loader;

    public ContentModule(@NotNull ContentAPIPlugin plugin) {
        this.plugin = plugin;
        this.loader = new ContentLoader(this);
    }

    @Override
    public void start() throws ModuleStartupException {
        this.repositories = new ConcurrentHashMap<>();
        this.conversions = new ConcurrentHashMap<>();

        this.registerProducer(Item.ID, Item.class, Item::new);
        this.registerProducer(Consumable.ID, Consumable.class, Consumable::new);

        this.loader.load();
    }

    @Override
    public void stop() {
        this.repositories.clear();
        this.repositories = null;

        this.conversions.clear();
        this.conversions = null;
    }

    public <T extends Content> void registerProducer(@NotNull String id, Class<T> clazz, @NotNull BiFunction<ContentRepository<T>, String, T> provider) {
        ContentRepository<T> repository = new ContentRepository<>(clazz, id);
        repository.setProvider(provider);

        this.repositories.put(id, repository);
        this.conversions.put(clazz.getSimpleName(), id);
    }

    public @Nullable ContentRepository<?> getRepository(@NotNull String id) {
        return this.repositories.get(id);
    }

    @SuppressWarnings("unchecked")
    public <T extends Content> @Nullable ContentRepository<T> getRepository(@NotNull String id, @NotNull Class<T> clazz) {
        return (ContentRepository<T>) this.getRepository(id);
    }

    public @Nullable ContentRepository<? extends Content> getRepository(Class<? extends Content> clazz) {
        String id = this.conversions.get(clazz.getSimpleName());
        if(id == null) return null;

        return this.getRepository(id);
    }

    public List<ContentRepository<?>> getChildren(Class<? extends Content> type) {
        return repositories.values().stream()
                .filter(repo -> ClassUtil.isDirectChild(type, repo.getClazz()))
                .toList();
    }

    public @NotNull Content loadContent(@NotNull ConfigurationSection config) {
        String type = config.getString("type");

        if(type == null) {
            throw new RuntimeException("Content requires a type");
        }

        ContentRepository<?> repo = this.getRepository(type);
        if(repo == null) {
            throw new RuntimeException("Content requires a repository for " + type);
        }

        return repo.loadContent(config);
    }
}
