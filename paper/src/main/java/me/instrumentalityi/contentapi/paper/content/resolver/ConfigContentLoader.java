package me.instrumentalityi.contentapi.paper.content.resolver;

import me.instrumentalityi.contentapi.common.ContentModule;
import me.instrumentalityi.contentapi.common.content.Content;
import me.instrumentalityi.contentapi.common.content.ContentType;
import me.instrumentalityi.contentapi.common.content.resolver.ContentLoader;
import me.instrumentalityi.steampunklib.common.modules.Modules;
import me.instrumentalityi.steampunklib.paper.utils.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

public class ConfigContentLoader implements ContentLoader {

    private static final FileFilter YML_FILTER = (file) -> file.getName().endsWith(".yml");

    private final JavaPlugin plugin;

    public ConfigContentLoader(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        File[] files = this.getFiles(new File(plugin.getDataFolder(), "content"));

        for (File file : files) {
            Configuration config = new Configuration(plugin, file);

            this.loadConfig(config);
        }
    }

    private File[] getFiles(File dir) {
        if (!dir.exists()) {
            dir.mkdir();
        }

        return dir.listFiles(YML_FILTER);
    }

    private void loadConfig(Configuration config) {
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if(section == null) continue;

            this.loadContent(section);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadContent(ConfigurationSection config) {
        ContentModule module = Modules.get(ContentModule.class);

        String typeId = config.getString("type");
        if (typeId == null) return;

        ContentType<?> type = module.getContentType(typeId);

        if (type == null) return;

        Map<String, Object> properties = config.getValues(true);
        properties.put("id", config.getName());

        Content<?> content = type.mapper().apply(properties);

        Class<? extends Content<?>> clazz = (Class<? extends Content<?>>) content.getClass();
        module.registerContent(clazz, content);
    }
}
