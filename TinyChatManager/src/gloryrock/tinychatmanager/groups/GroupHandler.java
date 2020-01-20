package gloryrock.tinychatmanager.groups;

import java.sql.ResultSet;
import gloryrock.tinychatmanager.Database;
import org.bukkit.configuration.ConfigurationSection;
import java.util.Set;
import java.sql.SQLException;
import java.util.ArrayList;
import gloryrock.tinychatmanager.files.ConfigData;
import java.io.File;
import gloryrock.tinychatmanager.TinyChatManager;
import gloryrock.tinychatmanager.user.User;
import gloryrock.tinychatmanager.files.FileManager;
import gloryrock.tinychatmanager.files.GroupsData;
import java.util.concurrent.ConcurrentHashMap;

public class GroupHandler
{
    private static ConcurrentHashMap<String, Group> groups;
    private static ConcurrentHashMap<String, Subgroup> subgroups;
    private static GroupsData groupsData;

    public static void load()
    {
        GroupHandler.groupsData = FileManager.getGroups();
        GroupHandler.groups = new ConcurrentHashMap<String, Group>();
        User.getUsers().clear();
        GroupHandler.groups.put("default", new Group("default"));
        if (TinyChatManager.getInstance().getDatabase() == null)
        {
            if (getGroupsData().getFileData().getString("groups.default.prefix") == null)
            {
                getGroupsData().set("groups.default.prefix", "&7");
            }
            if (getGroupsData().getFileData().getString("groups.default.suffix") == null)
            {
                getGroupsData().set("groups.default.suffix", "&f:");
            }
            if (getGroupsData().getFileData().getString("groups.default.chat-color") == null)
            {
                getGroupsData().set("groups.default.chat-color", "&7");
            }
            if (getGroupsData().getFileData().getString("groups.default.join-msg") == null)
            {
                getGroupsData().set("groups.default.join-msg", "&8» %ep_user_prefix% %player% &8joined the game");
            }
            if (getGroupsData().getFileData().getString("groups.default.quit-msg") == null)
            {
                getGroupsData().set("groups.default.quit-msg", "&8« %ep_user_prefix% %player% &8left the game");
            }
            final Set<String> groupsList = (Set<String>) getGroupsData().getFileData().getConfigurationSection("groups").getKeys(false);
            for (final String g : groupsList)
            {
                if (g.equals("default"))
                {
                    continue;
                }
                final Group group = new Group(g);
                GroupHandler.groups.put(g.toLowerCase(), group);
            }
            if (GroupHandler.groups.isEmpty())
            {
                final File old = new File("plugins/TinyChatManager", "groups.yml");
                final File backup = new File("plugins/TinyChatManager", "backup-groups.yml");
                if (old.renameTo(backup))
                {
                    getGroupsData().load();
                    load();
                }
            }
            if (FileManager.getConfig().getFileData().getBoolean(ConfigData.Values.USE_SUBGROUPS.toString()))
            {
                final ConfigurationSection configurationSection = getGroupsData().getFileData().getConfigurationSection("subgroups");
                if (configurationSection != null)
                {
                    final Set<String> subgroupsList = (Set<String>) configurationSection.getKeys(false);
                    for (final String g2 : subgroupsList)
                    {
                        final Subgroup group2 = new Subgroup(g2);
                        GroupHandler.subgroups.put(g2.toLowerCase(), group2);
                    }
                }
            }
        }
        else
        {
            final Database db = TinyChatManager.getInstance().getDatabase();
            final ResultSet result = db.getValue("SELECT `group` FROM `%p%groups`");
            final ArrayList<String> groupList = new ArrayList<String>();
            try
            {
                while (result.next())
                {
                    groupList.add(result.getString("group"));
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                return;
            }
            for (final String name : groupList)
            {
                if (name.equals("default"))
                {
                    continue;
                }
                final Group group3 = new Group(name);
                GroupHandler.groups.put(name.toLowerCase(), group3);
            }
            final ResultSet result2 = db.getValue("SELECT `group` FROM `%p%subgroups`");
            final ArrayList<String> sgList = new ArrayList<String>();
            try
            {
                while (result2.next())
                {
                    sgList.add(result2.getString("group"));
                }
            }
            catch (SQLException e2)
            {
                e2.printStackTrace();
                return;
            }
            for (final String name2 : sgList)
            {
                if (name2.equals("default"))
                {
                    continue;
                }
                final Subgroup group4 = new Subgroup(name2);
                GroupHandler.subgroups.put(name2.toLowerCase(), group4);
            }
        }
    }

    public static Group getGroup(final String name)
    {
        return getGroups().getOrDefault(name.toLowerCase(), GroupHandler.groups.get("default"));
    }

    public static Subgroup getSubgroup(final String name)
    {
        return getSubgroups().getOrDefault(name.toLowerCase(), null);
    }

    public static Boolean isGroup(final String group)
    {
        return getGroups().containsKey(group.toLowerCase());
    }

    public static Boolean isSubgroup(final String group)
    {
        return getSubgroups().containsKey(group.toLowerCase());
    }

    public static ConcurrentHashMap<String, Group> getGroups()
    {
        return GroupHandler.groups;
    }

    public static ConcurrentHashMap<String, Subgroup> getSubgroups()
    {
        return GroupHandler.subgroups;
    }

    public static void createGroup(final String groupName)
    {
        getGroupsData().getFileData().set("groups." + groupName + ".prefix", (Object) ("&6" + groupName + " &7| &8"));
        getGroupsData().getFileData().set("groups." + groupName + ".suffix", (Object) "&f:");
        getGroupsData().getFileData().set("groups." + groupName + ".chat-color", (Object) "&7");
        getGroupsData().getFileData().set("groups." + groupName + ".chat-formatting", (Object) "&o");
        getGroupsData().save();
        getGroups().put(groupName.toLowerCase(), new Group(groupName));
    }

    private static GroupsData getGroupsData()
    {
        return GroupHandler.groupsData;
    }

    static
    {
        GroupHandler.groups = new ConcurrentHashMap<String, Group>();
        GroupHandler.subgroups = new ConcurrentHashMap<String, Subgroup>();
    }
}
