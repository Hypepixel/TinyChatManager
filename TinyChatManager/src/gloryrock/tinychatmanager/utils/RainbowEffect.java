package gloryrock.tinychatmanager.utils;

import java.util.List;
import gloryrock.tinychatmanager.files.ConfigData;
import gloryrock.tinychatmanager.files.FileManager;
import java.util.Random;
import org.bukkit.ChatColor;
import java.util.ArrayList;

public class RainbowEffect
{
    private static ArrayList<ChatColor> rainbowColors;

    public static String addRainbowEffect(final String text)
    {
        final String[] letters = text.split("(?<!^)");
        final StringBuilder rainbow = new StringBuilder();
        ChatColor last = ChatColor.WHITE;
        for (final String letter : letters)
        {
            ChatColor color;
            for (color = getRandomColor(); color.equals((Object) last); color = getRandomColor())
            {
            }
            rainbow.append(color.toString()).append(letter);
            last = color;
        }
        return rainbow.toString();
    }

    private static ChatColor getRandomColor()
    {
        final ArrayList<ChatColor> colors = getRainbowColors();
        final Random rand = new Random();
        return colors.get(rand.nextInt(colors.size()));
    }

    public static ArrayList<ChatColor> getRainbowColors()
    {
        if (RainbowEffect.rainbowColors == null || RainbowEffect.rainbowColors.isEmpty())
        {
            final ArrayList<ChatColor> enabledColors = new ArrayList<ChatColor>();
            final ConfigData configData = FileManager.getConfig();
            final List<String> colors = (List<String>) configData.getFileData().getStringList(ConfigData.Values.COLOR_RAINBOW_COLORS.toString());
            for (final String color : colors)
            {
                try
                {
                    enabledColors.add(ChatColor.valueOf(color));
                }
                catch (Exception exception)
                {
                }
            }
            RainbowEffect.rainbowColors = enabledColors;
        }
        return RainbowEffect.rainbowColors;
    }

    static
    {
        RainbowEffect.rainbowColors = null;
    }
}
