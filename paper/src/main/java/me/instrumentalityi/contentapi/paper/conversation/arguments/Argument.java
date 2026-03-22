package me.instrumentalityi.contentapi.paper.conversation.arguments;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class Argument<T> {

    private final @NotNull Component prompt;
    protected @Nullable T value;

    public Argument(@NotNull Component prompt) {
        this.prompt = prompt;
    }

    public abstract void process(AsyncChatEvent event);
}
