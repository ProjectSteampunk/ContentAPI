package me.instrumentalityi.contentapi.paper.utils.containers.impl;

import me.instrumentalityi.contentapi.paper.utils.containers.ContainerReader;
import me.instrumentalityi.contentapi.paper.utils.containers.ContainerWriter;
import me.instrumentalityi.contentapi.paper.utils.containers.SimpleContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class ItemContainer implements SimpleContainer<ItemStack> {

    private final @NotNull ItemStack item;

    public ItemContainer(@NotNull ItemStack item) {
        this.item = item;
    }

    @Override
    public <T extends ContainerReader> @NotNull T read(@NotNull T reader) {
        ItemMeta meta = this.item.getItemMeta();
        if (meta == null) return reader;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        reader.read(pdc);

        return reader;
    }

    @Override
    public @NotNull ItemContainer write(@NotNull ContainerWriter writer) {
        ItemMeta meta = this.item.getItemMeta();
        if (meta == null) return this;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        writer.write(pdc);

        this.item.setItemMeta(meta);

        return this;
    }

    @Override
    public @NotNull ItemStack retrieve() {
        return this.item;
    }

    public static @NotNull ItemContainer of(@NotNull ItemStack item) {
        return new ItemContainer(item);
    }
}
