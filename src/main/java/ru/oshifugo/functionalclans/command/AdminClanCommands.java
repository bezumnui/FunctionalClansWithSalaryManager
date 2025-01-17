package ru.oshifugo.functionalclans.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import ru.oshifugo.functionalclans.sql.Clan;
import ru.oshifugo.functionalclans.sql.Member;
import ru.oshifugo.functionalclans.sql.SQLiteUtility;
import ru.oshifugo.functionalclans.utility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminClanCommands implements CommandExecutor {

    static String prefix = utility.config("prefix");
    private static Boolean check(CommandSender sender, String permission, String UID) {
        if (!sender.hasPermission("fc.admin." + permission)) {
            sender.sendMessage(utility.hex(utility.lang(sender,"common_errors.no_permission")));
            return true;
        } else if (Clan.hasUID(UID)) {
            sender.sendMessage(utility.hex(prefix + utility.lang(sender,"common_errors.no_clan_uid")));
            return true;
        }
        return false;
    }
    public void help(CommandSender sender) {
        int i = 0;
        TextComponent message = new TextComponent(utility.hex(prefix + utility.lang(sender,"help.fc_msg") + "\n"));
        if (sender.hasPermission("fc.admin.update")) {
            TextComponent text = new TextComponent(utility.hex(utility.lang(sender,"fc.update.errors.e") + "\n"));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fc update"));
            message.addExtra(text);
            i++;
        }
        if (sender.hasPermission("fc.admin.verify")) {
            TextComponent text = new TextComponent(utility.hex(utility.lang(sender,"fc.verify.errors.e") + "\n"));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fc verify"));
            message.addExtra(text);
            i++;
        }
        if (sender.hasPermission("fc.admin.info")) {
            TextComponent text = new TextComponent(utility.hex(utility.lang(sender,"fc.info.errors.e") + "\n"));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fc info"));
            message.addExtra(text);
            i++;
        }
        if (sender.hasPermission("fc.admin.members")) {
            TextComponent text = new TextComponent(utility.hex(utility.lang(sender,"fc.members.errors.e") + "\n"));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fc members"));
            message.addExtra(text);
            i++;
        }
        if (sender.hasPermission("fc.admin.delete")) {
            TextComponent text = new TextComponent(utility.hex(utility.lang(sender,"fc.delete.errors.e") + "\n"));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fc delete"));
            message.addExtra(text);
            i++;
        }
        if (sender.hasPermission("fc.admin.leader")) {
            TextComponent text = new TextComponent(utility.hex(utility.lang(sender,"fc.leader.errors.e") + "\n"));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fc leader"));
            message.addExtra(text);
            i++;
        }
        if (i == 0) {
            message.addExtra(utility.hex(utility.lang(sender,"common_errors.no_permission")));
        }
        sender.spigot().sendMessage(message);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            help(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("update")) {
            if (sender.hasPermission("fc.admin.update")) {
                SQLiteUtility.update();
                sender.sendMessage(utility.hex(utility.lang(sender,"fc.update.message.msg")));
                return true;
            } else sender.sendMessage(utility.hex(utility.lang(sender,"common_errors.no_permission")));
            return true;
        } else if (args[0].equalsIgnoreCase("verify")) {
            if (!sender.hasPermission("fc.admin.verify")) {
                sender.sendMessage(utility.hex(utility.lang(sender,"common_errors.no_permission")));
                return true;
            } else if (args.length == 2) {
                if (Clan.hasUID(args[1])) {
                    sender.sendMessage(utility.hex(prefix + utility.lang(sender,"common_errors.no_clan_uid")));
                    return true;
                }
                String bool = "false";
                if (Clan.getVerification(Clan.getClanNameUID(args[1])) == false) {
                    bool = "true";
                }
                Clan.setVerification(Clan.getClanNameUID(args[1]), bool);
                sender.sendMessage(utility.hex(prefix + utility.lang(sender,"fc.verify.message.msg")));
                Clan.broadcast(Clan.getClanNameUID(args[1]), utility.lang(sender,"fc.verify.message.msg1"));
                return true;
            }
            sender.sendMessage(utility.hex(String.format(utility.lang(sender,"help.msg"), command.getName(), args[0]) + "\n" + String.format(utility.lang(sender,"help.msg1"), utility.lang(sender, String.format("fc.%s.errors.e", args[0])))));
            return true;
        } else if (args[0].equalsIgnoreCase("info")) {
            if (args.length != 2) {
                sender.sendMessage(utility.hex(String.format(utility.lang(sender,"help.msg"), command.getName(), args[0]) + "\n" + String.format(utility.lang(sender,"help.msg1"), utility.lang(sender, String.format("fc.%s.errors.e", args[0])))));
                return true;
            }
            if (check(sender, "info", args[1])) {
                return true;
            }
            String  clanName = Clan.getClanNameUID(args[1]);
            TextComponent text = new TextComponent(utility.hex("\n" + utility.lang(sender,"commands.info.message-fc.line_1") + Clan.getClanRealName(clanName) + " "));
            TextComponent ver;
            TextComponent social;
            TextComponent online;
            if (Clan.getVerification(clanName)) {
                ver = new TextComponent(utility.hex(utility.lang(sender,"main.true") + "\n"));
            } else {
                ver = new TextComponent(utility.hex(utility.lang(sender,"main.false") + "\n"));
            }
            ver.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(utility.hex(utility.lang(sender,"commands.info.message-fc.line_1-1") + Clan.getDate(clanName) + "\n" + utility.lang(sender,"commands.info.message-fc.line_1-3") + Clan.getUID(clanName) + "\n" + utility.lang(sender,"commands.info.message-fc.line_1-2") + Clan.getCreator(clanName))).create()));
            text.addExtra(ver);
            text.addExtra(utility.hex(utility.lang(sender,"commands.info.message-fc.line_2") + Clan.getLeader(clanName) + "\n"));
            text.addExtra(utility.hex(utility.lang(sender,"commands.info.message-fc.line_3") + Clan.getCash(clanName) + "\n"));
            text.addExtra(utility.hex(utility.lang(sender,"commands.info.message-fc.line_4") + Clan.getRating(clanName) + "\n"));
            if (Clan.getType(clanName) == 0) {
                text.addExtra(utility.hex(utility.lang(sender,"commands.info.message-fc.line_5") + utility.lang(sender,"main.closed") + "\n"));
            } else {
                text.addExtra(utility.hex(utility.lang(sender,"commands.info.message-fc.line_5") + utility.lang(sender,"main.open") + "\n"));
            }
            text.addExtra(utility.hex(utility.lang(sender,"commands.info.message-fc.line_6") + Clan.getTax(clanName) + "\n"));
            if (!Clan.getStatus(clanName).equalsIgnoreCase("null")) {
                text.addExtra(utility.hex(utility.lang(sender,"commands.info.message-fc.line_7") + Clan.getStatus(clanName) + "\n"));
            }
            if (!Clan.getSocial(clanName).equalsIgnoreCase("null")) {
                text.addExtra(utility.hex(utility.lang(sender,"commands.info.message-fc.line_8")));
                social = new TextComponent(utility.hex(Clan.getSocial(clanName)) + "\n");
                social.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://" + Clan.getSocial(clanName)));
                text.addExtra(social);
            }
            online = new TextComponent(utility.hex(String.format(utility.lang(sender,"commands.info.message-fc.line_9"), Member.getCount(clanName), Clan.getMax_player(clanName)) + "\n"));
            online.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(utility.hex(utility.lang(sender,"commands.info.message-fc.line_9-1") + Member.getOnlineCount(clanName))).create()));
            online.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fc members " + args[1]));
            text.addExtra(online);
            if (!Clan.getMessage(clanName).equalsIgnoreCase("null")) {
                text.addExtra(utility.hex(utility.lang(sender,"commands.info.message-fc.line_10") + Clan.getMessage(clanName)) + "\n");
            }
            sender.spigot().sendMessage(text);
            return true;
        } else if (args[0].equalsIgnoreCase("members")) {
            if (args.length != 2) {
                sender.sendMessage(utility.hex(String.format(utility.lang(sender,"help.msg"), command.getName(), args[0]) + "\n" + String.format(utility.lang(sender,"help.msg1"), utility.lang(sender, String.format("fc.%s.errors.e", args[0])))));
                return true;
            }
            if (check(sender, "members", args[1])) {
                return true;
            }
            String  clanName = Clan.getClanNameUID(args[1]);
            String role_5 = "";
            String role_4 = "";
            String role_3 = "";
            String role_2 = "";
            String role_1 = "";
            for (int i = 0; i < Member.getMembers(clanName).size(); i++) {
                int role = Integer.valueOf(Member.getRank(Member.getMembers(clanName).get(i)));
                if (role == 1) {
                    if (Bukkit.getOfflinePlayer(Member.getMembers(clanName).get(i)).isOnline()) {
                        if (role_1.length() != 0) {
                            role_1 = role_1 + utility.hex(", &a") + Member.getMembers(clanName).get(i);
                        } else role_1 = utility.hex("\n" + Clan.getRoleName(clanName, 1) + "&a: " + Member.getMembers(clanName).get(i));
                    } else {
                        if (role_1.length() != 0) {
                            role_1 = role_1 +  utility.hex(", &2") + Member.getMembers(clanName).get(i);
                        } else role_1 = utility.hex("\n" + Clan.getRoleName(clanName, 1) + "&a:&2 " + Member.getMembers(clanName).get(i));
                    }
                } else if (role == 2) {
                    if (Bukkit.getOfflinePlayer(Member.getMembers(clanName).get(i)).isOnline()) {
                        if (role_2.length() != 0) {
                            role_2 = role_2 + utility.hex(", &a") + Member.getMembers(clanName).get(i);
                        } else role_2 = utility.hex("\n" + Clan.getRoleName(clanName, 2) + "&a: " + Member.getMembers(clanName).get(i));
                    } else {
                        if (role_2.length() != 0) {
                            role_2 = role_2 +  utility.hex(", &2") + Member.getMembers(clanName).get(i);
                        } else role_2 = utility.hex("\n" + Clan.getRoleName(clanName, 2) + "&a:&2 " + Member.getMembers(clanName).get(i));
                    }
                } else if (role == 3) {
                    if (Bukkit.getOfflinePlayer(Member.getMembers(clanName).get(i)).isOnline()) {
                        if (role_3.length() != 0) {
                            role_3 = role_3 + utility.hex(", &a") + Member.getMembers(clanName).get(i);
                        } else role_3 = utility.hex("\n" + Clan.getRoleName(clanName, 3) + "&a: " + Member.getMembers(clanName).get(i));
                    } else {
                        if (role_3.length() != 0) {
                            role_3 = role_3 +  utility.hex(", &2") + Member.getMembers(clanName).get(i);
                        } else role_3 = utility.hex("\n" + Clan.getRoleName(clanName, 3) + "&a:&2 " + Member.getMembers(clanName).get(i));
                    }
                } else if (role == 4) {
                    if (Bukkit.getOfflinePlayer(Member.getMembers(clanName).get(i)).isOnline()) {
                        if (role_4.length() != 0) {
                            role_4 = role_4 + utility.hex(", &a") + Member.getMembers(clanName).get(i);
                        } else role_4 = utility.hex("\n" + Clan.getRoleName(clanName, 4) + "&a: " + Member.getMembers(clanName).get(i));
                    } else {
                        if (role_4.length() != 0) {
                            role_4 = role_4 +  utility.hex(", &2") + Member.getMembers(clanName).get(i);
                        } else role_4 = utility.hex("\n" + Clan.getRoleName(clanName, 4) + "&a:&2 " + Member.getMembers(clanName).get(i));
                    }
                } else if (role == 5) {
                    if (Bukkit.getOfflinePlayer(Member.getMembers(clanName).get(i)).isOnline()) {
                        if (role_5.length() != 0) {
                            role_5 = role_5 + utility.hex(", &a")  + Member.getMembers(clanName).get(i);
                        } else role_5 = utility.hex("\n" + Clan.getRoleName(clanName, 5) + "&a: " + Member.getMembers(clanName).get(i));
                    } else {
                        if (role_5.length() != 0) {
                            role_5 = role_5 +  utility.hex(", &2")  + Member.getMembers(clanName).get(i);
                        } else role_5 = utility.hex("\n" + Clan.getRoleName(clanName, 5) + "&a:&2 " + Member.getMembers(clanName).get(i));
                    }
                }
            }
            String members = utility.hex(role_5 + role_4 + role_3 + role_2 + role_1);
            sender.sendMessage(utility.hex(prefix + utility.lang(sender,"commands.members.message.msg") + members));
            return true;
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                sender.sendMessage(utility.hex(String.format(utility.lang(sender,"help.msg"), command.getName(), args[0]) + "\n" + String.format(utility.lang(sender,"help.msg1"), utility.lang(sender, String.format("fc.%s.errors.e", args[0])))));
                return true;
            }
            if (check(sender, "delete", args[1])) {
                return true;
            }
            String clanName = Clan.getClanNameUID(args[1]);
            SQLiteUtility.delete(clanName);
            sender.sendMessage(utility.hex(prefix + utility.lang(sender,"commands.delete.message.msg")));
            return true;
        } else if (args[0].equalsIgnoreCase("leader")) {
            if (args.length != 3) {
                sender.sendMessage(utility.hex(String.format(utility.lang(sender,"help.msg"), command.getName(), args[0]) + "\n" + String.format(utility.lang(sender,"help.msg1"), utility.lang(sender, String.format("fc.%s.errors.e", args[0])))));
                return true;
            }
            if (check(sender, "leader", args[1])) {
                return true;
            }
            String clanName = Clan.getClanNameUID(args[1]);
            if (Bukkit.getOfflinePlayer(args[2]).hasPlayedBefore()) {
                if (SQLiteUtility.member_clan.get(Bukkit.getOfflinePlayer(args[2]).getName()) == null) {
                    sender.sendMessage(utility.hex(prefix + utility.lang(sender,"common_errors.no_player_clans")));
                    return true;
                }
                if (SQLiteUtility.member_clan.get(Bukkit.getOfflinePlayer(args[2]).getName()).equalsIgnoreCase(clanName)) {
                    Clan.setLeader(clanName, Bukkit.getOfflinePlayer(args[2]).getName());
                    sender.sendMessage(utility.hex(prefix + utility.lang(sender,"fc.leader.message.msg") + Bukkit.getOfflinePlayer(args[2]).getName()));
                } else sender.sendMessage(utility.hex(prefix + utility.lang(sender,"common_errors.no_player_clan")));
            } else sender.sendMessage(utility.hex(prefix + utility.lang(sender,"common_errors.no_player")));
            return true;
        }
        help(sender);
        return true;
    }
}
