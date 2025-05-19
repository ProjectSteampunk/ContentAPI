package me.instrumentalityi.contentapi.paper.utils.containers.impl;

import me.instrumentalityi.contentapi.paper.utils.containers.ContainerReader;
import me.instrumentalityi.contentapi.paper.utils.containers.ContainerWriter;
import me.instrumentalityi.contentapi.paper.utils.containers.SimpleContainer;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;

public abstract class DataContainer<R extends PersistentDataHolder> implements SimpleContainer<R> {

    private final @NotNull R holder;

    public DataContainer(@NotNull R holder) {
        this.holder = holder;
    }

    @Override
    public <T extends ContainerReader> @NotNull T read(@NotNull T reader) {
        PersistentDataContainer pdc = this.getContainer();

        reader.read(pdc);

        return reader;
    }

    @Override
    public @NotNull DataContainer<R> write(@NotNull ContainerWriter writer) {
        PersistentDataContainer pdc = this.getContainer();

        writer.write(pdc);

        return this;
    }

    public @NotNull PersistentDataContainer getContainer() {
        return this.holder.getPersistentDataContainer();
    }

    @Override
    public @NotNull R retrieve() {
        return this.holder;
    }
}
