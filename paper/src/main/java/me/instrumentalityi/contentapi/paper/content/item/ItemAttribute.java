package me.instrumentalityi.contentapi.paper.content.item;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ItemAttribute {

    String getId();

    default void applyToItem(@NotNull ItemStack item) {}

    default void applyToLore(@NotNull List<Component> lore) {}
}
