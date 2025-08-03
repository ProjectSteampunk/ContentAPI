package me.instrumentalityi.contentapi.paper.content;

import me.instrumentalityi.contentapi.common.ContentModule;
import me.instrumentalityi.steampunklib.common.modules.Modules;
import me.instrumentalityi.steampunklib.paper.utils.PaperStringUtil;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("content")
@CommandPermission("api.content.command")
public class ContentCommand {

    @CommandPlaceholder
    private void main(Player player, String type, String id) {
        GameItem item = Modules.get(ContentModule.class).getContent(GameItem.class, type, id);

        if(item == null) {
            player.sendMessage(PaperStringUtil.colorize("&cCould not find content with id: {0}", id));
            return;
        }

        item.builder().build().thenAccept(itemStack -> {
            if(itemStack != null) player.give(itemStack);
        });
    }
}
