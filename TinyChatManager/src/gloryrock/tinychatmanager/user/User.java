package gloryrock.tinychatmanager.user;

import gloryrock.tinychatmanager.messages.Messages;
import org.bukkit.configuration.file.FileConfiguration;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import gloryrock.tinychatmanager.CustomPermission;
import gloryrock.tinychatmanager.Database;
import gloryrock.tinychatmanager.files.ConfigData;
import gloryrock.tinychatmanager.files.FileManager;
import gloryrock.tinychatmanager.groups.GroupHandler;
import java.sql.SQLException;
import gloryrock.tinychatmanager.TinyChatManager;
import java.util.UUID;
import gloryrock.tinychatmanager.groups.Subgroup;
import gloryrock.tinychatmanager.groups.Group;
import gloryrock.tinychatmanager.utils.ChatFormatting;
import gloryrock.tinychatmanager.utils.Color;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import java.util.concurrent.ConcurrentHashMap;

public class User
{
    private static final ConcurrentHashMap<String, User> USERS;
    private final Player PLAYER;
    private UserData userData;
    private ArrayList<Color> colors;
    private ArrayList<ChatFormatting> chatFormattings;
    private Group group;
    private Subgroup subgroup;
    private String myCustomPrefix;
    private String myCustomSuffix;
    private Color chatColor;
    private ChatFormatting chatFormatting;
    private UUID uniqueId;
    private boolean forceGroup;

    public User(final Player player)
    {
        this.PLAYER = player;
        this.uniqueId = player.getUniqueId();
        
        if (TinyChatManager.getInstance().getDatabase() == null)
            this.userData = new UserData(player.getUniqueId());
        
        this.load();
    }

    public static User getUser(final Player player)
    {
        if (User.USERS.containsKey(player.getName()))
            return User.USERS.get(player.getName());
        
        return new User(player);
    }

    public static ConcurrentHashMap<String, User> getUsers()
    {
        return User.USERS;
    }

