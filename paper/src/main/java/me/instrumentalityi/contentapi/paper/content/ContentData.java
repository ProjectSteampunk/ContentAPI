package me.instrumentalityi.contentapi.paper.content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.instrumentalityi.contentapi.common.content.Content;
import me.instrumentalityi.steampunklib.paper.utils.containers.ContainerEditor;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@NoArgsConstructor
@Getter
public class ContentData implements ContainerEditor {

    private static final NamespacedKey ID_KEY = new NamespacedKey("contentapi", "content-id");
    private static final NamespacedKey TYPE_KEY = new NamespacedKey("contentapi", "content-type-id");

    private String contentId;
    private String contentTypeId;

    public ContentData(Content<?> content) {
        this.contentId = content.getId();
        this.contentTypeId = content.getType();
    }

    @Override
    public void read(PersistentDataContainer pdc) {
        this.contentId = pdc.get(ID_KEY, PersistentDataType.STRING);
        this.contentTypeId = pdc.get(TYPE_KEY, PersistentDataType.STRING);
    }

    @Override
    public void write(PersistentDataContainer pdc) {
        pdc.set(ID_KEY, PersistentDataType.STRING, this.contentId);
        pdc.set(TYPE_KEY, PersistentDataType.STRING, this.contentTypeId);
    }
}
