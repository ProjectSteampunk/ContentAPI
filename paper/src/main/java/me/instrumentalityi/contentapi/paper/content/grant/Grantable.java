package me.instrumentalityi.contentapi.paper.content.grant;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface Grantable {

    Result grant(Player player);

    interface Result {
        Component message();
    }
}
