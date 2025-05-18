package me.instrumentalityi.contentapi.paper;

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

        this.registerCommands();
    }

    private void registerCommands() {
        this.commandHandler = BukkitLamp.builder(this).build();
    }
}
