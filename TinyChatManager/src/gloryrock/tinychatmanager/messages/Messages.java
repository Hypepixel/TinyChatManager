package gloryrock.tinychatmanager.messages;

import java.util.Arrays;
import org.bukkit.ChatColor;
import gloryrock.tinychatmanager.user.User;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import org.bukkit.plugin.Plugin;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import org.bukkit.Bukkit;
import gloryrock.tinychatmanager.files.ConfigData;
import gloryrock.tinychatmanager.files.FileManager;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages
{
    private static FileConfiguration data;
    private static List<String> languages;
    private static String language;

    public static String getLanguage()
    {
        return Messages.language;
    }

    public static void setLanguage(final String lang)
    {
        if (Messages.languages.contains(lang))
        {
            Messages.language = lang;
            FileManager.getConfig().set(ConfigData.Values.LANG.toString(), lang);
            load();
        }
    }

    public static String langToName()
    {
        final String language = Messages.language;
        switch (language)
        {
            case "de_DE":
            {
                return "Deutsch";
            }
            case "it_IT":
            {
                return "Italiano";
            }
            default:
            {
                return "English";
            }
        }
    }

    public static void load()
    {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("TinyChatManager");
        final ConfigData config = FileManager.getConfig();
        Messages.language = config.getFileData().getString("config.lang");
        final String path = "plugins/TinyChatManager";
        if (Messages.language == null || !Messages.languages.contains(Messages.language))
        {
            Messages.language = "en_US";
            config.getFileData().set("config.lang", (Object) "en_US");
            config.save();
        }
        final File file = new File(path, Messages.language + ".yml");
        if (!file.exists())
        {
            plugin.saveResource(Messages.language + ".yml", false);
        }
        Messages.data = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
        for (final Message type : Message.values())
        {
            final Object message = Messages.data.get(type.toString());
            if (message == null)
            {
                file.renameTo(new File(path, "old-" + Messages.language + ".yml"));
                plugin.saveResource(Messages.language + ".yml", false);
                final File temp = new File(path, "temp-" + Messages.language + ".yml");
                new File(path, Messages.language + ".yml").renameTo(temp);
                final File old = new File(path, "old-" + Messages.language + ".yml");
                old.renameTo(new File(path, Messages.language + ".yml"));
                final FileConfiguration tempData = (FileConfiguration) YamlConfiguration.loadConfiguration(temp);
                final Object text = tempData.get(type.toString());
                Messages.data.set(type.toString(), text);
                temp.delete();
                old.delete();
            }
        }
        try
        {
            Messages.data.save(file);
        }
        catch (IOException ex)
        {
        }
    }

    public static List<String> getList(final Message message)
    {
        final List<String> temp = new ArrayList<String>();
        for (final String msg : Messages.data.getStringList(message.toString()))
        {
            temp.add(translate(msg));
        }
        return temp;
    }

    @Nullable
    public static String getText(@Nonnull final String path)
    {
        return translate(Messages.data.getString(path));
    }

    public static String getText(final Message message)
    {
        if (message != null)
        {
            return translate(Messages.data.getString(message.toString()));
        }
        return null;
    }

    public static String getText(final Message message, final User user)
    {
        String text = "";
        if (message != null)
        {
            text = translate(Messages.data.getString(message.toString()));
        }
        return text;
    }

    public static String getMessage(final Message message)
    {
        return getPrefix() + translate(Messages.data.getString(message.toString()));
    }

    public static String getMessage(final Message message, final User user)
    {
        final String text = translate(Messages.data.getString(message.toString()));
        return getPrefix() + text;
    }

    public static void log(final String message)
    {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + translate(message));
    }

    public static String getPrefix()
    {
        return "§7[§bTinyChatManager§7] ";
    }

    private static String translate(final String text)
    {
        return (text != null) ? ChatColor.translateAlternateColorCodes('&', text) : null;
    }

    static
    {
        Messages.languages = Arrays.asList("en_US", "nl_NL");
    }
}
