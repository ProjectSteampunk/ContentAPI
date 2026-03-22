package me.instrumentalityi.contentapi.paper;

import me.instrumentalityi.contentapi.paper.content.ContentModule;
import me.instrumentalityi.contentapi.paper.conversation.ConversationModule;
import me.instrumentalityi.steampunklib.common.modules.Modules;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

public class ContentAPIPlugin extends JavaPlugin {

    private static ContentAPIPlugin INSTANCE;

    private Lamp<BukkitCommandActor> commandHandler;

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.registerModules();
        this.registerListeners();
        this.registerCommands();
    }

    private void registerModules() {
        Modules.register(new ConversationModule(this));
        Modules.register(new ContentModule(this));
    }

    private void registerListeners() {

    }

    private void registerCommands() {
        this.commandHandler = BukkitLamp.builder(this).build();

//        this.commandHandler.register(new ContentCommand());
    }

    public static ContentAPIPlugin getInstance() {
        return INSTANCE;
    }
}
