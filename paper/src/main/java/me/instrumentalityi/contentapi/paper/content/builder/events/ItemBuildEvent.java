package me.instrumentalityi.contentapi.paper.content.builder.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class ItemBuildEvent extends Event implements Cancellable {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final HandlerList HANDLERS = new HandlerList();

    private @NotNull ItemStack item;

    private boolean cancelled;

    public ItemBuildEvent(final @NotNull ItemStack item) {
        this.item = item;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
