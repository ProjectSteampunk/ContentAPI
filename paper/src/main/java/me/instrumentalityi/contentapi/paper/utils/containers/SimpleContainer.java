package me.instrumentalityi.contentapi.paper.utils.containers;

import me.instrumentalityi.contentapi.paper.utils.containers.impl.BlockContainer;
import me.instrumentalityi.contentapi.paper.utils.containers.impl.EntityContainer;
import me.instrumentalityi.contentapi.paper.utils.containers.impl.ItemContainer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SimpleContainer<T> {

    <R extends ContainerReader> @NotNull R read(@NotNull R reader);

    @NotNull SimpleContainer<T> write(@NotNull ContainerWriter writer);

    @NotNull T retrieve();

    static @Nullable SimpleContainer<?> of(Object object) {
        return switch (object) {
            case ItemStack item -> ItemContainer.of(item);
            case Block block -> BlockContainer.of(block);
            case Entity entity -> EntityContainer.of(entity);
            default -> null;
        };
    }
}
