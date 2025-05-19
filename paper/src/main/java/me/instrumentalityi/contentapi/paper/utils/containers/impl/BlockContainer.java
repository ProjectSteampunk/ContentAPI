package me.instrumentalityi.contentapi.paper.utils.containers.impl;

import com.jeff_media.customblockdata.CustomBlockData;
import me.instrumentalityi.contentapi.paper.ContentAPIPlugin;
import me.instrumentalityi.contentapi.paper.utils.containers.ContainerReader;
import me.instrumentalityi.contentapi.paper.utils.containers.ContainerWriter;
import me.instrumentalityi.contentapi.paper.utils.containers.SimpleContainer;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class BlockContainer implements SimpleContainer<Block> {

    private final @NotNull Block block;

    public BlockContainer(@NotNull Block block) {
        this.block = block;
    }

    @Override
    public <T extends ContainerReader> @NotNull T read(@NotNull T reader) {
        PersistentDataContainer pdc = this.getContainer();

        reader.read(pdc);

        return reader;
    }

    @Override
    public @NotNull BlockContainer write(@NotNull ContainerWriter writer) {
        PersistentDataContainer pdc = this.getContainer();

        writer.write(pdc);

        return this;
    }

    private @NotNull PersistentDataContainer getContainer() {
        return new CustomBlockData(this.block, ContentAPIPlugin.getInstance());
    }

    @Override
    public @NotNull Block retrieve() {
        return this.block;
    }

    public static @NotNull BlockContainer of(@NotNull Block block) {
        return new BlockContainer(block);
    }
}