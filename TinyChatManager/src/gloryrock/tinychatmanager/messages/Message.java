package gloryrock.tinychatmanager.messages;

public enum Message
{
    NO_PERMS("info.noperms"),
    PLAYER_NOT_FOUND("info.playerNotFound"),
    GROUP_NOT_FOUND("info.groupNotFound"),
    SUCCESS("info.success"),
    GROUP_EXISTS("info.groupExists"),
    GROUP_CREATED("info.groupCreated"),
    SET_PREFIX("chat.prefix"),
    RELOAD_COMPLETE("info.reloadComplete"),
    PLAYER_ONLY("info.playerOnly"),
    CHAT_HEADER("chat.header"),
    CHAT_GROUP("chat.group"),
    CHAT_INPUT_SUFFIX("chat.suffix"),
    CHAT_INPUT_PREFIX("chat.prefix"),
    CHAT_INPUT_WRONGENTRY("chat.wrongEntry"),
    COLOR_BLACK("colors.black"),
    COLOR_DARK_BLUE("colors.blue"),
    COLOR_DARK_GREEN("colors.green"),
    COLOR_DARK_AQUA("colors.cyan"),
    COLOR_DARK_RED("colors.darkred"),
    COLOR_PURPLE("colors.purple"),
    COLOR_GOLD("colors.gold"),
    COLOR_LIGHT_GRAY("colors.lightgray"),
    COLOR_GRAY("colors.gray"),
    COLOR_LIGHT_BLUE("colors.lightblue"),
    COLOR_LIME("colors.lime"),
    COLOR_AQUA("colors.aqua"),
    COLOR_RED("colors.red"),
    COLOR_MAGENTA("colors.magenta"),
    COLOR_YELLOW("colors.yellow"),
    COLOR_WHITE("colors.white"),
    FORMATTING_BOLD("colors.formattings.bold"),
    FORMATTING_ITALIC("colors.formattings.italic"),
    FORMATTING_STRIKETHROUGH("colors.formattings.strikethrough"),
    FORMATTING_UNDERLINE("colors.formattings.underline"),
    FORMATTING_RAINBOW("colors.formattings.rainbow"),
    ENABLED("info.enabled"),
    DISABLED("info.disabled");

    private final String message;

    private Message(final String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return this.message;
    }
}
