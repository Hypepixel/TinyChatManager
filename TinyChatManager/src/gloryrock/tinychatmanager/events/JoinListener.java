package gloryrock.tinychatmanager.events;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import gloryrock.tinychatmanager.groups.Group;
import org.bukkit.configuration.file.FileConfiguration;
import gloryrock.tinychatmanager.messages.Messages;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import gloryrock.tinychatmanager.files.ConfigData;
import gloryrock.tinychatmanager.files.FileManager;
import gloryrock.tinychatmanager.user.User;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;

public class JoinListener implements Listener
{
    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(final PlayerJoinEvent event)
    {
        final User user = new User(event.getPlayer());
        final FileConfiguration config = FileManager.getConfig().getFileData();
        if (config.getBoolean(ConfigData.Values.HIDE_JOIN_QUIT.toString()))
        {
            event.setJoinMessage((String) null);
        }
        else if (config.getBoolean(ConfigData.Values.USE_JOIN_QUIT.toString()))
        {
            final Group group = user.getGroup();
            String joinMsg = group.getJoinMessage();
            joinMsg = joinMsg.replace("%player%", user.getPlayer().getDisplayName());
            event.setJoinMessage(joinMsg);
            if (config.getBoolean(ConfigData.Values.USE_JOIN_SOUND.toString()))
            {
                final String cfg = config.getString(ConfigData.Values.JOIN_SOUND.toString());
                final String[] soundOption = cfg.replace(" ", "").split(";");
                try
                {
                    final Sound sound = Sound.valueOf(soundOption[0]);
                    final float volume = (float) Integer.parseInt(soundOption[1]);
                    final float pitch = (float) Integer.parseInt(soundOption[2]);
                    if (soundOption.length == 3)
                    {
                        final String receiver = config.getString(ConfigData.Values.JOIN_QUIT_SOUND_RECEIVER.toString());
                        if (receiver.equals("all"))
                        {
                            for (final Player target : Bukkit.getOnlinePlayers())
                                target.playSound(target.getLocation(), sound, volume, pitch);
                        }
                        else
                        {
                            user.getPlayer().playSound(user.getPlayer().getLocation(), sound, volume, pitch);
                        }
                    }
                    else
                    {
                        Messages.log("&cCouldn't play sound on player join. Please check up the sound configuration.");
                    }
                }
                catch (IllegalArgumentException exception)
                {
                    Messages.log("&cCouldn't play sound '" + soundOption[0] + "'. Please use valid sounds!");
                }
            }
        }
    }
}
