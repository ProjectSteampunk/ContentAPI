package me.instrumentalityi.contentapi.paper.content.menus;

import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import me.instrumentalityi.menuapi.common.Menu;
import me.instrumentalityi.menuapi.common.props.Interactable;
import me.instrumentalityi.menuapi.common.props.Placeable;
import me.instrumentalityi.menuapi.paper.menus.PaperMenu;
import me.instrumentalityi.menuapi.paper.menus.props.impl.PaperButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ContentValue<T> {

    protected final String title;

    protected final Supplier<T> getter;
    protected final Consumer<T> setter;

    public ContentValue(String title, Supplier<T> getter, Consumer<T> setter) {
        this.title = title;
        this.getter = getter;
        this.setter = setter;
    }

    public Placeable getPlaceable(@NotNull PaperMenu menu, @NotNull Player player) {
        return PaperButton.builder(player).useItem(this::getItem).useAction(this.action()).build();
    }

    protected @NotNull ItemStack getItem() {
        ItemStack item = this.icon().createItemStack();

        item.setData(DataComponentTypes.CUSTOM_NAME, Component.text(this.title, NamedTextColor.LIGHT_PURPLE));

        return item;
    }

    public abstract @NotNull ItemType icon();

    public abstract @NotNull BiFunction<Menu, InventoryClickEvent, Interactable.ClickResult> action();
}
