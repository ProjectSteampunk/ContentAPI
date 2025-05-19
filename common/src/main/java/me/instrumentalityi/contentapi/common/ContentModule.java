package me.instrumentalityi.contentapi.common;

import me.instrumentalityi.contentapi.common.content.Content;
import me.instrumentalityi.contentapi.common.content.ContentType;
import me.instrumentalityi.contentapi.common.content.resolver.ContentResolver;
import me.instrumentalityi.steampunklib.common.modules.Module;

import java.util.Map;
import java.util.function.Function;

public interface ContentModule extends Module {

    <T extends Content<?>> void registerType(String id, Class<T> clazz, Function<Map<String, Object>, T> function);

    <T> void registerResolver(Class<T> clazz, ContentResolver<T> resolver);

    void registerContent(Class<? extends Content<?>> clazz, Content<?> content);

    Content<?> getContent(Object object);

    Content<?> getContent(String type, String id);

    <T extends Content<?>> T getContent(Class<T> clazz, Object object);

    <T extends Content<?>> T getContent(Class<T> clazz, String type, String id);

    ContentType<?> getContentType(String id);

    <T extends Content<?>> ContentType<T> getContentType(String id, Class<ContentType<T>> clazz);
}
