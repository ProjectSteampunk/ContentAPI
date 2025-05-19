package me.instrumentalityi.contentapi.paper.content;

import lombok.Getter;
import me.instrumentalityi.contentapi.common.content.Content;
import me.instrumentalityi.contentapi.common.content.builder.ContentBuilder;
import me.instrumentalityi.contentapi.paper.ContentAPIPlugin;
import me.instrumentalityi.contentapi.paper.content.builder.ItemBuilder;
import me.instrumentalityi.contentapi.paper.content.item.ItemAttribute;
import me.instrumentalityi.contentapi.paper.content.item.impl.LoreAttribute;
import me.instrumentalityi.contentapi.paper.content.item.impl.MaterialAttribute;
import me.instrumentalityi.contentapi.paper.content.item.impl.NameAttribute;
import me.instrumentalityi.contentapi.paper.utils.BukkitConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class GameItem implements Content<ItemStack> {

    private final String id;
    private final String type;

    private final Map<Class<? extends ItemAttribute>, ItemAttribute> attributes;

    public GameItem(Map<String, Object> config) {
        this.id = (String) config.get("id");
        this.type = (String) config.get("type");
        this.attributes = new ConcurrentHashMap<>();

        this.setAttribute(new MaterialAttribute(BukkitConfigUtil.getMaterial(config, "material")));
        this.setAttribute(new NameAttribute(BukkitConfigUtil.getComponent(config, "title")));
        this.setAttribute(new LoreAttribute(BukkitConfigUtil.getComponentList(config, "lore")));

        ContentAPIPlugin.getInstance().getLogger().info("Loaded GameItem: " + id);
    }

    public GameItem(ConfigurationSection config) {
        this(config.getValues(true));
    }

    public void setAttribute(ItemAttribute attribute) {
        this.attributes.put(attribute.getClass(), attribute);
    }

    public <T extends ItemAttribute> T getAttribute(Class<T> clazz) {
        return clazz.cast(this.attributes.get(clazz));
    }

    public ItemAttribute getAttribute(String id) {
        for (ItemAttribute attribute : this.attributes.values()) {
            if (!attribute.getId().equalsIgnoreCase(id)) continue;

            return attribute;
        }

        return null;
    }

    public List<ItemAttribute> getAttributes() {
        return new ArrayList<>(this.attributes.values());
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public @NotNull ContentBuilder<ItemStack> builder() {
        return new ItemBuilder(this);
    }
}
