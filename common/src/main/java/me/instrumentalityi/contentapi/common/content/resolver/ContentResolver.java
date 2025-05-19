package me.instrumentalityi.contentapi.common.content.resolver;

import me.instrumentalityi.contentapi.common.content.Content;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ContentResolver<T> {

    @Nullable Content<T> resolve(@NotNull Object object);
}
