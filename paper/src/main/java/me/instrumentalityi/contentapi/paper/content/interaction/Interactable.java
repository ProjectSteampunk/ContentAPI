package me.instrumentalityi.contentapi.paper.content.interaction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Interactable<T> {

    default void interact(@NotNull T args) {}

    default @Nullable InteractionHandler getHandler() { return null; }

}
