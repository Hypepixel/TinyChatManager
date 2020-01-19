package gloryrock.tinychatmanager.files;

import java.util.Arrays;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import gloryrock.tinychatmanager.TinyChatManager;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;

public class ConfigData
{
    private File file;
    private FileConfiguration fileData;

    public ConfigData load()
    {
        this.file = new File(FileManager.getPluginFolder(), "config.yml");
        if (!this.file.exists())
            TinyChatManager.getInstance().getPlugin().saveResource("config.yml", true);

        this.fileData = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
        this.fileData.options().copyDefaults(true);
        this.check();
        return this;
    }

    public void save()
    {
        try
        {
            this.fileData.options().copyDefaults(true);
            this.fileData.save(this.file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        this.load();
    }

    public FileConfiguration getFileData()
    {
        return this.fileData;
    }

    public void set(final String path, final Object value)
    {
        this.getFileData().set(path, value);
        this.save();
    }

    private void set(final Values path, final Object value)
    {
        this.getFileData().set(path.toString(), value);
        this.save();
    }

    private Object get(final Values path)
    {
        return this.getFileData().get(path.toString());
    }

    private void check()
    {
        if (this.getFileData().get("config.prefix") != null)
            this.set("config.prefix", null);

        if (this.get(Values.ENABLED) == null || !(this.get(Values.ENABLED) instanceof Boolean))
            this.set(Values.ENABLED, true);

        if (this.get(Values.LANG) == null)
            this.set(Values.LANG, "en_US");
        
        if (this.get(Values.USE_SQL) == null || !(this.get(Values.USE_SQL) instanceof Boolean))
            this.set(Values.USE_SQL, false);
        
        if (this.get(Values.SQL_HOST) == null)
            this.set(Values.SQL_HOST, "localhost");
        
        if (this.get(Values.SQL_PORT) == null)
            this.set(Values.SQL_PORT, "3306");
        
        if (this.get(Values.SQL_DATABASE) == null)
            this.set(Values.SQL_DATABASE, "TinyChatManager");
        
        if (this.get(Values.SQL_USERNAME) == null)
            this.set(Values.SQL_USERNAME, "username");
        
        if (this.get(Values.SQL_PASSWORD) == null)
            this.set(Values.SQL_PASSWORD, "password");
        
        if (this.get(Values.SQL_TABLE_PREFIX) == null)
            this.set(Values.SQL_TABLE_PREFIX, "tcm");
        
        if (this.get(Values.USE_SUBGROUPS) == null || !(this.get(Values.USE_SUBGROUPS) instanceof Boolean))
            this.set(Values.USE_SUBGROUPS, true);
        
        if (this.get(Values.DUPLICATE_WHITE_SPACES) == null || !(this.get(Values.DUPLICATE_WHITE_SPACES) instanceof Boolean))
            this.set(Values.DUPLICATE_WHITE_SPACES, false);

        if (this.get(Values.COLOR_RAINBOW_COLORS) == null)
            this.set(Values.COLOR_RAINBOW_COLORS,
                Arrays.asList("YELLOW", "BLUE", "LIGHT_PURPLE", "RED", "DARK_AQUA", "GOLD", "GREEN", "DARK_PURPLE"));

        if (this.get(Values.USE_JOIN_QUIT) == null || !(this.get(Values.USE_JOIN_QUIT) instanceof Boolean))
            this.set(Values.USE_JOIN_QUIT, true);

        if (this.get(Values.HIDE_JOIN_QUIT) == null || !(this.get(Values.HIDE_JOIN_QUIT) instanceof Boolean))
            this.set(Values.HIDE_JOIN_QUIT, false);
        
        if (this.get(Values.JOIN_QUIT_SOUND_RECEIVER) == null || (!this.get(Values.JOIN_QUIT_SOUND_RECEIVER).equals("all") && !this.get(Values.JOIN_QUIT_SOUND_RECEIVER).equals("player")))
            this.set(Values.JOIN_QUIT_SOUND_RECEIVER, "all");
        
        if (this.get(Values.USE_JOIN_SOUND) == null || !(this.get(Values.USE_JOIN_SOUND) instanceof Boolean))
            this.set(Values.USE_JOIN_SOUND, true);

        if (this.get(Values.JOIN_SOUND) == null)
            this.set(Values.JOIN_SOUND, "BLOCK_BEEHIVE_ENTER; 20; 1");

        if (this.get(Values.USE_QUIT_SOUND) == null || !(this.get(Values.USE_QUIT_SOUND) instanceof Boolean))
            this.set(Values.USE_QUIT_SOUND, true);

        if (this.get(Values.QUIT_SOUND) == null)
            this.set(Values.QUIT_SOUND, "BLOCK_BEEHIVE_EXIT; 18; 1");
    }

    public enum Values
    {
        ENABLED("config.enabled"),
        LANG("config.lang"),
        USE_SQL("config.sql.enabled"),
        SQL_HOST("config.sql.host"),
        SQL_PORT("config.sql.port"),
        SQL_DATABASE("config.sql.database"),
        SQL_USERNAME("config.sql.username"),
        SQL_PASSWORD("config.sql.password"),
        SQL_TABLE_PREFIX("config.sql.table-prefix"),
        USE_SUBGROUPS("config.subgroups.enabled"),
        DUPLICATE_WHITE_SPACES("config.chat.duplicate-white-spaces"),
        COLOR_RAINBOW_COLORS("config.chat.color.rainbow.colors"),
        USE_JOIN_QUIT("config.join-quit-messages.enabled"),
        HIDE_JOIN_QUIT("config.join-quit-messages.hide-messages"),
        JOIN_QUIT_SOUND_RECEIVER("config.join-quit-messages.sound.receiver"),
        USE_JOIN_SOUND("config.join-quit-messages.sound.join.enabled"),
        JOIN_SOUND("config.join-quit-messages.sound.join.sound"),
        USE_QUIT_SOUND("config.join-quit-messages.sound.quit.enabled"),
        QUIT_SOUND("config.join-quit-messages.sound.quit.sound");

        private final String KEY;

        private Values(final String key)
        {
            this.KEY = key;
        }

        @Override
        public String toString()
        {
            return this.KEY;
        }
    }
}
