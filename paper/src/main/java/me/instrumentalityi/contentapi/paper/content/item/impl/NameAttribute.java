package me.instrumentalityi.contentapi.paper.content.item.impl;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.instrumentalityi.contentapi.paper.ContentAPIPlugin;
import me.instrumentalityi.contentapi.paper.content.item.ItemAttribute;
import me.instrumentalityi.steampunklib.paper.utils.ItemEditor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class NameAttribute implements ItemAttribute {

    private static final String ID = "name";

    private final Component name;

    public NameAttribute(Component name) {
        this.name = name;
    }

    @Override
    public void applyToItem(@NotNull ItemEditor editor) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTask(ContentAPIPlugin.getInstance(), () -> {
            editor.modify(item -> item.setData(DataComponentTypes.CUSTOM_NAME, name));
            future.complete(null);
        });

        future.join();
    }

    @Override
    public String getId() {
        return ID;
    }
}
