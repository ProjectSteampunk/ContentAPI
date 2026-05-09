package me.instrumentalityi.contentapi.paper.content.impl;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import lombok.Getter;
import me.instrumentalityi.contentapi.paper.content.Content;
import me.instrumentalityi.contentapi.paper.content.repository.ContentRepository;
import me.instrumentalityi.contentapi.paper.content.container.ContainerData;
import me.instrumentalityi.contentapi.paper.content.grant.Grantable;
import me.instrumentalityi.contentapi.paper.content.interaction.Interactable;
import me.instrumentalityi.contentapi.paper.content.menus.values.ConversationValue;
import me.instrumentalityi.contentapi.paper.content.menus.values.EditableValues;
import me.instrumentalityi.contentapi.paper.content.menus.values.MenuEditable;
import me.instrumentalityi.contentapi.paper.content.menus.views.ItemView;
import me.instrumentalityi.contentapi.paper.content.menus.views.MenuView;
import me.instrumentalityi.contentapi.paper.content.menus.views.MenuViewable;
import me.instrumentalityi.steampunklib.paper.utils.PaperStringUtil;
import me.instrumentalityi.steampunklib.paper.utils.RegistryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Item implements Content, Grantable, Interactable<PlayerInteractEvent>, MenuViewable, MenuEditable {

    public static final @NotNull String ID = "item";

    // DEFAULTS
    private static final ItemType DEFAULT_ITEM_TYPE = ItemType.ARROW;
    private static final String DEFAULT_TITLE = "Unspecified";
    private static final String DEFAULT_DESCRIPTION = "Enter a description for this item.";
    private static final String ITEM_MODEL = null;

    public static final int MAX_DESCRIPTION_LENGTH = 45;

    // INITIALIZATION
    @Getter
    private final @NotNull ContentRepository<? extends Item> repo;
    @Getter
    protected final @NotNull EditableValues values;

    // COMPONENTS
    @Getter
    protected @NotNull String id;
    protected @NotNull ItemType material = DEFAULT_ITEM_TYPE;
    protected @NotNull String title = DEFAULT_TITLE;
    protected @NotNull String description = DEFAULT_DESCRIPTION;
    protected @Nullable String itemModel = ITEM_MODEL;

    public Item(@NotNull ContentRepository<? extends Item> repo, @NotNull String id) {
        this.repo = repo;
        this.id = id;
        this.values = new EditableValues(
                new ConversationValue.Text("Content ID", () -> this.id, s -> this.id = s),
                new ConversationValue.Text("Item Title", () -> this.title, s -> this.title = s),
                new ConversationValue.Item("Item Type", () -> this.material, s -> this.material = s),
                new ConversationValue.Text("Item Description", () -> this.description, s -> this.description = s),
                new ConversationValue.Text("Item Model", () -> this.itemModel, s -> this.itemModel = s)
        );
    }

    @Override
    public void write(@NotNull ConfigurationSection config) {
        config.set("material", this.material.key().asMinimalString());
        config.set("title", this.title);
        config.set("description", this.description);
        config.set("model", this.itemModel);
    }

    @Override
    public void read(@NotNull ConfigurationSection config) {
        this.material = RegistryUtil.getItemType(config.getString("material",
                DEFAULT_ITEM_TYPE.key().asMinimalString()));
        this.title = config.getString("title", DEFAULT_TITLE);
        this.description = config.getString("description", DEFAULT_DESCRIPTION);
        this.itemModel = config.getString("model", ITEM_MODEL);
    }

    @Override
    public Result grant(Player player) {
        ItemStack item = this.craftItem();
        Component title = this.craftTitle(item);

        if (!player.getInventory().addItem(item).isEmpty()) {
            return new NoSpace(title);
        }

        return new GrantedItem(title);
    }

    public @NotNull ItemStack craftItem() {
        ItemStack item = this.material.createItemStack();
        return this.shapeItem(item);
    }

    protected ItemStack shapeItem(@NotNull ItemStack item) {
        item.editPersistentDataContainer(this::shapeData);
        item.setData(DataComponentTypes.CUSTOM_NAME, this.craftTitle(item));
        item.setData(DataComponentTypes.LORE, this.craftLore(item));

        if (this.itemModel != null) {
            item.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey.fromString(this.itemModel));
        }

        return item;
    }

    protected @NotNull Component craftTitle(@NotNull ItemStack item) {
        return Component.text(this.title)
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false);
    }

    protected @NotNull ItemLore craftLore(@NotNull ItemStack item) {
        return ItemLore.lore()
                .addLine(Component.text("Item").color(NamedTextColor.GRAY))
                .addLine(Component.empty())
                .addLines(PaperStringUtil.wrap(this.description, MAX_DESCRIPTION_LENGTH,
                        line -> Component.text(line, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)))
                .build();
    }

    private void shapeData(PersistentDataContainer pdc) {
        ContainerData data = new ContainerData(this.repo, this);

        data.write(pdc);
    }

    @Override
    public @NotNull MenuView getView() {
        return new ItemView(this);
    }

    @Override
    public @NotNull ItemStack getPreview() {
        return this.craftItem();
    }

    public record NoSpace(Component title) implements Result {
        @Override
        public Component message() {
            return MiniMessage.miniMessage().deserialize("<red>Attempted to grant '<title>' to the player, but no space was found.", Placeholder.component("title", title));
        }
    }

    public record GrantedItem(Component title) implements Result {
        @Override
        public Component message() {
            return MiniMessage.miniMessage().deserialize("<green>Granted '<title>' to the player.", Placeholder.component("title", title));
        }
    }
}
