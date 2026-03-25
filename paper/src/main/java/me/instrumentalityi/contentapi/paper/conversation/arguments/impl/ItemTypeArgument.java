package me.instrumentalityi.contentapi.paper.conversation.arguments.impl;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.instrumentalityi.contentapi.paper.conversation.arguments.Argument;
import me.instrumentalityi.contentapi.paper.conversation.arguments.ArgumentException;
import me.instrumentalityi.contentapi.paper.utils.RegistryUtil;
import me.instrumentalityi.steampunklib.paper.utils.PaperStringUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public class ItemTypeArgument extends Argument<ItemType> {
    public ItemTypeArgument(@NotNull Component prompt) {
        super(prompt);
    }

    @Override
    public void process(AsyncChatEvent event) {
        String val = PaperStringUtil.toPlainText(event.message());

        try {
            this.value = RegistryUtil.getItemType(val);
        } catch (Exception e) {
            throw new ArgumentException("Invalid type: " + val);
        }
    }
}
