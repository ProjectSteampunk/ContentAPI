package me.instrumentalityi.contentapi.paper.content.menus.views;

import me.instrumentalityi.menuapi.common.props.Placeable;
import me.instrumentalityi.menuapi.paper.menus.PaperMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface MenuView {

    @NotNull ItemStack getItem();

    default void run(@NotNull PaperMenu menu, @NotNull Player player, @NotNull InventoryClickEvent event) {
        this.run(menu, player);
    }

    void run(@NotNull PaperMenu menu, @NotNull Player player);
}
