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
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidConfigurationException e) {
                        throw new RuntimeException(e);
                    }

                    // Get the normal-emojis and unnormal-emojis sections of the config
                    ConfigurationSection normalEmojisSection = plugin.getConfig().getConfigurationSection("normal-emojis");
                    ConfigurationSection unnormalEmojisSection = plugin.getConfig().getConfigurationSection("unnormal-emojis");

                    // Update the fields using the new config
                    plugin.setConfig(plugin.getConfig());
                    plugin.setNormalEmojisSection(normalEmojisSection);
                    plugin.setUnnormalEmojisSection(unnormalEmojisSection);

                    if(normalEmojisSection!=null) {
                        Map<String, String> normalEmojis = new HashMap<>();
                        for (Map.Entry<String, Object> entry : normalEmojisSection.getValues(false).entrySet()) {
                            normalEmojis.put(entry.getKey(), entry.getValue().toString());
                        }
                        plugin.setNormalEmojis(normalEmojis);
                    } else {
                        Map<String, String> normalEmojis = new HashMap<>();
                        plugin.setNormalEmojis(normalEmojis);
                    }

                    if(unnormalEmojisSection!=null) {
                        Map<String, String> unnormalEmojis = new HashMap<>();
                        for (Map.Entry<String, Object> entry : unnormalEmojisSection.getValues(false).entrySet()) {
                            unnormalEmojis.put(entry.getKey(), entry.getValue().toString());
                        }
                        plugin.setUnnormalEmojis(unnormalEmojis);
                    } else {
                        Map<String, String> unnormalEmojis = new HashMap<>();
                        plugin.setUnnormalEmojis(unnormalEmojis);
                    }

                    sender.sendMessage(ChatColor.GREEN + "ToastedEmojis config reloaded!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                }
                return true;
            } else if(args.length > 0 && args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(ChatColor.GOLD + "Current Emojis:");
                try {
                    for (Map.Entry<String, String> entry : plugin.getNormalEmojis().entrySet()) {
                        sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.WHITE + entry.getKey() + ChatColor.GOLD + " -> " + ChatColor.WHITE + entry.getValue());
                    }
                } catch (Exception e) {}
                try {
                    for (Map.Entry<String, String> entry : plugin.getUnnormalEmojis().entrySet()) {
                        sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.WHITE + ":" + entry.getKey() + ":" + ChatColor.GOLD + " -> " + ChatColor.WHITE + entry.getValue());
                    }
                } catch (Exception e) {}
            }
        }
        return false;
    }
}

