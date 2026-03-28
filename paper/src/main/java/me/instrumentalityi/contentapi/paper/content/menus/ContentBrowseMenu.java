package me.instrumentalityi.contentapi.paper.content.menus;

import com.google.common.primitives.Ints;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.instrumentalityi.contentapi.paper.content.Content;
import me.instrumentalityi.contentapi.paper.content.ContentModule;
import me.instrumentalityi.contentapi.paper.content.repository.ContentRepository;
import me.instrumentalityi.contentapi.paper.content.menus.views.MenuView;
import me.instrumentalityi.contentapi.paper.content.menus.views.MenuViewable;
import me.instrumentalityi.contentapi.paper.conversation.Conversation;
import me.instrumentalityi.contentapi.paper.conversation.ConversationModule;
import me.instrumentalityi.contentapi.paper.conversation.arguments.impl.StringArgument;
import me.instrumentalityi.menuapi.common.props.Interactable;
import me.instrumentalityi.menuapi.common.props.Placeable;
import me.instrumentalityi.menuapi.paper.menus.PaperMenu;
import me.instrumentalityi.menuapi.paper.menus.props.impl.PaperButton;
import me.instrumentalityi.menuapi.paper.utils.PaginationHelper;
import me.instrumentalityi.steampunklib.common.modules.Modules;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ContentBrowseMenu extends PaperMenu {

    private static final int ROWS = 5;
    private static final int NEXT_SLOT = 43;
    private static final int PREV_SLOT = 37;

    private static final int BACK_SLOT = 0;
    private static final int CREATE_SLOT = 40;

    private static final int[] SLOTS = Ints.concat(
            new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17},
            new int[]{18, 19, 20, 21, 22, 23, 24, 25, 26},
            new int[]{27, 28, 29, 30, 31, 32, 33, 34, 35}
    );

    private static final int[] PLACEHOLDERS = Ints.concat(
            new int[]{1, 2, 3, 4, 5, 6, 7, 8},
            new int[]{36, 38, 39, 40, 41, 42, 44}
    );

    private final @NotNull Class<? extends Content> clazz;
    private final @Nullable ContentRepository<?> repo;

    private @NotNull PaginationHelper<MenuView> pagination;
    private final @Nullable PaperMenu previousMenu;

    public ContentBrowseMenu(@NotNull Class<? extends Content> content, @Nullable PaperMenu previousMenu) {
        super(ROWS, Component.text("Content Menu", NamedTextColor.DARK_PURPLE));
        this.clazz = content;
        this.repo = this.resolveRepository(content);
        this.previousMenu = previousMenu;
        this.pagination = this.craftPagination();
    }

    public ContentBrowseMenu(@NotNull Class<? extends Content> content) {
        this(content, null);
    }

    public ContentBrowseMenu() {
        this(Content.class);
    }

    @Override
    protected void build(@NotNull Player player) {
        for (int i : PLACEHOLDERS) {
            this.place(i, PaperButton.builder(player).useItem(this::getPlaceholder).useAction((m, e) -> new Interactable.ClickResult.Cancelled()).build());
        }

        PaginationHelper.Placer<MenuView> placer = this.pagination.placer(player, SLOTS)
                .next(this.getNextArrow(), NEXT_SLOT)
                .previous(this.getPreviousArrow(), PREV_SLOT);
        placer.populate();

        this.place(BACK_SLOT, PaperButton.builder(player).useItem(this::getBackArrow)
                .useAction((m, e) -> {
                    if(this.previousMenu == null) return new Interactable.ClickResult.Cancelled();

                    this.previousMenu.open(player);

                    return new Interactable.ClickResult.Cancelled();
                }).build());

        if(this.repo == null) return;

        this.place(CREATE_SLOT, PaperButton.builder(player).useItem(this::getCreate)
                .useAction((m, e) -> {
                    if(!(m instanceof PaperMenu menu)) return new Interactable.ClickResult.Cancelled();

                    Conversation convo = new Conversation()
                            .addArgument("value", new StringArgument(Component.text("Please enter an ID for the content", NamedTextColor.GOLD)))
                            .setFinisher(c -> {
                                StringArgument arg = c.getArgument("value", StringArgument.class);
                                String val = arg.getValue();

                                if(val == null) return;

                                Content content = this.repo.createAndRegisterContent(val);
                                if(!(content instanceof MenuViewable viewable)) return;

                                viewable.getView().run(menu, player);
                            });

                    Modules.get(ConversationModule.class).startConversation(player, convo);

                    return new Interactable.ClickResult.Cancelled();
                }).build());
    }

    @Override
    public void open(@NotNull Player player) {
        this.pagination = this.craftPagination();
        super.open(player);
    }

    private @NotNull PaginationHelper<MenuView> craftPagination() {
        return new PaginationHelper<>(this, this::craftView)
                .paging(this.craftViews(this.clazz), SLOTS.length);
    }

    private @NotNull List<MenuView> craftViews(@NotNull Class<? extends Content> content) {
        List<ContentRepository<?>> repos = Modules.get(ContentModule.class).getChildren(content);
        List<MenuView> views = new CopyOnWriteArrayList<>(this.craftRepoViews(repos));

        ContentRepository<?> repo = Modules.get(ContentModule.class).getRepository(content);
        if(repo == null) return views;

        views.addAll(this.craftContentViews(repo));

        return views;
    }

    private ContentRepository<?> resolveRepository(@NotNull Class<? extends Content> content) {
        return Modules.get(ContentModule.class).getRepository(content);
    }

    private List<MenuView> craftRepoViews(List<ContentRepository<?>> repos) {
        return repos.stream().map(ContentRepository::getView).toList();
    }

    private List<MenuView> craftContentViews(ContentRepository<?> repo) {
        return repo.getContents().stream().map(content -> {
            if(!(content instanceof MenuViewable viewable)) return null;

            return viewable.getView();
        }).filter(Objects::nonNull).toList();
    }

    private Placeable craftView(@NotNull Player player, @NotNull MenuView view) {
        return PaperButton.builder(player).useItem(view::getItem)
                .useAction((m,e) -> {
                    if(!(m instanceof PaperMenu menu)) return new Interactable.ClickResult.Cancelled();

                    view.run(menu, player, e);
                    return new Interactable.ClickResult.Cancelled();
                }).build();
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

    private @NotNull ItemStack getCreate() {
        ItemStack create = ItemType.NETHER_STAR.createItemStack();

        create.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Create", NamedTextColor.GREEN));

        return create;
    }
}
