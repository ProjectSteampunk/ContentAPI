package me.instrumentalityi.contentapi.paper.content;

import me.instrumentalityi.contentapi.paper.content.grant.Grantable;
import me.instrumentalityi.contentapi.paper.content.menus.ContentBrowseMenu;
import me.instrumentalityi.steampunklib.common.modules.Modules;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Subcommand;

@Command("content")
public class ContentCommand {

    @CommandPlaceholder
    private void main(Player player) {
        // Open a content inventory
        new ContentBrowseMenu().open(player);
    }

    @Subcommand("grant")
    private void grant(Player player, String type, String id) {
        ContentRepository<?> repo = Modules.get(ContentModule.class).getRepository(type);
        if(repo == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Unable to find '<type>' repository", Placeholder.unparsed("type", type)));
            return;
        }

        Content content = repo.getContent(id);
        if(content == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Unable to find '<id>' in '<type>'", Placeholder.unparsed("id", id), Placeholder.unparsed("type", type)));
            return;
        }

        if(!(content instanceof Grantable grantable)) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Unable to grant '<id>' as it is not grant-able.", Placeholder.unparsed("id", id)));
            return;
        }

        player.sendMessage(grantable.grant(player).message());
    }

    @Subcommand("reload")
    private void reload(CommandSender sender) {
        Modules.get(ContentModule.class).getLoader().reload();
    }
}
