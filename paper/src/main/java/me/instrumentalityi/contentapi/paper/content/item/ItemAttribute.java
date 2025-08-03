package me.instrumentalityi.contentapi.paper.content.item;

import me.instrumentalityi.steampunklib.paper.utils.ItemEditor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ItemAttribute {

    String getId();

    default void applyToItem(@NotNull ItemEditor editor) {}

    default void applyToLore(@NotNull List<Component> lore) {}
}
