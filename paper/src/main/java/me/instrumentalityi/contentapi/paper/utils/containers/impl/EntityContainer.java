package me.instrumentalityi.contentapi.paper.utils.containers.impl;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityContainer extends DataContainer<Entity> {
    public EntityContainer(@NotNull Entity holder) {
        super(holder);
    }

    public static EntityContainer of(Entity entity) {
        return new EntityContainer(entity);
    }
}
