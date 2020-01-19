package gloryrock.tinychatmanager.user;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.IOException;
import gloryrock.tinychatmanager.files.FileManager;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import java.util.UUID;

public class UserData
{
    private final UUID uniqueId;
    private File file;
    private FileConfiguration fileData;

    public UserData(final UUID uniqueId)
    {
        this.uniqueId = uniqueId;
        this.load();
    }

    private void load()
    {
        this.file = new File(FileManager.getPluginFolder() + "/user", this.uniqueId + ".yml");
        if (!this.file.exists())
        {
            try
            {
                this.file.createNewFile();
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }
        }
        this.fileData = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
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

    private void save()
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
}
