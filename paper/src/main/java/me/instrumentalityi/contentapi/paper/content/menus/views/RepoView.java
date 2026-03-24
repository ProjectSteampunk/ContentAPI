package me.instrumentalityi.contentapi.paper.content.menus.views;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.instrumentalityi.contentapi.paper.content.Content;
import me.instrumentalityi.contentapi.paper.content.ContentRepository;
import me.instrumentalityi.contentapi.paper.content.menus.ContentBrowseMenu;
import me.instrumentalityi.menuapi.common.props.Interactable;
import me.instrumentalityi.menuapi.common.props.Placeable;
import me.instrumentalityi.menuapi.paper.menus.PaperMenu;
import me.instrumentalityi.menuapi.paper.menus.props.impl.PaperButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

public record RepoView<T extends Content>(ContentRepository<T> repo) implements MenuView {

    @Override
    public Placeable getPlaceable(@NotNull PaperMenu menu, @NotNull Player player) {
        return PaperButton.builder(player).useItem(this::craftRepo)
                .useAction((m, e) -> {
                    new ContentBrowseMenu(repo.getClazz(), menu).open(player);
                    return new Interactable.ClickResult.Cancelled();
                }).build();
    }

    private @NotNull ItemStack craftRepo() {
        ItemStack item = ItemType.BEDROCK.createItemStack();
        item.setData(DataComponentTypes.CUSTOM_NAME, Component.text(this.repo.getTag(), NamedTextColor.LIGHT_PURPLE));
        return item;
    }
}