    public void load()
    {
        this.colors = new ArrayList<Color>();
        this.chatFormattings = new ArrayList<ChatFormatting>();
        
        if (!this.PLAYER.hasPermission(CustomPermission.COLOR_ALL.toString()))
        {
            for (final Color color : Color.values())
            {
                if (this.PLAYER.hasPermission(CustomPermission.COLOR.toString() + "." + color.name()))
                    this.colors.add(color);
            }
            for (final ChatFormatting formatting : ChatFormatting.values())
            {
                if (!formatting.equals(ChatFormatting.RAINBOW))
                {
                    if (this.PLAYER.hasPermission(CustomPermission.COLOR.toString() + "." + formatting.name()))
                        this.chatFormattings.add(formatting);
                }
            }
        }
        
        String groupName = null;
        String subgroupName = null;
        String chatColor = null;
        String chatFormatting = null;
        String customPrefix = null;
        String customSuffix = null;
        boolean forceGroup = false;
        
        final Database database = TinyChatManager.getInstance().getDatabase();
        if (database != null)
        {
            final String readGroupQuery = "SELECT `group`,`force_group`,`subgroup`,`custom_prefix`,`custom_suffix`,`chat_color`,`chat_formatting` FROM `" + database.getTablePrefix() + "users` WHERE `uuid` = '" + this.PLAYER.getUniqueId().toString() + "'";
            try
            {
                final ResultSet readGroupResult = database.getValue(readGroupQuery);
                if (readGroupResult.next())
                {
                    groupName = readGroupResult.getString("group");
                    subgroupName = readGroupResult.getString("subgroup");
                    chatColor = readGroupResult.getString("chat_color");
                    chatFormatting = readGroupResult.getString("chat_formatting");
                    customPrefix = readGroupResult.getString("custom_prefix");
                    customSuffix = readGroupResult.getString("custom_suffix");
                    forceGroup = readGroupResult.getBoolean("force_group");
                }
                else
                {
                    final String insertUsersQuery = "INSERT INTO `" + database.getTablePrefix() + "users`(`uuid`) VALUES (?)";
                    final PreparedStatement insertUsersStatement = database.prepareStatement(insertUsersQuery);
                    insertUsersStatement.setString(1, this.uniqueId.toString());
                    insertUsersStatement.executeUpdate();
                }
            }
            catch (SQLException exception)
            {
                exception.printStackTrace();
                return;
            }
        }
        else
        {
            this.updateUserData();
            final FileConfiguration data = this.userData.getFileData();
            groupName = data.getString("group");
            subgroupName = data.getString("subgroup");
            chatColor = data.getString("chat-color");
            chatFormatting = data.getString("chat-formatting");
            customPrefix = data.getString("custom-prefix");
            customSuffix = data.getString("custom-suffix");
            forceGroup = data.getBoolean("force-group");
        }
        
        this.forceGroup = forceGroup;
        
        if (groupName == null)
        {
            this.group = this.getGroupPerPerms();
        }
        else if ((GroupHandler.isGroup(groupName) && this.PLAYER.hasPermission(CustomPermission.GROUP.toString() + "." + groupName)) || forceGroup || groupName.equals("default"))
        {
            this.group = GroupHandler.getGroup(groupName);
        }
        else
        {
            this.group = this.getGroupPerPerms();
            this.saveData("group", null);
        }
        
        if (FileManager.getConfig().getFileData().getBoolean(ConfigData.Values.USE_SUBGROUPS.toString()) && subgroupName != null)
        {
            if (GroupHandler.isSubgroup(subgroupName) && this.PLAYER.hasPermission(CustomPermission.SUBGROUP.toString() + "." + subgroupName))
            {
                this.subgroup = GroupHandler.getSubgroup(subgroupName);
            }
            else
            {
                this.subgroup = this.getSubgroupPerPerms();
                this.saveData("subgroup", null);
            }
        }
        
        if (chatColor != null && chatColor.length() >= 2)
            this.chatColor = Color.getByCode(chatColor.substring(1, 2));
        
        if (chatFormatting == null || chatFormatting.length() < 2)
        {
            this.setChatFormatting(null);
        }
        else if (chatFormatting.equals("%r"))
        {
            this.chatFormatting = ChatFormatting.RAINBOW;
            this.setChatColor(null);
        }
        else
        {
            this.chatFormatting = ChatFormatting.getByCode(chatFormatting.substring(1, 2));
        }
        
        if (this.PLAYER.hasPermission(CustomPermission.USER_CUSTOM_PREFIX.toString()))
        {
            if (customPrefix != null)
                this.myCustomPrefix = customPrefix.replace("&", "§");
        }
        
        if (this.PLAYER.hasPermission(CustomPermission.USER_CUSTOM_SUFFIX.toString()))
        {
            if (customSuffix != null)
                this.myCustomSuffix = customSuffix.replace("&", "§");
        }
        
        User.USERS.remove(this.PLAYER.getName());
        User.USERS.put(this.PLAYER.getName(), this);
    }

    private void updateUserData()
    {
        if (this.userData.getFileData().getConfigurationSection("user") != null)
        {
            this.userData.set("group", this.userData.getFileData().getString("user.group"));
            this.userData.set("subgroup", this.userData.getFileData().getString("user.subgroup"));
            this.userData.set("chat-color", this.userData.getFileData().getString("user.chatcolor"));
            this.userData.set("custom-prefix", this.userData.getFileData().getString("user.custom.prefix"));
            this.userData.set("custom-suffix", this.userData.getFileData().getString("user.custom.suffix"));
            this.userData.set("force-group", this.userData.getFileData().getBoolean("user.force-group"));
            this.userData.set("user", null);
        }
    }

    public String getPrefix()
    {
        return this.group.getPrefix();
    }

    public void setPrefix(String prefix)
    {
        this.saveData("custom-prefix", prefix);
            prefix = prefix.replace("&", "§");
        
        if (prefix != null)
            prefix = prefix.replace("&", "§");
        
        this.myCustomPrefix = prefix;
    }

    public String getSuffix()
    {
        return this.group.getSuffix();
    }

    public void setSuffix(String suffix)
    {
        this.saveData("custom-suffix", suffix);
        
        if (suffix != null)
            suffix = suffix.replace("&", "§");
            
        this.myCustomSuffix = suffix;
    }

    public ArrayList<Color> getColors()
    {
        return this.colors;
    }

    public Color getChatColor()
    {
        return this.chatColor;
    }
    
    public String getCustomPrefix()
    {
        return this.myCustomPrefix;
    }
    
    public String getCustomSuffix()
    {
        return this.myCustomSuffix;
    }

