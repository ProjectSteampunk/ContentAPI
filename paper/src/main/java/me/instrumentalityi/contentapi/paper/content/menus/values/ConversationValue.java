package me.instrumentalityi.contentapi.paper.content.menus.values;

import me.instrumentalityi.contentapi.paper.content.menus.ContentValue;
import me.instrumentalityi.contentapi.paper.conversation.Conversation;
import me.instrumentalityi.contentapi.paper.conversation.ConversationModule;
import me.instrumentalityi.contentapi.paper.conversation.arguments.Argument;
import me.instrumentalityi.contentapi.paper.conversation.arguments.impl.IntArgument;
import me.instrumentalityi.contentapi.paper.conversation.arguments.impl.ItemTypeArgument;
import me.instrumentalityi.contentapi.paper.conversation.arguments.impl.StringArgument;
import me.instrumentalityi.menuapi.common.Menu;
import me.instrumentalityi.menuapi.common.props.Interactable;
import me.instrumentalityi.steampunklib.common.modules.Modules;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ConversationValue<T, R extends Argument<T>> extends ContentValue<T> {

    private final @NotNull Class<R> clazz;
    private final @NotNull Function<Component, R> argCreator;

    public ConversationValue(String title, Supplier<T> getter, Consumer<T> setter, @NotNull Class<R> clazz, @NotNull Function<Component, R> argCreator) {
        super(title, getter, setter);
        this.clazz = clazz;
        this.argCreator = argCreator;
    }

    @Override
    public @NotNull BiFunction<Menu, InventoryClickEvent, Interactable.ClickResult> action() {
        return (m, e) -> {
            if(!(e.getWhoClicked() instanceof Player player)) return new Interactable.ClickResult.Cancelled();

            player.closeInventory();

            Conversation convo = new Conversation()
                    .addArgument("value", this.argCreator.apply(Component.text("Please enter a value for: " + this.title)))
                    .setFinisher(c -> {
                        R value = c.getArgument("value", this.clazz);
                        T val = value.getValue();

                        if(val == null) return;

                        this.setter.accept(val);

                        m.open(player);
                    });

            Modules.get(ConversationModule.class).startConversation(player, convo);

            return new Interactable.ClickResult.Cancelled();
        };
    }

    public static class Text extends ConversationValue<String, StringArgument> {
        public Text(String title, Supplier<String> getter, Consumer<String> setter) {
            super(title, getter, setter, StringArgument.class, StringArgument::new);
        }

        @Override
        public @NotNull ItemType icon() {
            return ItemType.OAK_HANGING_SIGN;
        }
    }

    public static class Item extends ConversationValue<ItemType, ItemTypeArgument> {
        public Item(String title, Supplier<ItemType> getter, Consumer<ItemType> setter) {
            super(title, getter, setter, ItemTypeArgument.class, ItemTypeArgument::new);
        }

        @Override
        public @NotNull ItemType icon() {
            return ItemType.GRASS_BLOCK;
        }
    }

    public static class Number extends ConversationValue<Integer, IntArgument> {

        public Number(String title, Supplier<Integer> getter, Consumer<Integer> setter) {
            super(title, getter, setter, IntArgument.class, IntArgument::new);
        }

        @Override
        public @NotNull ItemType icon() {
            return ItemType.HONEY_BOTTLE;
        }
    }
}