package me.instrumentalityi.contentapi.paper.conversation;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.instrumentalityi.contentapi.paper.ContentAPIPlugin;
import me.instrumentalityi.contentapi.paper.conversation.arguments.impl.IntArgument;
import me.instrumentalityi.contentapi.paper.conversation.arguments.impl.StringArgument;
import me.instrumentalityi.steampunklib.common.modules.Module;
import me.instrumentalityi.steampunklib.common.modules.exceptions.ModuleStartupException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ConversationModule implements Module, Listener {

    private Map<UUID, Conversation.Cursor> cursors;

    public ConversationModule(ContentAPIPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void start() throws ModuleStartupException {
        this.cursors = new ConcurrentHashMap<>();
    }

    @Override
    public void stop() {
        this.cursors.clear();
        this.cursors = null;
    }

    public @Nullable Conversation.Cursor getCursor(Player player) {
        return this.cursors.get(player.getUniqueId());
    }

    public void startConversation(@NotNull Player player, @NotNull Conversation convo) {
        Conversation.Cursor cursor = convo.cursor(player);
        cursor.processPrompt();

        this.cursors.put(player.getUniqueId(), cursor);
    }

    public void finishConversation(@NotNull Player player) {
        Conversation.Cursor cursor = this.cursors.get(player.getUniqueId());
        if(cursor == null) return;

        if(cursor.isFinished()) {
            Conversation convo = cursor.getConversation();
            Consumer<Conversation> finisher = convo.getFinisher();

            if(finisher != null) finisher.accept(convo);
        }

        this.cursors.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onChat(@NotNull AsyncChatEvent event) {
        Player player = event.getPlayer();
        Conversation.Cursor cursor = this.getCursor(player);

        if (cursor == null) return;

        Conversation.Cursor.Result result = cursor.processCurrent(event);
        result.proceed();

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onLeave(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.finishConversation(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Conversation conversation = new Conversation()
                .addArgument("name", new StringArgument(Component.text("What is your name?").color(NamedTextColor.AQUA)))
                .addArgument("age", new IntArgument(Component.text("How old are you?").color(NamedTextColor.AQUA)))
                .setFinisher(convo -> {
                    String name = convo.getArgument("name", StringArgument.class).getValue();
                    Integer age = convo.getArgument("age", IntArgument.class).getValue();

                    player.sendMessage(Component.text("You are " + name + " " + age));
                });

        this.startConversation(player, conversation);
    }
}
