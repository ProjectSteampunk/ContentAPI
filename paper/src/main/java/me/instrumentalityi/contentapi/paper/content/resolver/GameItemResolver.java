package me.instrumentalityi.contentapi.paper.content.resolver;

import me.instrumentalityi.contentapi.common.ContentModule;
import me.instrumentalityi.contentapi.common.content.Content;
import me.instrumentalityi.contentapi.common.content.resolver.ContentResolver;
import me.instrumentalityi.contentapi.paper.content.ContentData;
import me.instrumentalityi.contentapi.paper.content.GameItem;
import me.instrumentalityi.contentapi.paper.utils.containers.impl.ItemContainer;
import me.instrumentalityi.steampunklib.common.modules.Modules;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GameItemResolver implements ContentResolver<ItemStack> {
    @Override
    public @Nullable Content<ItemStack> resolve(@NotNull Object object) {
        if (!(object instanceof ItemStack item)) return null;

        ContentData data = ItemContainer.of(item).read(new ContentData());

        String contentId = data.getContentId();
        String contentTypeId = data.getContentTypeId();

        if (contentId == null || contentTypeId == null) return null;

        return Modules.get(ContentModule.class).getContent(GameItem.class, contentTypeId, contentId);
    }
}
