package gloryrock.tinychatmanager.commands;

public enum CustomCommand
{
    ROOT("tinychatmanager", "", "Display information about tinychatmanager commands."),
    RELOAD("reload", "tinychatmanager.commands.reload", "Reload configs."),
    DEBUG("debug", "tinychatmanager.commands.debug", "Debug configs."),
    SET("set", "tinychatmanager.commands.set", "Set user specific prefixes, suffixes, groups, and colors."),
    USER("user", "tinychatmanager.commands.user", "Set user groups, subgroups, and display other commands."),
    USER_UPDATE("update", "tinychatmanager.commands.user.update", "Update a user's information."),
    USER_INFO("info", "tinychatmanager.commands.user.info", "Display a user's information."),
    USER_SETGROUP("setgroup", "tinychatmanager.commands.user.setgroup", "Set a user's group."),
    USER_SETSUBGROUP("setsubgroup", "tinychatmanager.commands.user.setsubgroup", "Set a user's subgroup."),
    DATABASE("database", "tinychatmanager.commands.database", "Display information about database commands."),
    DATABASE_MIGRATE("migrate", "tinychatmanager.commands.database.migrate", "Migrate the user database.");

    private final String myName;
    private final String myPermission;
    private final String myDescription;

    private CustomCommand(final String aName, final String aPermission, final String aDescription)
    {
        this.myName = aName;
        this.myPermission = aPermission;
        this.myDescription = aDescription;
    }

    public static CustomCommand[] getValues()
    {
        final CustomCommand[] commands = new CustomCommand[values().length - 1];
        int i = 0;
        for (final CustomCommand customCommand : values())
        {
            if (customCommand != CustomCommand.ROOT)
            {
                commands[i] = customCommand;
                ++i;
            }
        }
        return commands;
    }

    @Override
    public String toString()
    {
        return this.myName;
    }

    public String getPermission()
    {
        return this.myPermission;
    }

    public String getDescription()
    {
        return this.myDescription;
    }
}
