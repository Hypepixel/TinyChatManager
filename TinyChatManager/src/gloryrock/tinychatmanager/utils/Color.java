package gloryrock.tinychatmanager.utils;

import gloryrock.tinychatmanager.messages.Messages;
import gloryrock.tinychatmanager.messages.Message;

public enum Color
{
    BLACK("0", 15, Message.COLOR_BLACK),
    DARK_BLUE("1", 11, Message.COLOR_DARK_BLUE),
    DARK_GREEN("2", 13, Message.COLOR_DARK_GREEN),
    DARK_AQUA("3", 9, Message.COLOR_DARK_AQUA),
    DARK_RED("4", 14, Message.COLOR_DARK_RED),
    DARK_PURPLE("5", 10, Message.COLOR_PURPLE),
    GOLD("6", 1, Message.COLOR_GOLD),
    GRAY("7", 8, Message.COLOR_LIGHT_GRAY),
    DARK_GRAY("8", 7, Message.COLOR_GRAY),
    BLUE("9", 3, Message.COLOR_DARK_BLUE),
    GREEN("a", 5, Message.COLOR_DARK_GREEN),
    AQUA("b", 3, Message.COLOR_AQUA),
    RED("c", 6, Message.COLOR_RED),
    LIGHT_PURPLE("d", 2, Message.COLOR_MAGENTA),
    YELLOW("e", 4, Message.COLOR_YELLOW),
    WHITE("f", 0, Message.COLOR_WHITE),
    UNDEFINED("r", 0, (Message) null);

    private final String code;
    private final Message name;

    private Color(final String code, final int id, final Message name)
    {
        this.code = code;
        this.name = name;
    }

    public static Color[] getValues()
    {
        final Color[] colors = new Color[values().length - 1];
        int i = 0;
        for (final Color color : values())
        {
            if (color != Color.UNDEFINED)
            {
                colors[i] = color;
                ++i;
            }
        }
        return colors;
    }

    public static Color getByCode(final String code)
    {
        for (final Color color : values())
        {
            if (color.code.equals(code))
                return color;
        }
        return null;
    }

    @Override
    public String toString()
    {
        return this.getCode() + Messages.getText(this.name);
    }

    public String getCode()
    {
        return "§" + this.code;
    }
}
