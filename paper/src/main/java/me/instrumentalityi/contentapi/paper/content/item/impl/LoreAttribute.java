package me.instrumentalityi.contentapi.paper.content.item.impl;

import me.instrumentalityi.contentapi.paper.content.item.ItemAttribute;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoreAttribute implements ItemAttribute {

    private static final String ID = "lore";

    private final List<Component> components;

    public LoreAttribute(final @NotNull List<Component> components) {
        this.components = components;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void applyToLore(@NotNull List<Component> lore) {
        lore.addAll(this.components);
    }
}
