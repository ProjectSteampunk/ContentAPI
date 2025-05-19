package me.instrumentalityi.contentapi.common.content;

import java.util.Map;
import java.util.function.Function;

public record ContentType<T extends Content<?>>(Class<T> clazz, Function<Map<String, Object>, T> mapper) {
}
