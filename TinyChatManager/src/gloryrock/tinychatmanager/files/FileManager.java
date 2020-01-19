package gloryrock.tinychatmanager.files;

import java.io.File;

public class FileManager
{
    private static ConfigData configData;
    private static GroupsData groupsData;

    public static void load()
    {
        final File userFolder = new File(getPluginFolder() + "/user");
        if (!userFolder.exists())
            userFolder.mkdirs();

        FileManager.configData = new ConfigData().load();
        FileManager.groupsData = new GroupsData().load();
    }

    public static ConfigData getConfig()
    {
        return FileManager.configData;
    }

    public static GroupsData getGroups()
    {
        return FileManager.groupsData;
    }

    public static File getPluginFolder()
    {
        return new File("plugins/TinyChatManager");
    }
}
