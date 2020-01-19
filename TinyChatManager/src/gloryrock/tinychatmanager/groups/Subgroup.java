package gloryrock.tinychatmanager.groups;

import java.sql.PreparedStatement;
import gloryrock.tinychatmanager.user.User;
import gloryrock.tinychatmanager.utils.ChatFormatting;
import javax.annotation.Nullable;
import gloryrock.tinychatmanager.utils.Color;
import java.sql.ResultSet;
import gloryrock.tinychatmanager.Database;
import java.sql.SQLException;
import gloryrock.tinychatmanager.TinyChatManager;
import gloryrock.tinychatmanager.files.GroupsData;
import org.bukkit.ChatColor;

public class Subgroup extends EasyGroup
{
    private String name;
    private String prefix;
    private String suffix;
    private String rawPrefix;
    private String rawSuffix;
    private ChatColor groupColor;
    private GroupsData groupsData;

    Subgroup(final String name)
    {
        this.name = name;
        final Database db = TinyChatManager.getInstance().getDatabase();
        if (db != null)
        {
            try
            {
                final ResultSet result = db
                    .getValue("SELECT `prefix`, `suffix` FROM `%p%subgroups` WHERE `group` = '" + name + "'");
                while (result.next())
                {
                    this.prefix = result.getString("prefix");
                    this.suffix = result.getString("suffix");
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        if (this.prefix == null)
        {
            this.rawPrefix = "";
            this.saveData("prefix", this.prefix = "");
        }
        else
        {
            this.rawPrefix = this.prefix.replace("§", "&");
            this.prefix = this.translate(this.prefix);
        }
        if (this.suffix == null)
        {
            this.rawSuffix = "";
            this.saveData("suffix", this.suffix = "");
        }
        else
        {
            this.rawSuffix = this.suffix.replace("§", "&");
            this.suffix = this.translate(this.suffix);
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
        {
            this.groupColor = ChatColor.DARK_PURPLE;
        }
    }

    @Override
    public String getSuffix()
    {
        return this.suffix;
    }

    @Override
    public void setSuffix(final String suffix)
    {
        this.rawSuffix = suffix.replace("§", "&");
        this.suffix = this.translate(suffix);
        this.saveData("suffix", suffix);
    }

    @Override
    public ChatColor getGroupColor()
    {
        return this.groupColor;
    }

    @Nullable
    @Override
    public Color getChatColor()
    {
        return null;
    }

    @Override
    public void setChatColor(final Color color)
    {
    }

    @Nullable
    @Override
    public ChatFormatting getChatFormatting()
    {
        return null;
    }

    @Override
    public void setChatFormatting(final ChatFormatting chatFormatting)
    {
    }

    @Override
    public String getFilePath()
    {
        return "subgroups." + this.getName() + ".";
    }

    @Override
    public void delete()
    {
        if (TinyChatManager.getInstance().getDatabase() == null)
        {
            this.groupsData.set("subgroups." + this.getName(), null);
        }
        else
        {
            final Database db = TinyChatManager.getInstance().getDatabase();
            db.update("DELETE FROM `%p%subgroups` WHERE `group` = '" + this.getName() + "'");
        }
        GroupHandler.getSubgroups().remove(this.getName().toLowerCase());
        User.getUsers().clear();
    }

    private void saveData(String key, Object value)
    {
        final Database db = TinyChatManager.getInstance().getDatabase();
        if (value instanceof String)
        {
            value = ((String) value).replace("§", "&");
        }
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
                stmt.setString(2, this.getName());
                stmt.executeUpdate();
            }
            catch (SQLException exception)
            {
                exception.printStackTrace();
            }
        }
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
    public String getPrefix()
    {
        return this.prefix;
    }

    @Override
    public void setPrefix(final String prefix)
    {
        this.rawPrefix = prefix.replace("§", "&");
        this.prefix = this.translate(prefix);
        this.saveData("prefix", prefix);
    }

    @Override
    public String getRawSuffix()
    {
        return this.rawSuffix;
    }

    private String translate(final String text)
    {
        return (text != null) ? ChatColor.translateAlternateColorCodes('&', text) : text;
    }
}
