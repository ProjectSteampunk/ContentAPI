package me.instrumentalityi.contentapi.paper.content.menus.views;

import me.instrumentalityi.contentapi.paper.content.impl.Item;
import me.instrumentalityi.contentapi.paper.content.menus.ContentEditMenu;
import me.instrumentalityi.menuapi.common.Menu;
import me.instrumentalityi.menuapi.common.props.Interactable;
import me.instrumentalityi.menuapi.common.props.Placeable;
import me.instrumentalityi.menuapi.paper.menus.PaperMenu;
import me.instrumentalityi.menuapi.paper.menus.props.impl.PaperButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public record ItemView(Item item) implements MenuView {

    @Override
    public @NotNull ItemStack getItem() {
        return item.craftItem();
    }

    @Override
    public void run(@NotNull PaperMenu menu, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if(event.isLeftClick()){
            this.run(menu, player);
            return;
        }

        item.grant(player);
    }

    @Override
    public void run(@NotNull PaperMenu menu, @NotNull Player player) {
        new ContentEditMenu(item, menu).open(player);
    }
}
