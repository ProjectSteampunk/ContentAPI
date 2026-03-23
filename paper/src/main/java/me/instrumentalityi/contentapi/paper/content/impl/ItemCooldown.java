package me.instrumentalityi.contentapi.paper.content.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.instrumentalityi.steampunklib.paper.utils.containers.ContainerEditor;
import me.instrumentalityi.steampunklib.paper.utils.containers.impl.ItemContainer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Getter
public class ItemCooldown {

    private final long cooldown;

    public ItemCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public boolean canUse(@NotNull ItemStack stack) {
        ItemContainer container = ItemContainer.of(stack);
        Data data = container.read(new Data());

        return !this.isOnCooldown(data);
    }

    public void useNow(@NotNull ItemStack stack) {
        this.updateLastUsed(stack, Instant.now().toEpochMilli());
    }

    public void updateLastUsed(@NotNull ItemStack stack, long lastUsed) {
        ItemContainer container = ItemContainer.of(stack);
        container.write(new Data(lastUsed));
    }

    private boolean isOnCooldown(Data data) {
        if(data.lastUsedAt == null || data.lastUsedAt <= 0) return false;

        Instant then = Instant.ofEpochMilli(data.lastUsedAt);
        Instant now = Instant.now();

        return then.plusMillis(cooldown).isAfter(now);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private class Data implements ContainerEditor {
        private static final NamespacedKey LAST_USED_KEY = new NamespacedKey("contentapi", "last_used");

        @Getter
        private Long lastUsedAt = 0L;

        @Override
        public void read(PersistentDataContainer pdc) {
            this.lastUsedAt = pdc.get(LAST_USED_KEY, PersistentDataType.LONG);
        }

        @Override
        public void write(PersistentDataContainer pdc) {
            pdc.set(LAST_USED_KEY, PersistentDataType.LONG, this.lastUsedAt);
        }
    }
}
