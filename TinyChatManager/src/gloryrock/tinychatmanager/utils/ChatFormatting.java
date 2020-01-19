package gloryrock.tinychatmanager.utils;

import gloryrock.tinychatmanager.messages.Messages;
import gloryrock.tinychatmanager.messages.Message;

public enum ChatFormatting
{
    BOLD("l", Message.FORMATTING_BOLD),
    UNDERLINE("n", Message.FORMATTING_UNDERLINE),
    RAINBOW("r", Message.FORMATTING_RAINBOW),
    ITALIC("o", Message.FORMATTING_ITALIC),
    STRIKETHROUGH("m", Message.FORMATTING_STRIKETHROUGH),
    UNDEFINED("r", (Message) null);

    private final String code;
    private final Message name;

    private ChatFormatting(final String code, final Message name)
    {
        this.code = code;
        this.name = name;
    }

    public static ChatFormatting getByCode(final String code)
    {
        for (final ChatFormatting formatting : values())
        {
            if (formatting.code.equals(code))
            {
                return formatting;
            }
        }
        return null;
    }

    public static ChatFormatting[] getValues()
    {
        final ChatFormatting[] formattings = new ChatFormatting[values().length - 1];
        int i = 0;
        for (final ChatFormatting formatting : values())
        {
            if (formatting != ChatFormatting.UNDEFINED)
            {
                formattings[i] = formatting;
                ++i;
            }
        }
        return formattings;
    }

    @Override
    public String toString()
    {
        if (this.code.equals("r"))
            return this.getCode() + RainbowEffect.addRainbowEffect(Messages.getText(this.name));

        return this.getCode() + Messages.getText(this.name);
    }

    public String getCode()
    {
        return "§" + this.code;
    }
}
