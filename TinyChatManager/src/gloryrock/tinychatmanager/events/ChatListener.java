package gloryrock.tinychatmanager.events;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;

import gloryrock.tinychatmanager.CustomPermission;
import gloryrock.tinychatmanager.files.ConfigData;
import gloryrock.tinychatmanager.files.FileManager;
import gloryrock.tinychatmanager.utils.Color;
import org.bukkit.ChatColor;
import gloryrock.tinychatmanager.utils.RainbowEffect;
import gloryrock.tinychatmanager.utils.ChatFormatting;
import gloryrock.tinychatmanager.user.User;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.Listener;

public class ChatListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final AsyncPlayerChatEvent event)
    {
        final User user = User.getUser(event.getPlayer());
        String prefix = user.getPrefix();
        String suffix = user.getSuffix();
        String msg = event.getMessage();
        String chatColor = "";

        if (user.getChatColor() != null || user.getChatFormatting() != null)
        {
            if (user.getChatFormatting() != null && user.getChatFormatting().equals(ChatFormatting.RAINBOW))
            {
                msg = RainbowEffect.addRainbowEffect(msg);
            }
            else
            {
                chatColor = user.getChatColor().getCode();
                if (user.getChatFormatting() != null)
                    chatColor += user.getChatFormatting().getCode();
            }
        }
        else if (user.getGroup().getChatFormatting() != null
            && user.getGroup().getChatFormatting().equals(ChatFormatting.RAINBOW))
        {
            msg = RainbowEffect.addRainbowEffect(msg);
        }
        else
        {
            chatColor = user.getGroup().getChatColor().getCode();
            if (user.getGroup().getChatFormatting() != null)
                chatColor += user.getGroup().getChatFormatting().getCode();
        }
        if (user.getPlayer().hasPermission(CustomPermission.COLOR_ALL.toString()))
        {
            msg = ChatColor.translateAlternateColorCodes('&', msg);
        }
        else
        {
            for (final Color color : user.getColors())
            {
                msg = msg.replace(color.getCode().replace("§", "&"), color.getCode());
            }
            for (final ChatFormatting formatting : user.getChatFormattings())
            {
                msg = msg.replace(formatting.getCode().replace("§", "&"), formatting.getCode());
            }
        }
        event.setMessage(msg);
        String format = prefix + user.getPlayer().getDisplayName() + suffix + " " + chatColor + event.getMessage();
        if (!FileManager.getConfig().getFileData().getBoolean(ConfigData.Values.DUPLICATE_WHITE_SPACES.toString()))
            format = format.replaceAll("\\s+", " ");

        event.setFormat(format.replace("%", "%%"));
    }
}