    public void setChatColor(final Color aColor)
    {
        String value = null;
        this.chatColor = aColor;
        if (aColor != null)
        {
            value = aColor.getCode().replace("§", "&");
            
            if (this.chatFormatting != null && this.chatFormatting.equals(ChatFormatting.RAINBOW))
                this.setChatFormatting(null);
        }
        else if (this.chatFormatting != null && !this.chatFormatting.equals(ChatFormatting.RAINBOW))
        {
            this.setChatFormatting(null);
        }
        
        this.saveData("chat-color", value);
    }

    public ArrayList<ChatFormatting> getChatFormattings()
    {
        return this.chatFormattings;
    }

    public ChatFormatting getChatFormatting()
    {
        return this.chatFormatting;
    }

    public void setChatFormatting(final ChatFormatting chatFormatting)
    {
        String value = null;
        this.chatFormatting = chatFormatting;
        if (chatFormatting != null)
        {
            if (chatFormatting.equals(ChatFormatting.RAINBOW))
            {
                this.setChatColor(null);
                value = "%r";
            }
            else
            {
                value = chatFormatting.getCode().replace("§", "&");
            }
        }
        
        this.saveData("chat-formatting", value);
    }

    public Group getGroup()
    {
        return this.group;
    }

    public void setGroup(final Group group, final Boolean force)
    {
        this.group = group;
        this.chatColor = null;
        this.chatFormatting = null;
        this.saveData("group", group.getName());
        this.saveData("chat-color", null);
        this.saveData("group", group.getName());
        this.saveData("custom-prefix", null);
        this.saveData("custom-suffix", null);
        this.saveData("force-group", force);
    }

    public Subgroup getSubgroup()
    {
        return this.subgroup;
    }

    public void setSubgroup(final Subgroup subgroup)
    {
        this.subgroup = subgroup;
        final String name = (subgroup != null) ? subgroup.getName() : null;
        this.saveData("subgroup", name);
    }

    public Player getPlayer()
    {
        return this.PLAYER;
    }

    public ArrayList<Group> getAvailableGroups()
    {
        final ArrayList<Group> availableGroups = new ArrayList<Group>();
        for (final Group targetGroup : GroupHandler.getGroups().values())
        {
            if (this.PLAYER.hasPermission(CustomPermission.GROUP.toString() + "." + targetGroup.getName()))
                availableGroups.add(targetGroup);
        }
        if (this.forceGroup)
        {
            final Group group = this.getGroup();
            if (!availableGroups.contains(group))
                availableGroups.add(group);
        }
        return availableGroups;
    }

    public ArrayList<Subgroup> getAvailableSubgroups()
    {
        final ArrayList<Subgroup> availableGroups = new ArrayList<Subgroup>();
        for (final Subgroup targetGroup : GroupHandler.getSubgroups().values())
        {
            if (this.PLAYER.hasPermission(CustomPermission.SUBGROUP.toString() + "." + targetGroup.getName()))
                availableGroups.add(targetGroup);
        }
        return availableGroups;
    }

    private Group getGroupPerPerms()
    {
        for (final Group group : GroupHandler.getGroups().values())
        {
            if (this.PLAYER.hasPermission(CustomPermission.GROUP.toString() + "group." + group.getName()))
                return group;
        }
        return GroupHandler.getGroup("default");
    }

    private Subgroup getSubgroupPerPerms()
    {
        for (final Subgroup subgroup : GroupHandler.getSubgroups().values())
        {
            if (this.PLAYER.hasPermission(CustomPermission.SUBGROUP.toString() + "." + subgroup.getName()))
                return subgroup;
        }
        return null;
    }

    public String getName()
    {
        return this.PLAYER.getName();
    }

    private UserData getUserData()
    {
        return this.userData;
    }

    public void sendMessage(final String message)
    {
        this.PLAYER.sendMessage(Messages.getPrefix() + message);
    }

    private void saveData(String key, final Object value)
    {
        final Database db = TinyChatManager.getInstance().getDatabase();
        if (db == null)
        {
            key = key.replace("_", "-");
            this.getUserData().set(key, value);
        }
        else
        {
            key = key.replace("-", "_");
            final String sql = "UPDATE `" + db.getTablePrefix() + "users` SET `" + key + "`=? WHERE `uuid`=?";
            final PreparedStatement stmt = db.prepareStatement(sql);
            try
            {
                stmt.setObject(1, value);
                stmt.setString(2, this.getPlayer().getUniqueId().toString());
                stmt.executeUpdate();
            }
            catch (SQLException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    static
    {
        USERS = new ConcurrentHashMap<String, User>();
    }
}
