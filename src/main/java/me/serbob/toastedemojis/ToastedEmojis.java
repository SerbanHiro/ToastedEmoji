package me.serbob.toastedemojis;

import me.serbob.toastedemojis.ToastedEmojisCommands.ToastedEmojiReloadCommand;
import me.serbob.toastedemojis.ToastedEmojisCommands.ToastedEmojiReloadTabCompleter;
import me.serbob.toastedemojis.Utils.ToastedUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permission;
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
    private Map<String, Map<String,String>> normalEmojis = new HashMap<>();
    private Map<String, Map<String,String>> unnormalEmojis = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        normalEmojisSection = config.getConfigurationSection("normal-emojis");
        unnormalEmojisSection = config.getConfigurationSection("unnormal-emojis");

        // Initialize the emojis maps
           //normalEmojis = normalEmojisSection.getValues(false).entrySet().stream()
             //       .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
        ConfigurationSection emojisSection = config.getConfigurationSection("normal-emojis");
        for (String category : emojisSection.getKeys(false)) {
            String permissionName = "toastedemojis.emoji."+category;
            if (getServer().getPluginManager().getPermission(permissionName) == null) {
                getServer().getPluginManager().addPermission(new Permission(permissionName));
            }
            ConfigurationSection categorySection = emojisSection.getConfigurationSection(category);
            if (categorySection != null) {
                Map<String, String> emojiMap = new HashMap<>();
                for (String emoji : categorySection.getKeys(false)) {
                    String value = categorySection.getString(emoji);
                    emojiMap.put(emoji, value);
                }
                normalEmojis.put(category, emojiMap);
            }
        }
        emojisSection = config.getConfigurationSection("unnormal-emojis");
        for (String category : emojisSection.getKeys(false)) {
            String permissionName = "toastedemojis.emoji."+category;
            if (getServer().getPluginManager().getPermission(permissionName) == null) {
                getServer().getPluginManager().addPermission(new Permission(permissionName));
            }
            ConfigurationSection categorySection = emojisSection.getConfigurationSection(category);
            if (categorySection != null) {
                Map<String, String> emojiMap = new HashMap<>();
                for (String emoji : categorySection.getKeys(false)) {
                    String value = categorySection.getString(emoji);
                    emojiMap.put(emoji, value);
                }
                unnormalEmojis.put(category, emojiMap);
            }
        }
        // Register the event listener
        getServer().getPluginManager().registerEvents(this, this);

        getCommand("toastedemojis").setExecutor(new ToastedEmojiReloadCommand(this));
        getCommand("toastedemojis").setTabCompleter(new ToastedEmojiReloadTabCompleter());
    }

    // Handle the AsyncPlayerChatEvent event
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String originalMessage = event.getMessage();
        String replacedMessage = originalMessage;

        // Iterate over the outer map
        for (Map.Entry<String, Map<String, String>> entry : normalEmojis.entrySet()) {
            String category = entry.getKey();
            String permissionName = "toastedemojis.emoji."+category;
            if(player.hasPermission(permissionName)) {
                Map<String, String> emojiMap = entry.getValue();
                for (Map.Entry<String, String> emojiEntry : emojiMap.entrySet()) {
                    String emoji = emojiEntry.getKey();
                    String value = emojiEntry.getValue();
                    String symbol = ChatColor.translateAlternateColorCodes('&', value);
                    if(getConfig().getBoolean("replace_color"))
                        replacedMessage = replacedMessage.replace(emoji, symbol);
                    else replacedMessage = replacedMessage.replace(emoji, value);
                }
            }
        }
        for (Map.Entry<String, Map<String, String>> entry : unnormalEmojis.entrySet()) {
            String category = entry.getKey();
            String permissionName = "toastedemojis.emoji."+category;
            if(player.hasPermission(permissionName)) {
                Map<String, String> emojiMap = entry.getValue();
                for (Map.Entry<String, String> emojiEntry : emojiMap.entrySet()) {
                    String emoji = emojiEntry.getKey();
                    String value = emojiEntry.getValue();
                    String symbol = ChatColor.translateAlternateColorCodes('&', value);
                    String pattern = ":" + emoji + ":";
                    if(getConfig().getBoolean("replace_color"))
                        replacedMessage = replacedMessage.replace(pattern, symbol);
                    else replacedMessage = replacedMessage.replace(pattern, value);
                }
            }
        }
        /**for (Map.Entry<String, Map<String,String>> entry : this.normalEmojis.entrySet()) {
            String emojiPerm = entry.getKey();
            player.sendMessage(emojiPerm);
            for(String key:config.getConfigurationSection("normal-emojis."+emojiPerm).getKeys(true)) {
                player.sendMessage(key);
            }*/
            //String symbol = ChatColor.translateAlternateColorCodes('&', entry.getValue());
            /**if(getConfig().getBoolean("replace_color"))
                replacedMessage = replacedMessage.replace(entry.getKey(), symbol);
            else replacedMessage = replacedMessage.replace(entry.getKey(), entry.getValue());*/
        /**player.sendMessage("-");
        for (Map.Entry<String, String> entry : this.unnormalEmojis.entrySet()) {
            String emojiPerm = entry.getValue();
            for(String key:config.getConfigurationSection("unnormal-emojis."+emojiPerm).getKeys(true)) {
                player.sendMessage(key);
            }
            String pattern = ":" + entry.getKey() + ":";
            if(getConfig().getBoolean("replace_color"))
                replacedMessage = replacedMessage.replace(pattern, symbol);
            else replacedMessage = replacedMessage.replace(pattern, entry.getValue());*/
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

    public Map<String, Map<String,String>> getNormalEmojis() {
        return normalEmojis;
    }

    public void setNormalEmojis(Map<String, Map<String,String>> normalEmojis) {
        this.normalEmojis = normalEmojis;
    }

    public Map<String, Map<String,String>> getUnnormalEmojis() {
        return unnormalEmojis;
    }

    public void setUnnormalEmojis(Map<String, Map<String,String>> unnormalEmojis) {
        this.unnormalEmojis = unnormalEmojis;
    }
}
