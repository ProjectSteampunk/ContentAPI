package me.instrumentalityi.contentapi.paper.content.menus.values;

import me.instrumentalityi.contentapi.paper.content.Content;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface MenuEditable extends Content {

    @NotNull EditableValues getValues();

    @NotNull ItemStack getPreview();
}
