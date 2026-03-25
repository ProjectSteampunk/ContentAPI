package me.instrumentalityi.contentapi.paper.content.menus.values;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface MenuEditable {

    @NotNull EditableValues getValues();

    @NotNull ItemStack getPreview();
}
