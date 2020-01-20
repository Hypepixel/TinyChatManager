package gloryrock.tinychatmanager.groups;

import javax.annotation.Nullable;
import gloryrock.tinychatmanager.user.User;
import java.sql.PreparedStatement;
import org.bukkit.configuration.file.FileConfiguration;
import java.sql.ResultSet;
import gloryrock.tinychatmanager.Database;
import java.sql.SQLException;
import gloryrock.tinychatmanager.TinyChatManager;
import gloryrock.tinychatmanager.files.FileManager;
import gloryrock.tinychatmanager.utils.ChatFormatting;
import gloryrock.tinychatmanager.utils.Color;
import gloryrock.tinychatmanager.files.GroupsData;
import org.bukkit.ChatColor;

public class Group extends EasyGroup
{
    private final String name;
    private String prefix;
    private String suffix;
    private String rawPrefix;
    private String rawSuffix;
    private String joinMessage;
    private String quitMessage;
    private ChatColor groupColor;
    private GroupsData groupsData;
    private Color chatColor;
    private ChatFormatting chatFormatting;

    Group(final String name)
    {
        this.groupsData = FileManager.getGroups();
        this.name = name;
        String chatColor = "";
        String chatFormatting = "";
        String joinMsg = "";
        String quitMsg = "";
        final Database db = TinyChatManager.getInstance().getDatabase();
        if (db != null)
        {
            try
            {
                final String sql = "SELECT `prefix`,`suffix`,`chat_color`,`chat_formatting`,`join_msg`,`quit_msg` FROM `%p%groups` WHERE `group` = '" + name + "'";
                final ResultSet result = db.getValue(sql);
                while (result.next())
                {
                    this.rawPrefix = result.getString("prefix");
                    this.rawSuffix = result.getString("suffix");
                    chatColor = result.getString("chat_color");
                    chatFormatting = result.getString("chat_formatting");
                    joinMsg = result.getString("join_msg");
                    quitMsg = result.getString("quit_msg");
                }
            }
            catch (SQLException exception)
            {
                exception.printStackTrace();
            }
        }
        else
        {
            final FileConfiguration data = this.getGroupsData().getFileData();
            if (data.getString(this.getFilePath() + "chatcolor") != null)
            {
                this.getGroupsData().set(this.getFilePath() + "chat-color", data.getString(this.getFilePath() + "chatcolor"));
                this.getGroupsData().set(this.getFilePath() + "chatcolor", null);
            }
            if (data.getString(this.getFilePath() + "chatformatting") != null)
            {
                this.getGroupsData().set(this.getFilePath() + "chat-formatting", data.getString(this.getFilePath() + "chatformatting"));
                this.getGroupsData().set(this.getFilePath() + "chatformatting", null);
            }
            this.rawPrefix = data.getString(this.getFilePath() + "prefix");
            this.rawSuffix = data.getString(this.getFilePath() + "suffix");
            chatColor = data.getString(this.getFilePath() + "chat-color");
            chatFormatting = data.getString(this.getFilePath() + "chat-formatting");
            joinMsg = data.getString(this.getFilePath() + "join-msg");
            quitMsg = data.getString(this.getFilePath() + "quit-msg");
        }
        this.rawPrefix = ((this.rawPrefix != null) ? this.rawPrefix.replace("§", "&") : "");
        this.prefix = this.translate(this.rawPrefix);
        this.rawSuffix = ((this.rawSuffix != null) ? this.rawSuffix.replace("§", "&") : "");
        this.suffix = this.translate(this.rawSuffix);
        if (chatColor == null || chatColor.length() < 2)
        {
            this.setChatColor(Color.GRAY);
            chatColor = "&7";
        }
        if (chatColor.length() > 2)
        {
            this.setChatColor(Color.getByCode(chatColor.substring(1, 2)));
            this.setChatFormatting(ChatFormatting.getByCode(chatColor.substring(3, 4)));
        }
        final String chatColorCode = chatColor.substring(1, 2);
        if (chatColorCode.equals("r"))
        {
            this.setChatColor(null);
            this.setChatFormatting(ChatFormatting.RAINBOW);
        }
        this.chatColor = Color.getByCode(chatColorCode);
        if (this.chatColor == null)
            this.setChatColor(null);

        if (chatFormatting != null && chatFormatting.length() >= 2)
        {
            this.chatFormatting = ChatFormatting.getByCode(chatFormatting.substring(1, 2));
            if (this.chatFormatting == null)
                this.setChatFormatting(null);
        }
        if (this.getRawPrefix().contains("&"))
        {
            if (!this.getRawPrefix().startsWith("&"))
            {
                String temp;
                for (temp = this.getRawPrefix(); !temp.startsWith("&") && temp.length() > 0; temp = temp.substring(1))
                {
                }
                this.groupColor = ChatColor.getByChar(temp.substring(1, 2));
            }
            else
            {
                this.groupColor = ChatColor.getByChar(this.getPrefix().substring(1, 2));
            }
        }
        if (this.getGroupColor() == null)
            this.groupColor = ChatColor.DARK_PURPLE;

        this.joinMessage = ((joinMsg == null) ? this.translate(GroupHandler.getGroup("default").getJoinMessage()) : this.translate(joinMsg));
        this.quitMessage = ((quitMsg == null) ? this.translate(GroupHandler.getGroup("default").getQuitMessage()) : this.translate(quitMsg));
    }

