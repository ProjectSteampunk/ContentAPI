package me.instrumentalityi.contentapi.paper.conversation;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.Setter;
import me.instrumentalityi.contentapi.paper.conversation.arguments.Argument;
import me.instrumentalityi.contentapi.paper.conversation.arguments.ArgumentException;
import me.instrumentalityi.steampunklib.common.modules.Modules;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Conversation {

    private final LinkedHashMap<String, Argument<?>> arguments = new LinkedHashMap<>();

    @Getter
    private Consumer<Conversation> finisher;

    public Conversation() {
    }

    public Conversation addArgument(@NotNull String id, @NotNull Argument<?> argument) {
        this.arguments.put(id, argument);
        return this;
    }

    public <T extends Argument<?>> T getArgument(@NotNull String id, @NotNull Class<T> type) {
        Argument<?> argument = this.arguments.get(id);

        return type.cast(argument);
    }

    public Conversation setFinisher(Consumer<Conversation> finisher) {
        this.finisher = finisher;
        return this;
    }

    public Cursor cursor(Player player) {
        return new Cursor(this, player);
    }

    public static class Cursor {
        @Getter
        private final Conversation conversation;
        private final Player player;

        private int index = 0;

        public Cursor(Conversation conversation, Player player) {
            this.conversation = conversation;
            this.player = player;
        }

        public Result processCurrent(AsyncChatEvent event) {
            Map.Entry<String, Argument<?>> entry = this.getEntryAt(this.conversation.arguments, this.index);

            if (entry == null) return new Result.Completed(this.player);

            try {
                entry.getValue().process(event);

                if(!this.isFinishedNext()) return new Result.Next(this);

                return new Result.Completed(this.player);
            } catch (ArgumentException e) {
                this.player.sendMessage(Component.text(e.getMessage()).color(NamedTextColor.RED));
                return new Result.Invalid();
            }
        }

        public void processNext() {
            this.index++;

            this.processPrompt();
        }

        public void processPrompt() {
            Map.Entry<String, Argument<?>> entry = this.getEntryAt(this.conversation.arguments, this.index);

            if (entry == null) return;

            this.player.sendMessage(entry.getValue().getPrompt());
        }

        private boolean isFinishedNext() {
            return this.index + 1 >= this.conversation.arguments.size();
        }

        public boolean isFinished() {
            return this.index >= this.conversation.arguments.size();
        }

        private <K, V> Map.Entry<K, V> getEntryAt(LinkedHashMap<K, V> map, int index) {
            int i = 0;
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (i++ == index) return entry;
            }
            return null;
        }

        public interface Result {
            default void proceed() {};

            record Completed(Player player) implements Result {
                @Override
                public void proceed() {
                    Modules.get(ConversationModule.class).finishConversation(player);
                }
            }

            record Next(Cursor cursor) implements Result {
                @Override
                public void proceed() {
                    cursor.processNext();
                }
            }

            record Invalid() implements Result {}
        }
    }
}
