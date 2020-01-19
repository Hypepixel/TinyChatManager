package gloryrock.tinychatmanager;

public enum CustomPermission
{
    ROOT("tinychatmanager.*"),
    ADMIN("tinychatmanager.admin"),
    COLOR("tinychatmanager.color"),
    COLOR_ALL("tinychatmanager.color.*"),
    GROUP("tinychatmanager.group"),
    SUBGROUP("tinychatmanager.subgroup");

    private final String myName;

    private CustomPermission(final String aName)
    {
        this.myName = aName;
    }

    public static CustomPermission[] getValues()
    {
        final CustomPermission[] permissions = new CustomPermission[values().length - 1];
        int i = 0;
        for (final CustomPermission customPermission : values())
        {
            if (customPermission != CustomPermission.ROOT)
            {
                permissions[i] = customPermission;
                ++i;
            }
        }
        return permissions;
    }

    @Override
    public String toString()
    {
        return this.myName;
    }
}
