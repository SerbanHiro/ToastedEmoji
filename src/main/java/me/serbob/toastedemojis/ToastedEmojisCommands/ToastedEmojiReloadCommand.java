package me.serbob.toastedemojis.ToastedEmojisCommands;

import me.serbob.toastedemojis.ToastedEmojis;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ToastedEmojiReloadCommand implements CommandExecutor {
    private final ToastedEmojis plugin;

    public ToastedEmojiReloadCommand(ToastedEmojis plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("toastedemojis")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("toastedemojis.reload")) {
                    // Reload the config
                    plugin.reloadConfig();
                    try {
                        plugin.getConfig().load(plugin.getConfigFile());
                    } catch (IOException | InvalidConfigurationException e) {
                        throw new RuntimeException(e);
                    }

                    // Get the normal-emojis and unnormal-emojis sections of the config
                    ConfigurationSection normalEmojisSection = plugin.getConfig().getConfigurationSection("normal-emojis");
                    ConfigurationSection unnormalEmojisSection = plugin.getConfig().getConfigurationSection("unnormal-emojis");

                    // Update the fields using the new config
                    plugin.setConfig(plugin.getConfig());
                    plugin.setNormalEmojisSection(normalEmojisSection);
                    plugin.setUnnormalEmojisSection(unnormalEmojisSection);

                    Map<String, String> normalEmojis = new HashMap<>();
                    if(normalEmojisSection!=null) {
                        plugin.setNormalEmojis(normalEmojisSection.getValues(false).entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString())));
                    }
                    plugin.setNormalEmojis(normalEmojis);

                    Map<String, String> unnormalEmojis = new HashMap<>();
                    if(unnormalEmojisSection!=null) {
                        plugin.setUnnormalEmojis(unnormalEmojisSection.getValues(false).entrySet().stream()
                                .collect(Collectors.toMap(e -> ":" + e.getKey() + ":", e -> e.getValue().toString())));
                    }
                    plugin.setUnnormalEmojis(unnormalEmojis);

                    sender.sendMessage(ChatColor.GREEN + "ToastedEmojis config reloaded!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                }
                return true;
            } else if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(ChatColor.GOLD + "Current Emojis:");

                Map<String, String> normalEmojis = plugin.getNormalEmojis();
                Map<String, String> unnormalEmojis = plugin.getUnnormalEmojis();

                // Iterate over the normal emojis map and send the messages to the sender
                for (Map.Entry<String, String> entry : normalEmojis.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.WHITE + key + ChatColor.GOLD + " -> " + ChatColor.WHITE + value);
                }

                // Iterate over the unnormal emojis map and send the messages to the sender
                for (Map.Entry<String, String> entry : unnormalEmojis.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.WHITE + ":" + key + ":" + ChatColor.GOLD + " -> " + ChatColor.WHITE + value);
                }
            }
        }
        return false;
    }
}

