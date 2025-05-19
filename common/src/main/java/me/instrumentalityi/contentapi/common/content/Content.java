package me.instrumentalityi.contentapi.common.content;

import me.instrumentalityi.contentapi.common.content.builder.ContentBuilder;
import org.jetbrains.annotations.NotNull;

public interface Content<T> {

    String getId();

    String getType();

    @NotNull ContentBuilder<T> builder();
}
