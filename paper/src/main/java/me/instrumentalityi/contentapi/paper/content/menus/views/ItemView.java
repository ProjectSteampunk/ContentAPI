package me.instrumentalityi.contentapi.paper.content.menus.views;

import me.instrumentalityi.contentapi.paper.content.impl.Item;
import me.instrumentalityi.menuapi.common.props.Interactable;
import me.instrumentalityi.menuapi.common.props.Placeable;
import me.instrumentalityi.menuapi.paper.menus.PaperMenu;
import me.instrumentalityi.menuapi.paper.menus.props.impl.PaperButton;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record ItemView(Item item) implements MenuView {
    @Override
    public Placeable getPlaceable(@NotNull PaperMenu menu, @NotNull Player player) {
        return PaperButton.builder(player).useItem(item::craftItem)
                .useAction((m, e) -> {
                    player.sendMessage(Component.text("Wow, you clicked an item, it does nothing yet"));
                    return new Interactable.ClickResult.Cancelled();
                }).build();
    }
}
