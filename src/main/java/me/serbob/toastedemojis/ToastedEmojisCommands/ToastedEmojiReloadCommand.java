package me.serbob.toastedemojis.ToastedEmojisCommands;

import me.serbob.toastedemojis.ToastedEmojis;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
                    this.plugin.reloadConfig();
                    try {
                        this.plugin.getConfig().load(this.plugin.getConfigFile());
                    } catch (IOException | InvalidConfigurationException e) {
                        throw new RuntimeException(e);
                    }
                    ConfigurationSection normalEmojisSection = this.plugin.getConfig().getConfigurationSection("normal-emojis");
                    ConfigurationSection unnormalEmojisSection = this.plugin.getConfig().getConfigurationSection("unnormal-emojis");

                    this.plugin.setConfig(this.plugin.getConfig());
                    this.plugin.setNormalEmojisSection(normalEmojisSection);
                    this.plugin.setUnnormalEmojisSection(unnormalEmojisSection);

                    if (normalEmojisSection != null) {
                        Map<String, Map<String, String>> normalEmojis = new HashMap<>();
                        for (String category : normalEmojisSection.getKeys(false)) {
                            ConfigurationSection categorySection = normalEmojisSection.getConfigurationSection(category);
                            if (categorySection != null) {
                                Map<String, String> emojiMap = new HashMap<>();
                                for (String emoji : categorySection.getKeys(false)) {
                                    String value = categorySection.getString(emoji);
                                    emojiMap.put(emoji, value);
                                }
                                normalEmojis.put(category, emojiMap);
                            }
                        }
                        this.plugin.setNormalEmojis(normalEmojis);
                    } else {
                        Map<String, Map<String, String>> normalEmojis = new HashMap<>();
                        this.plugin.setNormalEmojis(normalEmojis);
                    }

                    if (unnormalEmojisSection != null) {
                        Map<String, Map<String, String>> unnormalEmojis = new HashMap<>();
                        for (String category : unnormalEmojisSection.getKeys(false)) {
                            ConfigurationSection categorySection = unnormalEmojisSection.getConfigurationSection(category);
                            if (categorySection != null) {
                                Map<String, String> emojiMap = new HashMap<>();
                                for (String emoji : categorySection.getKeys(false)) {
                                    String value = categorySection.getString(emoji);
                                    emojiMap.put(emoji, value);
                                }
                                unnormalEmojis.put(category, emojiMap);
                            }
                        }
                        this.plugin.setUnnormalEmojis(unnormalEmojis);
                    } else {
                        Map<String, Map<String, String>> unnormalEmojis = new HashMap<>();
                        this.plugin.setUnnormalEmojis(unnormalEmojis);
                    }
                    sender.sendMessage(ChatColor.GREEN + "ToastedEmojis config reloaded!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                }
                return true;
            } else if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission("toastedemojis.list")) {
                    sender.sendMessage(ChatColor.GOLD + "Current Emojis:");
                    Map<String, Map<String, String>> normalEmojis = this.plugin.getNormalEmojis();
                    Map<String, Map<String, String>> unnormalEmojis = this.plugin.getUnnormalEmojis();

                    if (!normalEmojis.isEmpty()) {
                        for (Map.Entry<String, Map<String, String>> categoryEntry : normalEmojis.entrySet()) {
                            String category = categoryEntry.getKey();
                            Map<String, String> emojiMap = categoryEntry.getValue();
                            for (Map.Entry<String, String> emojiEntry : emojiMap.entrySet()) {
                                String emoji = emojiEntry.getKey();
                                String symbol = ChatColor.translateAlternateColorCodes('&', emojiEntry.getValue());
                                sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.WHITE + category + ChatColor.GOLD + " -> " + ChatColor.WHITE + emoji + ChatColor.GOLD + " -> " + ChatColor.WHITE + symbol);
                            }
                        }
                    }
                    if (!unnormalEmojis.isEmpty()) {
                        for (Map.Entry<String, Map<String, String>> categoryEntry : unnormalEmojis.entrySet()) {
                            String category = categoryEntry.getKey();
                            Map<String, String> emojiMap = categoryEntry.getValue();
                            for (Map.Entry<String, String> emojiEntry : emojiMap.entrySet()) {
                                String emoji = emojiEntry.getKey();
                                String symbol = ChatColor.translateAlternateColorCodes('&', emojiEntry.getValue());
                                sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.WHITE+ category + ChatColor.GOLD + " -> " + ChatColor.WHITE + ":" + emoji + ":" + ChatColor.GOLD + " -> " + ChatColor.WHITE + symbol);
                            }
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                }
            }
        }
        return false;
    }
}

