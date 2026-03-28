package me.instrumentalityi.contentapi.paper.content.menus;

import com.google.common.primitives.Ints;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.instrumentalityi.contentapi.paper.content.menus.values.MenuEditable;
import me.instrumentalityi.menuapi.common.props.Interactable;
import me.instrumentalityi.menuapi.common.props.Placeable;
import me.instrumentalityi.menuapi.paper.menus.PaperMenu;
import me.instrumentalityi.menuapi.paper.menus.props.impl.PaperButton;
import me.instrumentalityi.menuapi.paper.utils.PaginationHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContentEditMenu extends PaperMenu {

    private static final int ROWS = 4;
    private static final int NEXT_SLOT = 34;
    private static final int PREV_SLOT = 28;

    private static final int BACK_SLOT = 0;
    private static final int PREVIEW_SLOT = 4;

    private static final int[] SLOTS = Ints.concat(
            new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17},
            new int[]{18, 19, 20, 21, 22, 23, 24, 25, 26}
    );

    private static final int[] PLACEHOLDERS = Ints.concat(
            new int[]{1, 2, 3, 5, 6, 7, 8},
            new int[]{27, 29, 30, 31, 32, 33, 35}
    );

    private final @NotNull MenuEditable editable;
    private final @NotNull PaginationHelper<ContentValue<?>> pagination;
    private final @Nullable PaperMenu previousMenu;

    public ContentEditMenu(@NotNull MenuEditable editable, @Nullable PaperMenu previousMenu) {
        super(ROWS, Component.text(editable.getId(), NamedTextColor.DARK_PURPLE));
        this.editable = editable;
        this.previousMenu = previousMenu;
        this.pagination = new PaginationHelper<>(this, this::craftEditableValue)
                .paging(editable.getValues().getValues(), SLOTS.length);
    }

    public ContentEditMenu(@NotNull MenuEditable editable) {
        this(editable, null);
    }

    @Override
    protected void build(@NotNull Player player) {
        for (int i : PLACEHOLDERS) {
            this.place(i, PaperButton.builder(player).useItem(this::getPlaceholder).useAction((m, e) -> new Interactable.ClickResult.Cancelled()).build());
        }

        PaginationHelper.Placer<ContentValue<?>> placer = this.pagination.placer(player, SLOTS)
                .next(this.getNextArrow(), NEXT_SLOT)
                .previous(this.getPreviousArrow(), PREV_SLOT);
        placer.populate();

        this.place(BACK_SLOT, PaperButton.builder(player).useItem(this::getBackArrow)
                .useAction((m, e) -> {
                    if(this.previousMenu == null) return new Interactable.ClickResult.Cancelled();

                    this.previousMenu.open(player);

                    return new Interactable.ClickResult.Cancelled();
                }).build());

        this.place(PREVIEW_SLOT, this.craftPreview(player, this.editable));
    }

    @Override
    public boolean close() {
        this.editable.getRepo().getLogic().saveContent(this.editable);

        return true;
    }

    private Placeable craftEditableValue(@NotNull Player player, @NotNull ContentValue<?> value) {
        return value.getPlaceable(this, player);
    }

    private Placeable craftPreview(@NotNull Player player, @NotNull MenuEditable editable) {
        return PaperButton.builder(player)
                .useItem(editable::getPreview)
                .useAction((m,e) -> new Interactable.ClickResult.Cancelled())
                .build();
    }

    private @NotNull ItemStack getNextArrow() {
        ItemStack next = ItemType.ARROW.createItemStack();

        next.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Next", NamedTextColor.GREEN));

        return next;
    }

    private @NotNull ItemStack getPreviousArrow() {
        ItemStack prev = ItemType.ARROW.createItemStack();

        prev.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Previous", NamedTextColor.GREEN));

        return prev;
    }

    private @NotNull ItemStack getBackArrow() {
        ItemStack back = ItemType.ANVIL.createItemStack();

        back.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Back", NamedTextColor.GREEN));

        return back;
    }

    private @NotNull ItemStack getPlaceholder() {
        ItemStack placeholder = ItemType.GRAY_STAINED_GLASS_PANE.createItemStack();

        placeholder.setData(DataComponentTypes.CUSTOM_NAME, Component.empty());

        return placeholder;
    }
}
