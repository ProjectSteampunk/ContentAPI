package me.instrumentalityi.contentapi.paper.content.menus.views;

import me.instrumentalityi.menuapi.common.props.Placeable;
import me.instrumentalityi.menuapi.paper.menus.PaperMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface MenuView {

    Placeable getPlaceable(@NotNull PaperMenu menu, @NotNull Player player);
}
