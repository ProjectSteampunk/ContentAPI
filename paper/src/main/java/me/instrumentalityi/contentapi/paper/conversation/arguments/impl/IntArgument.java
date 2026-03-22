package me.instrumentalityi.contentapi.paper.conversation.arguments.impl;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.instrumentalityi.contentapi.paper.conversation.arguments.Argument;
import me.instrumentalityi.contentapi.paper.conversation.arguments.ArgumentException;
import me.instrumentalityi.steampunklib.paper.utils.PaperStringUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class IntArgument extends Argument<Integer> {
    public IntArgument(@NotNull Component prompt) {
        super(prompt);
    }

    @Override
    public void process(AsyncChatEvent event) {
        String input = PaperStringUtil.toPlainText(event.message());

        try {
            this.value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ArgumentException("Please provide a valid number.");
        }
    }
}
