package me.instrumentalityi.contentapi.paper.content;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.instrumentalityi.contentapi.common.ContentModule;
import me.instrumentalityi.contentapi.common.content.Content;
import me.instrumentalityi.contentapi.common.content.ContentType;
import me.instrumentalityi.contentapi.common.content.resolver.ContentResolver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PaperContentModule implements ContentModule {

    private final Multimap<Class<? extends Content<?>>, Content<?>> content;
    private final Map<String, ContentType<?>> types;
    private final Map<Class<?>, ContentResolver<?>> resolvers;

    public PaperContentModule() {
        this.content = ArrayListMultimap.create();
        this.types = new ConcurrentHashMap<>();
        this.resolvers = new ConcurrentHashMap<>();
    }

    @Override
    public <T extends Content<?>> void registerType(String id, Class<T> clazz, Function<Map<String, Object>, T> function) {
        this.types.put(id, new ContentType<>(clazz, function));
    }

    @Override
    public <T> void registerResolver(Class<T> clazz, ContentResolver<T> resolver) {
        this.resolvers.put(clazz, resolver);
    }

    @Override
    public void registerContent(Class<? extends Content<?>> clazz, Content<?> content) {
        this.content.put(clazz, content);
    }

    @Override
    public ContentType<?> getContentType(String id) {
        return this.types.get(id);
    }

    @Override
    public <T extends Content<?>> ContentType<T> getContentType(String id, Class<ContentType<T>> clazz) {
        ContentType<?> type = this.getContentType(id);

        if(type == null) return null;

        return clazz.cast(type);
    }

    @Override
    public Content<?> getContent(Object object) {
        ContentResolver<?> resolver = this.resolvers.get(object.getClass());

        if(resolver == null) return null;

        return resolver.resolve(object);
    }

    @Override
    public Content<?> getContent(String type, String id) {
        ContentType<?> typeValue = this.getContentType(type);
        if(typeValue == null) return null;

        for(Content<?> content : this.content.get(typeValue.clazz())) {
            if(content.getId().equalsIgnoreCase(id)) return content;
        }

        return null;
    }

    @Override
    public <T extends Content<?>> T getContent(Class<T> clazz, Object object) {
        return clazz.cast(this.getContent(object));
    }

    @Override
    public <T extends Content<?>> T getContent(Class<T> clazz, String type, String id) {
        return clazz.cast(this.getContent(type, id));
    }
}
