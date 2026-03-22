package me.instrumentalityi.contentapi.paper.content.impl;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.instrumentalityi.contentapi.paper.content.Content;
import me.instrumentalityi.contentapi.paper.content.grant.Grantable;
import me.instrumentalityi.contentapi.paper.utils.RegistryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public class Item implements Content, Grantable {

    public static final String ID = "item";

    private static final ItemType DEFAULT_ITEM_TYPE = ItemType.ARROW;
    private static final String DEFAULT_TITLE = "Unspecified";
    private static final String DEFAULT_DESCRIPTION = "Enter a description for this item.";

    @Getter
    private final @NotNull String id;

    private @NotNull ItemType material = DEFAULT_ITEM_TYPE;
    private @NotNull String title = DEFAULT_TITLE;
    private @NotNull String description = DEFAULT_DESCRIPTION;

    public Item(@NotNull String id) {
        this.id = id;
    }

    @Override
    public void write(@NotNull ConfigurationSection config) {
        config.set("material", this.material.key().asMinimalString());
        config.set("title", this.title);
        config.set("description", this.description);
    }

    @Override
    public void read(@NotNull ConfigurationSection config) {
        this.material = RegistryUtil.getItemType(config.getString("material",
                DEFAULT_ITEM_TYPE.key().asMinimalString()));
        this.title = config.getString("title", DEFAULT_TITLE);
        this.description = config.getString("description", DEFAULT_DESCRIPTION);
    }

    @Override
    public Result grant(Player player) {
        ItemStack item = this.craftItem();

        if (!player.getInventory().addItem(item).isEmpty()) {
            return new NoSpace();
        }

        return new Result.Granted();
    }

    protected @NotNull ItemStack craftItem() {
        ItemStack item = this.material.createItemStack();
        item.setData(DataComponentTypes.CUSTOM_NAME, this.craftTitle());
        item.setData(DataComponentTypes.LORE, this.craftLore());

        return item;
    }

    private @NotNull Component craftTitle() {
        return Component.text(this.title)
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
    }

    private @NotNull ItemLore craftLore() {
        return ItemLore.lore()
                .addLine(Component.text("Item").color(NamedTextColor.GRAY))
                .addLine(Component.empty())
                .addLine(Component.text(this.description).color(NamedTextColor.WHITE))
                .build();
    }

    public record NoSpace() implements Result {
    }
}
