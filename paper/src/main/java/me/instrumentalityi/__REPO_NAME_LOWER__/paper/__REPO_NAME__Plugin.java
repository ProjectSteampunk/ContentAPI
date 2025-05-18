package me.instrumentalityi.__REPO_NAME_LOWER__.paper;

import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

public class __REPO_NAME__Plugin extends JavaPlugin {

    private static __REPO_NAME__Plugin INSTANCE;

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
