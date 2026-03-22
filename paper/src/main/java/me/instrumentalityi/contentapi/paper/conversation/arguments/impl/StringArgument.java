package me.instrumentalityi.contentapi.paper.conversation.arguments.impl;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.instrumentalityi.contentapi.paper.conversation.arguments.Argument;
import me.instrumentalityi.steampunklib.paper.utils.PaperStringUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class StringArgument extends Argument<String> {
    public StringArgument(@NotNull Component prompt) {
        super(prompt);
    }

    @Override
    public void process(AsyncChatEvent event) {
        this.value = PaperStringUtil.toPlainText(event.message());
    }
}
