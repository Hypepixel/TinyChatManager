package gloryrock.tinychatmanager.commands;

import gloryrock.tinychatmanager.groups.Subgroup;
import org.bukkit.configuration.file.FileConfiguration;
import gloryrock.tinychatmanager.groups.Group;
import java.sql.SQLException;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import gloryrock.tinychatmanager.files.FileManager;
import org.bukkit.event.HandlerList;
import org.bukkit.Bukkit;
import gloryrock.tinychatmanager.groups.GroupHandler;
import gloryrock.tinychatmanager.messages.Messages;
import gloryrock.tinychatmanager.messages.Message;
import gloryrock.tinychatmanager.TinyChatManager;
import gloryrock.tinychatmanager.user.User;
import gloryrock.tinychatmanager.commands.CustomCommand;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

public class CommandKit implements Listener, CommandExecutor
{
    public boolean onCommand(final CommandSender aSender, final Command aCommand, final String aLabel,
        final String[] someArguments)
    {
        User user = null;
        if (aSender instanceof Player)
            user = User.getUser((Player) aSender);

        if (someArguments.length == 1)
        {
            if (someArguments[0].equalsIgnoreCase(CustomCommand.RELOAD.toString()))
            {
                if (aSender.hasPermission(CustomCommand.RELOAD.getPermission()))
                {
                    TinyChatManager.getInstance().reload();
                    aSender.sendMessage(Messages.getMessage(Message.RELOAD_COMPLETE));
                    return true;
                }
                aSender.sendMessage(Messages.getMessage(Message.NO_PERMS, user));
                return false;
            }
            else
            {
                if (someArguments[0].equalsIgnoreCase(CustomCommand.DEBUG.toString())
                    && aSender.hasPermission(CustomCommand.DEBUG.getPermission()))
                {
                    aSender.sendMessage(" ");
                    aSender.sendMessage("§6§lTinyChatManager Debug");
                    aSender.sendMessage(
                        "Groups: " + GroupHandler.getGroups().size() + "/" + GroupHandler.getSubgroups().size());
                    aSender.sendMessage("Users cached: " + User.getUsers().size());
                    aSender.sendMessage("Bukkit Version: " + Bukkit.getVersion());
                    aSender.sendMessage("Version Name: " + Bukkit.getBukkitVersion());
                    aSender.sendMessage("active EventHandler: "
                        + HandlerList.getRegisteredListeners(TinyChatManager.getInstance().getPlugin()).size());
                    return true;
                }
                if (someArguments[0].equalsIgnoreCase(CustomCommand.DATABASE.toString())
                    && TinyChatManager.getInstance().getDatabase() != null
                    && aSender.hasPermission(CustomCommand.DATABASE.getPermission()))
                {
                    aSender.sendMessage("§6§lTinyChatManager");
                    aSender.sendMessage(" ");
                    aSender.sendMessage(
                        "§7/§6TinyChatManager database migrate §f| §7save groups and users to database (will override database!)");
                    aSender.sendMessage(" ");
                    return true;
                }
            }
        }
        else if (someArguments.length >= 2)
        {
            if (someArguments[0].equalsIgnoreCase(CustomCommand.SET.toString()))
            {
                if (!aSender.hasPermission(CustomCommand.SET.getPermission()))
                {
                    aSender.sendMessage(Messages.getMessage(Message.NO_PERMS));
                    return false;
                }
                if (someArguments.length != 3)
                {
                    aSender.sendMessage(Messages.getPrefix() + "§7Usage: /TinyChatManager set <Player> <Prefix>");
                    return false;
                }
                if (!GroupHandler.isGroup(someArguments[2]))
                {
                    aSender.sendMessage(Messages.getMessage(Message.GROUP_NOT_FOUND));
                    return false;
                }
                final Player targetPlayer = Bukkit.getPlayer(someArguments[1]);
                final Group group = GroupHandler.getGroup(someArguments[2]);
                if (targetPlayer != null)
                {
                    final User target = User.getUser(targetPlayer);
                    target.setGroup(group, true);
                    aSender.sendMessage(Messages.getMessage(Message.SUCCESS));
                    return true;
                }
                final Player op = Bukkit.getPlayer(someArguments[1]);
                final File userFile = new File(FileManager.getPluginFolder() + "/user", op.getUniqueId() + ".yml");
                if (op.hasPlayedBefore() && userFile.exists())
                {
                    final FileConfiguration userData = (FileConfiguration) YamlConfiguration
                        .loadConfiguration(userFile);
                    userData.set("user.group", (Object) group.getName());
                    try
                    {
                        userData.save(userFile);
                    }
                    catch (IOException exception)
                    {
                        exception.printStackTrace();
                    }
                    aSender.sendMessage(Messages.getMessage(Message.SUCCESS));
                    return true;
                }
                aSender.sendMessage(Messages.getMessage(Message.PLAYER_NOT_FOUND));
                return false;
            }
            else if (someArguments[0].equalsIgnoreCase(CustomCommand.USER.toString()))
            {
                if (!aSender.hasPermission(CustomCommand.USER.getPermission()))
                {
                    aSender.sendMessage(Messages.getMessage(Message.NO_PERMS));
                    return false;
                }
                final Player player = Bukkit.getPlayer(someArguments[1]);
                if (player != null)
                {
                    final User target2 = new User(player);
                    if (someArguments.length >= 3)
                    {
                        if (someArguments[2].equalsIgnoreCase(CustomCommand.USER_UPDATE.toString()))
                        {
                            Bukkit.getScheduler().runTaskLaterAsynchronously(TinyChatManager.getInstance().getPlugin(),
                                () -> {
                                    target2.load();
                                    aSender.sendMessage(Messages.getMessage(Message.SUCCESS));
                                    return;
                                }, 20L);
                            return true;
                        }
                        if (someArguments[2].equalsIgnoreCase(CustomCommand.USER_INFO.toString()))
                        {
                            aSender.sendMessage(" ");
                            aSender.sendMessage(
                                "§6§l" + target2.getPlayer().getName());
                            aSender.sendMessage(" ");
                            aSender.sendMessage("§6Group§f: §7" + target2.getGroup().getName());
                            final String subgroup = (target2.getSubgroup() != null) ? target2.getSubgroup().getName()
                                : "-";
                            aSender.sendMessage("§6Subgroup§f: §7" + subgroup);
                            aSender.sendMessage("§6Prefix§f: §8«§7" + target2.getPrefix().replace("§", "&") + "§8»");
                            aSender.sendMessage("§6Suffix§f: §8«§7" + target2.getSuffix().replace("§", "&") + "§8»");
                            String colorCode = (target2.getChatColor() != null) ? target2.getChatColor().getCode()
                                : "-";
                            if (target2.getChatFormatting() != null)
                                colorCode += target2.getChatFormatting().getCode();
                            
                            aSender.sendMessage("§6Chatcolor§f: §7" + colorCode.replace("§", "&"));
                            aSender.sendMessage(" ");
                            return true;
                        }
                        if (someArguments[2].equalsIgnoreCase(CustomCommand.USER_SETGROUP.toString()))
                        {
                            if (someArguments.length == 4)
                            {
                                if (GroupHandler.isGroup(someArguments[3]))
                                {
                                    final Group targetGroup = GroupHandler.getGroup(someArguments[3]);
                                    target2.setGroup(targetGroup, true);
                                    aSender.sendMessage(Messages.getMessage(Message.SUCCESS));
                                    return true;
                                }
                                aSender.sendMessage(Messages.getMessage(Message.GROUP_NOT_FOUND));
                                return false;
                            }
                        }
                        else if (someArguments[2].equalsIgnoreCase(CustomCommand.USER_SETSUBGROUP.toString()))
                        {
                            if (someArguments.length == 4)
                            {
                                if (GroupHandler.isSubgroup(someArguments[3]))
                                {
                                    final Subgroup targetGroup2 = GroupHandler.getSubgroup(someArguments[3]);
                                    target2.setSubgroup(targetGroup2);
                                    aSender.sendMessage(Messages.getMessage(Message.SUCCESS));
                                    return true;
                                }
                                aSender.sendMessage(Messages.getMessage(Message.GROUP_NOT_FOUND));
                                return false;
                            }
                        }
                    }
                    aSender.sendMessage(" ");
                    aSender.sendMessage("§6§lTinyChatManager User");
                    aSender.sendMessage(" ");
                    aSender.sendMessage("§7/§6TinyChatManager user <Player> info §f| §7" + CustomCommand.USER_INFO.getDescription());
                    aSender.sendMessage("§7/§6TinyChatManager user <Player> update §f| §7" + CustomCommand.USER_UPDATE.getDescription());
                    aSender.sendMessage("§7/§6TinyChatManager user <Player> setgroup <Group> §f| §7" + CustomCommand.USER_SETGROUP.getDescription());
                    aSender.sendMessage("§7/§6TinyChatManager user <Player> setsubgroup <Subgroup> §f| §7" + CustomCommand.USER_SETSUBGROUP.getDescription());
                    aSender.sendMessage(" ");
                    return false;
                }
                aSender.sendMessage(Messages.getMessage(Message.PLAYER_NOT_FOUND));
                return false;
            }
            else if (someArguments[0].equalsIgnoreCase(CustomCommand.DATABASE.toString())
                && TinyChatManager.getInstance().getDatabase() != null)
            {
                if (!aSender.hasPermission(CustomCommand.DATABASE.getPermission()))
                {
                    aSender.sendMessage(Messages.getMessage(Message.NO_PERMS));
                    return false;
                }
                if (someArguments[1].equalsIgnoreCase(CustomCommand.DATABASE_MIGRATE.toString()))
                {
                    aSender
                        .sendMessage(Messages.getPrefix() + "§7Uploading data to database. This could take a while.");
                    try
                    {
                        TinyChatManager.getInstance().getDatabase().migrateData();
                    }
                    catch (SQLException exception)
                    {
                        exception.printStackTrace();
                        return false;
                    }
                    TinyChatManager.getInstance().reload();
                    aSender.sendMessage(Messages.getPrefix() + "§7Files have been uploaded!");
                    return true;
                }
                return true;
            }
        }
        aSender.sendMessage(" ");
        aSender.sendMessage("§6§lTinyChatManager");
        aSender.sendMessage(" ");
        aSender.sendMessage("§7/§6TinyChatManager §7" + CustomCommand.ROOT.getDescription());
        aSender.sendMessage("§7/§6TinyChatManager reload §7" + CustomCommand.RELOAD.getDescription());
        aSender.sendMessage("§7/§6TinyChatManager user <Player> §7" + CustomCommand.USER.getDescription());
        aSender.sendMessage("§7/§6TinyChatManager set <Player> <Prefix> §7" + CustomCommand.SET.getDescription());
        
        if (TinyChatManager.getInstance().getDatabase() != null && aSender.hasPermission(CustomCommand.DATABASE.getPermission()))
            aSender.sendMessage("§7/§6TinyChatManager database §7" + CustomCommand.DATABASE.getDescription());
        
        aSender.sendMessage(" ");
        aSender.sendMessage("§7Version: " + TinyChatManager.getInstance().getPlugin().getDescription().getVersion());
        aSender.sendMessage("§7TinyChatManager by §b§lGloryrock");
        aSender.sendMessage(" ");
        return false;
    }
}
