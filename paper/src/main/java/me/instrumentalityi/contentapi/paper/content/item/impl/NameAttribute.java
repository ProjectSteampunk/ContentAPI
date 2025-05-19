package me.instrumentalityi.contentapi.paper.content.item.impl;

import me.instrumentalityi.contentapi.paper.content.item.ItemAttribute;
import me.instrumentalityi.contentapi.paper.utils.ItemEditor;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NameAttribute implements ItemAttribute {

    private static final String ID = "name";

    private final Component name;

    public NameAttribute(Component name) {
        this.name = name;
    }

    @Override
    public void applyToItem(@NotNull ItemStack item) {
        item = new ItemEditor(item)
                .meta(itemMeta -> {
                    itemMeta.displayName(this.name);
                }).build();
    }

    @Override
    public String getId() {
        return ID;
    }
}
