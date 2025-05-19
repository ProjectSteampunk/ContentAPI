package me.instrumentalityi.contentapi.paper.utils;

import me.instrumentalityi.contentapi.paper.utils.containers.ContainerWriter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemEditor {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemEditor(Material material) {
        this(ItemStack.of(material));
    }

    public ItemEditor(@NotNull ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemEditor meta(Consumer<ItemMeta> builder) {
        builder.accept(this.meta);

        this.updateItemMeta();

        return this;
    }

    public ItemEditor write(ContainerWriter writer) {
        PersistentDataContainer pdc = this.meta.getPersistentDataContainer();

        writer.write(pdc);
        this.updateItemMeta();

        return this;
    }

    public ItemStack build() {
        return this.item;
    }

    private void updateItemMeta() {
        this.item.setItemMeta(this.meta);
    }
}
