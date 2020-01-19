package gloryrock.tinychatmanager.groups;

import gloryrock.tinychatmanager.utils.ChatFormatting;
import javax.annotation.Nullable;
import gloryrock.tinychatmanager.utils.Color;
import org.bukkit.ChatColor;

public abstract class EasyGroup
{
    public abstract String getName();

    public abstract String getRawPrefix();

    public abstract String getPrefix();

    public abstract void setPrefix(final String p0);

    public abstract String getRawSuffix();

    public abstract String getSuffix();

    public abstract void setSuffix(final String p0);

    public abstract ChatColor getGroupColor();

    @Nullable
    public abstract Color getChatColor();

    public abstract void setChatColor(final Color p0);

    @Nullable
    public abstract ChatFormatting getChatFormatting();

    public abstract void setChatFormatting(final ChatFormatting p0);

    public abstract String getFilePath();

    public abstract void delete();
}
