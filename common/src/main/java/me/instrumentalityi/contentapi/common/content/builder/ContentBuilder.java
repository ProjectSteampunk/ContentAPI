package me.instrumentalityi.contentapi.common.content.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface ContentBuilder<T> {

    @NotNull CompletableFuture<@Nullable T> build();

    @NotNull CompletableFuture<@Nullable T> build(@NotNull T previous);
}
