package me.instrumentalityi.contentapi.paper;

import me.instrumentalityi.contentapi.common.ContentModule;
import me.instrumentalityi.contentapi.paper.content.ContentCommand;
import me.instrumentalityi.contentapi.paper.content.GameItem;
import me.instrumentalityi.contentapi.paper.content.PaperContentModule;
import me.instrumentalityi.contentapi.paper.content.resolver.ConfigContentLoader;
import me.instrumentalityi.contentapi.paper.content.resolver.GameItemResolver;
import me.instrumentalityi.steampunklib.common.modules.Modules;
import me.instrumentalityi.steampunklib.common.modules.exceptions.ModuleException;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
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

        try {
            ContentModule module = Modules.register(new PaperContentModule());
            module.registerType("item", GameItem.class, GameItem::new);
            module.registerResolver(ItemStack.class, new GameItemResolver());

            ConfigContentLoader loader = new ConfigContentLoader(this);
            loader.load();
        } catch (ModuleException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        this.registerCommands();
    }

    private void registerCommands() {
        this.commandHandler = BukkitLamp.builder(this).build();

        this.commandHandler.register(new ContentCommand());
    }

    public static ContentAPIPlugin getInstance() {
        return INSTANCE;
    }
}
