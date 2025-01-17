package ru.oshifugo.functionalclans;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class utility {
//    public static String ColorTratslate(String msg) {
//        msg = ChatColor.translateAlternateColorCodes('&', msg);
//        return msg;
//    }

    public static String config(String cfg) {
        cfg = Main.instance.getConfig().getString(cfg);
        return cfg;
    }

    public static boolean configBoolean(String cfg) {
        boolean bool;
        bool = Main.instance.getConfig().getBoolean(cfg);
        return bool;
    }

    public static String[] configList(String cfg) {
        List<String> myArray =  Main.instance.getConfig().getStringList(cfg);
        String[] response = myArray.toArray(new String[myArray.size()]);
        int i = 0;
        for (String element : myArray) {
            response[i] = element;
            i++;
        }
        return response;
    }
//    public static String lang(String cfg) {
//        if (Files.exists(Paths.get(Main.instance.getDataFolder() + "/message.yml"))) {
//            File langYml = new File(Main.instance.getDataFolder()+"/message.yml");
//            FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langYml);
//            if (langConfig.getString(utility.config("lang") + "." + cfg) == null) {
//                cfg = " There was an error in message.yml. The required key could not be found. Recheck the values.";
//            } else cfg = langConfig.getString(utility.config("lang") + "."  + cfg);
//        } else cfg = " Could not find message.yml file";
//        return cfg;
//    }
    public static String lang(CommandSender sender, String cfg) {
        if (Files.exists(Paths.get(Main.instance.getDataFolder() + "/message.yml"))) {
            File langYml = new File(Main.instance.getDataFolder()+"/message.yml");
            FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langYml);
            if (langConfig.getString(utility.config("lang") + "." + cfg) == null) {
                cfg = " There was an error in message.yml. The required key could not be found. Recheck the values.";
            } else cfg = langConfig.getString(utility.config("lang") + "."  + cfg);
        } else cfg = " Could not find message.yml file";
        if (!(sender instanceof Player)) {
            return cfg;
        }
        Player player = (Player) sender;
        return PlaceholderAPI.setPlaceholders(player, cfg);
    }

//    public static String quest(String cfg) {
//        if (Files.exists(Paths.get(Main.instance.getDataFolder() + "/quest.yml"))) {
//            File langYml = new File(Main.instance.getDataFolder()+"/quest.yml");
//            FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langYml);
//            cfg = langConfig.getString(cfg);
//        } else cfg = " Could not find quest.yml file";
//        return cfg;
//    }
    public static void info(Object text) {
        Bukkit.getConsoleSender().sendMessage(hex("[" + Main.instance.getName() + "] " + text));
    }
    public static void warning(Object text) {
        Bukkit.getConsoleSender().sendMessage(utility.hex("&6[" + Main.instance.getName() + "] [warning]" + text));
    }
    public static void error(Object text) {
        Bukkit.getConsoleSender().sendMessage(hex("&4[" + Main.instance.getName() + "] [ERROR] " + text));
    }
    public static void debug(Object text) {
        if (Main.instance.getConfig().getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage(hex("&e[" + Main.instance.getName() + "] [Debug] " + text));
        }
    }
    public static String hex(String msg) {
        String version = Bukkit.getServer().getBukkitVersion();
        if (version.startsWith("1.15") || version.startsWith("1.14") || version.startsWith("1.13") || version.startsWith("1.12") || version.startsWith("1.11") || version.startsWith("1.10") || version.startsWith("1.9") || version.startsWith("1.8")) {
            msg = ChatColor.translateAlternateColorCodes('&', msg);
            return msg;
        } else {
            msg = ChatColor.translateAlternateColorCodes('&', msg);
            Matcher matcher = Pattern.compile("<#[A-Fa-f0-9]{6}>").matcher(msg);
            int hexAmount;
            for(hexAmount = 0; matcher.find(); ++hexAmount) {
                matcher.region(matcher.end() - 1, msg.length());
            }
            int startIndex = 0;
            for(int hexIndex = 0; hexIndex < hexAmount; ++hexIndex) {
                int msgIndex = msg.indexOf("<#", startIndex);
                String hex = msg.substring(msgIndex + 1, msgIndex + 8);
                startIndex = msgIndex + 2;
                msg = msg.replace("<" + hex + ">", net.md_5.bungee.api.ChatColor.of(hex) + "");
            }
            return msg;
        }
    }

    public static TextComponent page_msg(CommandSender sender, String[] args, int page, int max_line, int count) {
        TextComponent msg = new TextComponent("");
        if (count == 0) {
            msg.addExtra(utility.hex(utility.lang(sender, "pages.no_values")));
            return msg;
        }
        int end_count = page * max_line;
        int start_count = end_count - max_line;
        int page_max = (int) Math.ceil((double) count / max_line);
        if (count < start_count) {
            msg.addExtra(utility.hex(utility.lang(sender, "pages.no_page")));
            return msg;
        }
        TextComponent back = new TextComponent(), next = new TextComponent();
        String cmd = "";
        if (args[0].equalsIgnoreCase("list")) {
            cmd = "/clan list %s";
        }
        if (start_count - 1 > 0) {
            back.addExtra(utility.hex(utility.lang(sender, "pages.back")));
            back.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(cmd, String.valueOf(page - 1))));
        }
        if (end_count <= count) {
            next.addExtra(utility.hex(utility.lang(sender, "pages.next")));
            next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(cmd, String.valueOf(page + 1))));
        }
        msg.addExtra(back);
        msg.addExtra(utility.hex(String.format(utility.lang(sender,"pages.page"), page, page_max)));
        msg.addExtra(next);
        return msg;
    }
}
