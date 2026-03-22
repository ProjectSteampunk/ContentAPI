package me.instrumentalityi.contentapi.paper.content.grant;

import org.bukkit.entity.Player;

public interface Grantable {

    Result grant(Player player);

    interface Result {
        record Granted() implements Result {};
    }
}
