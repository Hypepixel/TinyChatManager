package gloryrock.tinychatmanager;

import gloryrock.tinychatmanager.events.QuitEvent;
import gloryrock.tinychatmanager.events.JoinListener;
import org.bukkit.event.Listener;
import gloryrock.tinychatmanager.events.ChatListener;
import java.util.Set;
import gloryrock.tinychatmanager.files.GroupsData;
import java.io.File;
import gloryrock.tinychatmanager.utils.RainbowEffect;
import org.bukkit.entity.Player;
import java.sql.SQLException;
import org.bukkit.command.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import gloryrock.tinychatmanager.commands.CommandTabCompletion;
import gloryrock.tinychatmanager.commands.CustomCommand;
import org.bukkit.command.CommandExecutor;
import gloryrock.tinychatmanager.commands.CommandKit;
import gloryrock.tinychatmanager.groups.GroupHandler;
import gloryrock.tinychatmanager.files.ConfigData;
import gloryrock.tinychatmanager.messages.Messages;
import gloryrock.tinychatmanager.files.FileManager;
import gloryrock.tinychatmanager.user.User;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TinyChatManager extends JavaPlugin
{
    private static TinyChatManager instance;
    private Plugin plugin;
    private Database database;

    public static TinyChatManager getInstance()
    {
        return TinyChatManager.instance;
    }

    public void onEnable()
    {
        TinyChatManager.instance = this;
        this.plugin = (Plugin) this;
        FileManager.load();
        Messages.load();

        if (FileManager.getConfig().getFileData().getBoolean(ConfigData.Values.USE_SQL.toString()))
            this.database = new Database();

        GroupHandler.load();
        final ConfigData cfg = FileManager.getConfig();
        final String configVersion = cfg.getFileData().getString("config.version");
        if (configVersion == null)
            this.update();

        final PluginCommand rootCommand = this.getCommand(CustomCommand.ROOT.toString());
        if (rootCommand != null)
        {
            rootCommand.setExecutor((CommandExecutor) new CommandKit());
            rootCommand.setTabCompleter((TabCompleter) new CommandTabCompletion());
        }

        this.registerEvents();
        if (!cfg.getFileData().getBoolean("config.enabled"))
        {
            Bukkit.getServer().getConsoleSender()
                .sendMessage(Messages.getPrefix() + "§cPlugin has been disabled! §7Please enable it in \"config.yml\"");
            Bukkit.getPluginManager().disablePlugin((Plugin) this);
        }
    }

    public void onDisable()
    {
        if (this.getDatabase() != null)
        {
            try
            {
                if (!this.getDatabase().getConnection().isClosed())
                    this.getDatabase().getConnection().close();
            }
            catch (SQLException exception)
            {
                exception.printStackTrace();
            }
        }
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            if (player != null)
                player.closeInventory();
        }
    }

    public void reload()
    {
        FileManager.load();
        Messages.load();
        GroupHandler.load();
        User.getUsers().clear();
        RainbowEffect.getRainbowColors().clear();
    }

    private void update()
    {
        Bukkit.getServer().getConsoleSender()
            .sendMessage(Messages.getPrefix() + "§cYour configuration is not up to date!");
        Bukkit.getServer().getConsoleSender().sendMessage(Messages.getPrefix() + "§cUpdating files...");
        final ConfigData config = FileManager.getConfig();
        final GroupsData groups = FileManager.getGroups();
        try
        {
            final Set<String> oldGroups = (Set<String>) config.getFileData().getConfigurationSection("config.prefix")
                .getKeys(false);
            for (final String name : oldGroups)
            {
                groups.getFileData().set("groups." + name + ".prefix",
                    (Object) config.getFileData().getString("config.prefix." + name + ".prefix"));
                groups.getFileData().set("groups." + name + ".suffix",
                    (Object) config.getFileData().getString("config.prefix." + name + ".suffix"));
                groups.getFileData().set("groups." + name + ".chat-color",
                    (Object) config.getFileData().getString("config.prefix." + name + ".chatcolor"));
            }
            final File file = new File(this.getDataFolder(), "config.yml");
            file.renameTo(new File(this.getDataFolder(), "old-config.yml"));
            groups.save();
        }
        catch (Exception ex)
        {
        }
        config.getFileData().set("config.prefix", (Object) null);
        config.getFileData().set("config.enabled", (Object) true);
        config.save();
        Bukkit.getServer().getConsoleSender().sendMessage(Messages.getPrefix() + "§bFiles have been updated!");
    }

    private void registerEvents()
    {
        this.getServer().getPluginManager().registerEvents((Listener) new ChatListener(), (Plugin) this);
        this.getServer().getPluginManager().registerEvents((Listener) new JoinListener(), (Plugin) this);
        this.getServer().getPluginManager().registerEvents((Listener) new QuitEvent(), (Plugin) this);
    }

    public Database getDatabase()
    {
        return this.database;
    }

    public Plugin getPlugin()
    {
        return this.plugin;
    }
}