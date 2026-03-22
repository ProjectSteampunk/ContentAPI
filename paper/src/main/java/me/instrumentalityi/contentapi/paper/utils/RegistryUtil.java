package me.instrumentalityi.contentapi.paper.utils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public class RegistryUtil {

    public static @NotNull ItemType getItemType(@NotNull String input) {
        return Registry.ITEM.getOrThrow(namespaceKey(input));
    }

    private static @NotNull Key namespaceKey(@NotNull String input) {
        return input.contains(":")
                ? Key.key(input.toLowerCase())
                : Key.key("minecraft", input.toLowerCase());
    }
}
