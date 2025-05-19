package me.instrumentalityi.contentapi.paper.content.item.impl;

import lombok.Getter;
import me.instrumentalityi.contentapi.paper.content.item.ItemAttribute;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@Getter
public class MaterialAttribute implements ItemAttribute {

    private static final String ID = "material";

    private final @NotNull Material material;

    public MaterialAttribute(@NotNull Material material) {
        this.material = material;
    }

    @Override
    public String getId() {
        return ID;
    }
}
