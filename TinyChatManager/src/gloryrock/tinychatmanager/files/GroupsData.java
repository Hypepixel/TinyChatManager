package gloryrock.tinychatmanager.files;

import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import gloryrock.tinychatmanager.TinyChatManager;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;

public class GroupsData
{
    private File file;
    private FileConfiguration fileData;

    public GroupsData load()
    {
        this.file = new File(FileManager.getPluginFolder(), "groups.yml");
        if (!this.file.exists())
            TinyChatManager.getInstance().getPlugin().saveResource("groups.yml", true);

        this.fileData = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
        if (this.fileData.getConfigurationSection("groups") == null)
        {
            final File old = new File("plugins/TinyChatManager", "groups.yml");
            final File backup = new File("plugins/TinyChatManager", "backup-groups.yml");
            if (old.renameTo(backup) && old.delete())
                this.load();
        }
        return this;
    }

    public void save()
    {
        try
        {
            this.fileData.save(this.file);
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
        this.load();
    }

    public void set(final String key, final Object value)
    {
        this.getFileData().set(key, value);
        this.save();
    }

    public FileConfiguration getFileData()
    {
        return this.fileData;
    }
}
