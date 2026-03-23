package me.instrumentalityi.contentapi.paper.content.impl;

import me.instrumentalityi.contentapi.paper.content.ContentRepository;
import me.instrumentalityi.contentapi.paper.content.container.ContainerData;
import me.instrumentalityi.contentapi.paper.content.interaction.Interactable;
import me.instrumentalityi.contentapi.paper.content.interaction.InteractionHandler;
import me.instrumentalityi.steampunklib.paper.utils.containers.impl.ItemContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemInteractionHandler implements InteractionHandler {

    public static ItemInteractionHandler INSTANCE = new ItemInteractionHandler();

    @EventHandler
    private void onInteract(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        if(stack == null || stack.getType() == Material.AIR) return;

        ItemContainer container = ItemContainer.of(stack);
        ContainerData data = container.read(new ContainerData());

        if(!data.isValid()) return;

        if(!(data.getContent() instanceof Item item)) {
            return;
        }

        item.interact(event);
    }

}
