package me.instrumentalityi.contentapi.paper.content.impl;

import io.papermc.paper.datacomponent.item.ItemLore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.instrumentalityi.contentapi.paper.content.ContentRepository;
import me.instrumentalityi.steampunklib.paper.utils.PaperStringUtil;
import me.instrumentalityi.steampunklib.paper.utils.containers.ContainerEditor;
import me.instrumentalityi.steampunklib.paper.utils.containers.impl.ItemContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class Consumable extends Item {

    public static final @NotNull String ID = "consumable";

    // DEFAULTS
    private static final ItemCooldown DEFAULT_COOLDOWN = new ItemCooldown(500);
    private static final int DEFAULT_USES = 1;

    // COMPONENTS
    private @NotNull ItemCooldown cooldown = DEFAULT_COOLDOWN;
    private int uses = DEFAULT_USES;

    public Consumable(@NotNull ContentRepository<Consumable> repo, @NotNull String id) {
        super(repo, id);
    }

    @Override
    public void write(@NotNull ConfigurationSection config) {
        super.write(config);
        config.set("cooldown", cooldown.getCooldown());
        config.set("uses", uses);
    }

    @Override
    public void read(@NotNull ConfigurationSection config) {
        super.read(config);
        this.cooldown = new ItemCooldown(config.getLong("cooldown", DEFAULT_COOLDOWN.getCooldown()));
        this.uses = config.getInt("uses", DEFAULT_USES);
    }

    @Override
    protected ItemStack shapeItem(@NotNull ItemStack item) {
        this.ensureUsesApplied(item);
        return super.shapeItem(item);
    }

    @Override
    protected @NotNull ItemLore craftLore(@NotNull ItemStack item) {
        int uses = this.getRemainingUses(item);

        return ItemLore.lore()
                .addLine(Component.text("Item").color(NamedTextColor.GRAY))
                .addLine(Component.empty())
                .addLine(MiniMessage.miniMessage().deserialize("<dark_gray>Uses <gray><current><dark_gray>/<gray><max>",
                        Placeholder.unparsed("current", String.valueOf(uses)),
                        Placeholder.unparsed("max", String.valueOf(this.uses))))
                .addLine(Component.empty())
                .addLines(PaperStringUtil.wrap(this.description, MAX_DESCRIPTION_LENGTH,
                        line -> Component.text(line, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)))
                .build();
    }

    protected void ensureUsesApplied(@NotNull ItemStack item) {
        if(item.getPersistentDataContainer().has(Data.USES_KEY)) return;

        ItemContainer container = new ItemContainer(item);
        container.write(new Data(this.uses));
    }

    private int getRemainingUses(@NotNull ItemStack item) {
        return ItemContainer.of(item).read(new Data()).uses;
    }

    private void consumeUse(@NotNull ItemStack item) {
        ItemContainer container = new ItemContainer(item);
        Data data = container.read(new Data());

        data.uses = data.uses - 1;

        container.write(data);
    }

    @Override
    public void interact(@NotNull PlayerInteractEvent args) {
        Player player = args.getPlayer();
        ItemStack item = args.getItem();

        if(item == null) return;

        if(!this.cooldown.canUse(item)) {
            player.sendMessage(Component.text("You are on cooldown!", NamedTextColor.RED));
            return;
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1f, 1f);

        // Remove a use
        int uses = this.getRemainingUses(item);
        if(uses > 1) {
            this.consumeUse(item);
            this.cooldown.useNow(item);
            this.shapeItem(item);
            return;
        }

        player.getInventory().remove(item);
    }

    // MOVE TO A FOOD CLASS EVENTUALLY
    private void feed(Player player, int food, float saturationModifier) {
        int newFood = Math.min(player.getFoodLevel() + food, 20);
        player.setFoodLevel(newFood);

        float saturationToAdd = food * saturationModifier * 2;
        float newSaturation = Math.min(player.getSaturation() + saturationToAdd, newFood);

        player.setSaturation(newSaturation);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class Data implements ContainerEditor {
        public static final @NotNull NamespacedKey USES_KEY = new NamespacedKey("contentapi", "uses");

        private Integer uses;

        public Data(int uses) {
            this.uses = uses;
        }

        @Override
        public void read(PersistentDataContainer pdc) {
            this.uses = pdc.get(USES_KEY, PersistentDataType.INTEGER);
        }

        @Override
        public void write(PersistentDataContainer pdc) {
            pdc.set(USES_KEY, PersistentDataType.INTEGER, this.uses);
        }
    }
}
