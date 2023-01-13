package me.serbob.toastedemojis;

import me.serbob.toastedemojis.ToastedEmojisCommands.ToastedEmojiReloadCommand;
import me.serbob.toastedemojis.ToastedEmojisCommands.ToastedEmojiReloadTabCompleter;
import me.serbob.toastedemojis.Utils.ToastedUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class ToastedEmojis extends JavaPlugin implements Listener {
    private File configFile;
    private YamlConfiguration config;

    // Get the normal-emojis and unnormal-emojis sections of the config
    private ConfigurationSection normalEmojisSection;
    private ConfigurationSection unnormalEmojisSection;

    // Get a map of all the normal and unnormal emojis and their replacements
    private Map<String, String> normalEmojis;
    private Map<String, String> unnormalEmojis;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        normalEmojisSection = config.getConfigurationSection("normal-emojis");
        unnormalEmojisSection = config.getConfigurationSection("unnormal-emojis");

        // Initialize the emojis maps
        if(normalEmojisSection!=null) {
            normalEmojis = normalEmojisSection.getValues(false).entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
        } else {
            normalEmojis = new HashMap<>();
        }
        if(unnormalEmojisSection!=null) {
            unnormalEmojis = unnormalEmojisSection.getValues(false).entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
        } else {
            unnormalEmojis = new HashMap<>();
        }
        // Register the event listener
        getServer().getPluginManager().registerEvents(this, this);

        getCommand("toastedemojis").setExecutor(new ToastedEmojiReloadCommand(this));
        getCommand("toastedemojis").setTabCompleter(new ToastedEmojiReloadTabCompleter());
    }

    // Handle the AsyncPlayerChatEvent event
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String originalMessage = event.getMessage();
        String replacedMessage = originalMessage;

        for (Map.Entry<String, String> entry : this.normalEmojis.entrySet()) {
            String symbol = ChatColor.translateAlternateColorCodes('&', entry.getValue());
            replacedMessage = replacedMessage.replace(entry.getKey(), symbol);
        }
        for (Map.Entry<String, String> entry : this.unnormalEmojis.entrySet()) {
            String symbol = ChatColor.translateAlternateColorCodes('&', entry.getValue());
            String pattern = ":" + entry.getKey() + ":";
            replacedMessage = replacedMessage.replace(pattern, symbol);
        }
        event.setMessage(replacedMessage);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }

    public ConfigurationSection getNormalEmojisSection() {
        return normalEmojisSection;
    }

    public void setNormalEmojisSection(ConfigurationSection normalEmojisSection) {
        this.normalEmojisSection = normalEmojisSection;
    }

    public ConfigurationSection getUnnormalEmojisSection() {
        return unnormalEmojisSection;
    }

    public void setUnnormalEmojisSection(ConfigurationSection unnormalEmojisSection) {
        this.unnormalEmojisSection = unnormalEmojisSection;
    }

    public Map<String, String> getNormalEmojis() {
        return normalEmojis;
    }

    public void setNormalEmojis(Map<String, String> normalEmojis) {
        this.normalEmojis = normalEmojis;
    }

    public Map<String, String> getUnnormalEmojis() {
        return unnormalEmojis;
    }

    public void setUnnormalEmojis(Map<String, String> unnormalEmojis) {
        this.unnormalEmojis = unnormalEmojis;
    }
}
