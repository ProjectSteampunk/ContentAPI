package me.instrumentalityi.contentapi.paper.content.menus.values;

import me.instrumentalityi.contentapi.paper.content.menus.ContentValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MenuEditable {

    @NotNull List<ContentValue<?>> getValues();
}
