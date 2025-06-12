package me.instrumentalityi.contentapi.paper.content.builder;

import me.instrumentalityi.contentapi.common.content.builder.ContentBuilder;
import me.instrumentalityi.contentapi.paper.content.ContentData;
import me.instrumentalityi.contentapi.paper.content.GameItem;
import me.instrumentalityi.contentapi.paper.content.builder.events.ItemBuildEvent;
import me.instrumentalityi.contentapi.paper.content.builder.events.ItemUpdateEvent;
import me.instrumentalityi.contentapi.paper.content.item.ItemAttribute;
import me.instrumentalityi.contentapi.paper.content.item.impl.MaterialAttribute;
import me.instrumentalityi.steampunklib.paper.utils.ItemEditor;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ItemBuilder implements ContentBuilder<ItemStack> {

    private final GameItem gameItem;
    private final String[] loreFormat;

    public ItemBuilder(GameItem gameItem, String... loreFormat) {
        this.gameItem = gameItem;
        this.loreFormat = loreFormat;
    }

    public ItemBuilder(GameItem gameItem) {
        this(gameItem, "lore");
    }

    @Override
    public @Nullable ItemStack build() {
        return this.buildWithEvent(ItemBuildEvent::new, null);
    }

    @Override
    public @Nullable ItemStack build(@NotNull ItemStack previous) {
        return this.buildWithEvent(item -> new ItemUpdateEvent(item, previous), previous);
    }

    private @Nullable ItemStack buildWithEvent(@Nullable Function<ItemStack, Event> event, @Nullable ItemStack previous) {
        ItemStack item = this.buildItem(previous);

        if (event != null && !event.apply(item).callEvent()) return null;

        return item;
    }

    private ItemStack buildItem(@Nullable ItemStack previous) {
        MaterialAttribute material = this.gameItem.getAttribute(MaterialAttribute.class);

        ItemStack item = previous == null ? new ItemStack(material.getMaterial()) : previous;

        for (ItemAttribute attribute : this.gameItem.getAttributes()) {
            attribute.applyToItem(item);
        }

        return new ItemEditor(item)
                .meta(meta -> {
                    List<Component> lore = new ArrayList<>();
                    Component empty = Component.empty();

                    for(String format : this.loreFormat) {
                        if(format == null || format.isBlank()) {
                            if(!lore.isEmpty()) lore.add(empty);
                            continue;
                        }

                        ItemAttribute attribute = this.gameItem.getAttribute(format);
                        if(attribute == null) {
                            continue;
                        }

                        attribute.applyToLore(lore);
                    }

                    meta.lore(lore);
                })
                .write(new ContentData(gameItem))
                .build();
    }
}
