package me.instrumentalityi.contentapi.paper.content.menus.values;

import me.instrumentalityi.contentapi.paper.content.menus.ContentValue;
import me.instrumentalityi.contentapi.paper.conversation.Conversation;
import me.instrumentalityi.contentapi.paper.conversation.ConversationModule;
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
import java.util.function.Supplier;

public class StringValue extends ContentValue<String> {
    public StringValue(String title, Supplier<String> getter, Consumer<String> setter) {
        super(title, getter, setter);
    }

    @Override
    public @NotNull ItemType icon() {
        return ItemType.OAK_HANGING_SIGN;
    }

    @Override
    public @NotNull BiFunction<Menu, InventoryClickEvent, Interactable.ClickResult> action() {
        return (m, e) -> {
            if(!(e.getWhoClicked() instanceof Player player)) return new Interactable.ClickResult.Cancelled();

            player.closeInventory();

            Conversation convo = new Conversation()
                    .addArgument("value", new StringArgument(Component.text("Please enter a value for: " + this.title)))
                    .setFinisher(c -> {
                        StringArgument value = c.getArgument("value", StringArgument.class);
                        String val = value.getValue();

                        if(val == null) return;

                        this.setter.accept(val);

                        m.open(player);
                    });

            Modules.get(ConversationModule.class).startConversation(player, convo);

            return new Interactable.ClickResult.Cancelled();
        };
    }
}