    public String getJoinMessage()
    {
        return this.joinMessage;
    }

    public String getQuitMessage()
    {
        return this.quitMessage;
    }

    private GroupsData getGroupsData()
    {
        return this.groupsData;
    }

    private void saveData(String key, Object value)
    {
        final Database db = TinyChatManager.getInstance().getDatabase();
        if (value instanceof String)
            value = ((String) value).replace("§", "&");

        if (db == null)
        {
            key = key.replace("_", "-");
            this.groupsData.set(this.getFilePath() + key, value);
        }
        else
        {
            key = key.replace("-", "_");
            final String sql = "UPDATE `" + db.getTablePrefix() + "groups` SET `" + key + "`=? WHERE `group`=?";
            final PreparedStatement stmt = db.prepareStatement(sql);
            try
            {
                stmt.setObject(1, value);
                stmt.setString(2, this.name);
                stmt.executeUpdate();
            }
            catch (SQLException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public String getFilePath()
    {
        return "groups." + this.name + ".";
    }

    @Override
    public void delete()
    {
        if (TinyChatManager.getInstance().getDatabase() == null)
        {
            this.groupsData.set("groups." + this.getName(), null);
        }
        else
        {
            final Database db = TinyChatManager.getInstance().getDatabase();
            db.update("DELETE FROM `%p%groups` WHERE `group` = '" + this.getName() + "'");
        }
        GroupHandler.getGroups().remove(this.name.toLowerCase());
        User.getUsers().clear();
    }

    @Override
    public String getPrefix()
    {
        return this.prefix;
    }

    @Override
    public void setPrefix(final String prefix)
    {
        this.prefix = prefix;
        this.rawPrefix = this.suffix.replace("§", "&");
        this.saveData("prefix", prefix);
    }

    @Override
    public String getRawSuffix()
    {
        return this.rawSuffix;
    }

    @Override
    public void setSuffix(final String suffix)
    {
        this.suffix = suffix;
        this.rawSuffix = suffix.replace("§", "&");
        this.saveData("suffix", suffix);
    }

    @Override
    public String getSuffix()
    {
        return this.suffix;
    }

    @Override
    public Color getChatColor()
    {
        return this.chatColor;
    }

    @Override
    public void setChatColor(final Color color)
    {
        this.chatColor = color;
        String value = null;
        if (color != null)
        {
            value = color.getCode().replace("§", "&");
            if (ChatFormatting.RAINBOW.equals(this.chatFormatting))
                this.setChatFormatting(null);
        }
        else if (this.chatFormatting != null && !this.chatFormatting.equals(ChatFormatting.RAINBOW))
        {
            this.setChatFormatting(null);
        }

        this.saveData("chat-color", value);
    }

    @Nullable
    @Override
    public ChatFormatting getChatFormatting()
    {
        return this.chatFormatting;
    }

    @Override
    public void setChatFormatting(final ChatFormatting chatFormatting)
    {
        this.chatFormatting = chatFormatting;
        String value = null;
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

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getRawPrefix()
    {
        return this.rawPrefix;
    }

    @Override
    public ChatColor getGroupColor()
    {
        return this.groupColor;
    }

    private String translate(final String text)
    {
        return (text != null) ? ChatColor.translateAlternateColorCodes('&', text) : text;
    }
}
