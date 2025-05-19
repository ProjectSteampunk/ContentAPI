package me.instrumentalityi.contentapi.paper.content.builder.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class ItemUpdateEvent extends ItemBuildEvent {

    private @NotNull ItemStack previousItem;

    public ItemUpdateEvent(@NotNull ItemStack item, @NotNull ItemStack previousItem) {
        super(item);
        this.previousItem = previousItem;
    }
}
