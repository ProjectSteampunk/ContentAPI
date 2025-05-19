package me.instrumentalityi.contentapi.common.content.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ContentBuilder<T> {

    @Nullable T build();

    @Nullable T build(@NotNull T previous);
}
