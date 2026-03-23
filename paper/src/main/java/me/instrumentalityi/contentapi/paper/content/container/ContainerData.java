package me.instrumentalityi.contentapi.paper.content.container;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.instrumentalityi.contentapi.paper.content.Content;
import me.instrumentalityi.contentapi.paper.content.ContentModule;
import me.instrumentalityi.contentapi.paper.content.ContentRepository;
import me.instrumentalityi.steampunklib.common.modules.Modules;
import me.instrumentalityi.steampunklib.paper.utils.containers.ContainerEditor;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ContainerData implements ContainerEditor {

    private static final NamespacedKey ID_KEY = new NamespacedKey("contentapi", "id");
    private static final NamespacedKey REPO_KEY = new NamespacedKey("contentapi", "repo");

    private @Nullable ContentRepository<?> repo;
    private @Nullable Content content;

    @Override
    public void read(PersistentDataContainer pdc) {
        String repoId = pdc.get(REPO_KEY, PersistentDataType.STRING);
        String contentId = pdc.get(ID_KEY, PersistentDataType.STRING);

        if (repoId == null || contentId == null) {
            return;
        }

        this.repo = Modules.get(ContentModule.class).getRepository(repoId);

        if (this.repo == null) return;

        this.content = this.repo.getContent(contentId);
    }

    @Override
    public void write(PersistentDataContainer pdc) {
        if (this.repo == null || this.content == null) return;

        pdc.set(ID_KEY, PersistentDataType.STRING, this.content.getId());
        pdc.set(REPO_KEY, PersistentDataType.STRING, this.repo.getTag());
    }

    public boolean isValid() {
        return this.repo != null && this.content != null;
    }
}
