package gloryrock.tinychatmanager.commands;

import java.util.Collections;
import gloryrock.tinychatmanager.groups.Subgroup;
import gloryrock.tinychatmanager.groups.Group;
import gloryrock.tinychatmanager.groups.GroupHandler;
import gloryrock.tinychatmanager.CustomPermission;
import gloryrock.tinychatmanager.commands.CustomCommand;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandTabCompletion implements TabCompleter
{
    public List<String> onTabComplete(final CommandSender aSender, final Command aCommand, final String aLabel,
        final String[] someArguments)
    {
        if (aCommand.getName().equalsIgnoreCase(CustomCommand.ROOT.toString()))
        {
            final ArrayList<String> commands = new ArrayList<String>();
            if (someArguments.length == 1)
            {
                if (aSender.hasPermission(CustomPermission.ADMIN.toString()))
                {
                    commands.add(CustomCommand.RELOAD.toString());
                    commands.add(CustomCommand.SET.toString());
                    commands.add(CustomCommand.USER.toString());
                    commands.add(CustomCommand.DATABASE.toString());
                }
            }
            else if (someArguments.length == 2)
            {
                if (someArguments[0].equalsIgnoreCase(CustomCommand.USER.toString()))
                {
                    for (final Player target : Bukkit.getOnlinePlayers())
                        commands.add(target.getDisplayName());
                }
                else if (someArguments[0].equalsIgnoreCase(CustomCommand.DATABASE.toString()))
                    commands.add(CustomCommand.DATABASE_MIGRATE.toString());
            }
            else if (someArguments.length == 3)
            {
                if (someArguments[0].equalsIgnoreCase(CustomCommand.USER.toString()))
                {
                    commands.add(CustomCommand.USER_INFO.toString());
                    commands.add(CustomCommand.USER_UPDATE.toString());
                    commands.add(CustomCommand.USER_SETGROUP.toString());
                    commands.add(CustomCommand.USER_SETSUBGROUP.toString());
                }
            }
            else if (someArguments.length == 4 && someArguments[0].equalsIgnoreCase(CustomCommand.USER.toString()))
            {
                if (someArguments[2].equalsIgnoreCase(CustomCommand.USER_SETGROUP.toString()))
                {
                    for (final Group targetGroup : GroupHandler.getGroups().values())
                        commands.add(targetGroup.getName());
                }
                else if (someArguments[2].equalsIgnoreCase(CustomCommand.USER_SETSUBGROUP.toString()))
                {
                    for (final Subgroup targetGroup2 : GroupHandler.getSubgroups().values())
                        commands.add(targetGroup2.getName());
                }
            }
            Collections.sort(commands);
            return commands;
        }
        return null;
    }
}
