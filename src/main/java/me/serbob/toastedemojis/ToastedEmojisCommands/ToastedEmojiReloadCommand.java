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
                        Map<String, String> normalEmojis = new HashMap<>();
                        for (Map.Entry<String, Object> entry : normalEmojisSection.getValues(false).entrySet())
                            normalEmojis.put(entry.getKey(), entry.getValue().toString());
                        this.plugin.setNormalEmojis(normalEmojis);
                    } else {
                        Map<String, String> normalEmojis = new HashMap<>();
                        this.plugin.setNormalEmojis(normalEmojis);
                    }

                    if (unnormalEmojisSection != null) {
                        Map<String, String> unnormalEmojis = new HashMap<>();
                        for (Map.Entry<String, Object> entry : unnormalEmojisSection.getValues(false).entrySet())
                            unnormalEmojis.put(entry.getKey(), entry.getValue().toString());
                        this.plugin.setUnnormalEmojis(unnormalEmojis);
                    } else {
                        Map<String, String> unnormalEmojis = new HashMap<>();
                        this.plugin.setUnnormalEmojis(unnormalEmojis);
                    }
                    sender.sendMessage(ChatColor.GREEN + "ToastedEmojis config reloaded!");
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                }
                return true;
            } else if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(ChatColor.GOLD + "Current Emojis:");
                Map<String, String> normalEmojis = this.plugin.getNormalEmojis();
                Map<String, String> unnormalEmojis = this.plugin.getUnnormalEmojis();

                if (!normalEmojis.isEmpty()) {
                    for (Map.Entry<String, String> entry : normalEmojis.entrySet()) {
                        sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.WHITE + entry.getKey() + ChatColor.GOLD + " -> " + ChatColor.WHITE + entry.getValue());
                    }
                }
                if (!unnormalEmojis.isEmpty()) {
                    for (Map.Entry<String, String> entry : unnormalEmojis.entrySet()) {
                        sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.WHITE + ":" + entry.getKey() + ":" + ChatColor.GOLD + " -> " + ChatColor.WHITE + entry.getValue());
                    }
                }
            }
        }
        return false;
    }
}

