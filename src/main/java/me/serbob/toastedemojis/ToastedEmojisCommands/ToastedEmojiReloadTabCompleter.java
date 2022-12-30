package me.serbob.toastedemojis.ToastedEmojisCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToastedEmojiReloadTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("toastedemojis")) {
            List<String> options = new ArrayList<>();
            if(sender.hasPermission("toastedemojis.reload")) {
                options.add("reload");
            }
            options.add("list");
            return options;
        }
        return Collections.emptyList();
    }
}
