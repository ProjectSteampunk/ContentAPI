package me.instrumentalityi.contentapi.paper.content.menus.views;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.instrumentalityi.contentapi.paper.content.Content;
import me.instrumentalityi.contentapi.paper.content.repository.ContentRepository;
import me.instrumentalityi.contentapi.paper.content.menus.ContentBrowseMenu;
import me.instrumentalityi.menuapi.paper.menus.PaperMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public record RepoView<T extends Content>(ContentRepository<T> repo) implements MenuView {

    @Override
    public @NotNull ItemStack getItem() {
        ItemStack item = ItemType.BEDROCK.createItemStack();
        item.setData(DataComponentTypes.CUSTOM_NAME, Component.text(this.repo.getTag(), NamedTextColor.LIGHT_PURPLE));
        return item;
    }

    @Override
    public void run(@NotNull PaperMenu menu, @NotNull Player player) {
        new ContentBrowseMenu(repo.getClazz(), menu).open(player);
    }
}
