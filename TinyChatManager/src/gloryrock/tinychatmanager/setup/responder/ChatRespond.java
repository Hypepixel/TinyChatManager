package gloryrock.tinychatmanager.setup.responder;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.List;
import org.bukkit.event.HandlerList;
import gloryrock.tinychatmanager.messages.Messages;
import gloryrock.tinychatmanager.messages.Message;
import org.bukkit.event.Listener;
import gloryrock.tinychatmanager.TinyChatManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import java.util.function.Function;
import gloryrock.tinychatmanager.user.User;

public class ChatRespond
{
    private final ListenUp LISTENER;
    private final User RESPONDER;
    private final Function<String, String> ANSWER;
    private final String TEXT;
    private String textAllowedEntries;
    private BukkitTask bukkitTask;

    public ChatRespond(final User responder, final String text, final Function<String, String> function)
    {
        this.LISTENER = new ListenUp();
        this.RESPONDER = responder;
        this.TEXT = text;
        this.ANSWER = function;
        Bukkit.getPluginManager().registerEvents((Listener) this.LISTENER, TinyChatManager.getInstance().getPlugin());
        this.sendMessage();
    }

    public void setAllowedEntriesText(final String message)
    {
        this.textAllowedEntries = message;
    }

    private void exit()
    {
        this.bukkitTask.cancel();
        HandlerList.unregisterAll((Listener) this.LISTENER);
    }

    private void sendMessage()
    {
        this.RESPONDER.getPlayer().closeInventory();
        final List<String> messages = Messages.getList(Message.CHAT_HEADER);
        for (final String msg : messages)
        {
            this.RESPONDER.getPlayer()
                .sendMessage(msg.replace("%quit%", "quit").replace("%question%", this.TEXT.replace("%newline%", "\n")));
        }
    }

    private class ListenUp implements Listener
    {
        @EventHandler
        public void onChatEvent(final AsyncPlayerChatEvent event)
        {
            if (!event.getPlayer().equals(ChatRespond.this.RESPONDER.getPlayer()))
            {
                return;
            }
            if (event.getMessage().equals("quit") || event.getMessage().equals("cancel"))
            {
                ChatRespond.this.ANSWER.apply("cancelled");
                ChatRespond.this.exit();
            }
            else
            {
                final String respond = ChatRespond.this.ANSWER.apply(event.getMessage());
                switch (respond)
                {
                    case "correct":
                    case "cancel":
                    {
                        ChatRespond.this.exit();
                        break;
                    }
                    case "incorrect":
                    {
                        ChatRespond.this.RESPONDER.sendMessage(Messages.getText(Message.CHAT_INPUT_WRONGENTRY)
                            .replace("%allowed_inputs%", ChatRespond.this.textAllowedEntries)
                            .replace("%newline%", "\n"));
                        break;
                    }
                    case "error":
                    {
                        break;
                    }
                    default:
                    {
                        ChatRespond.this.sendMessage();
                        break;
                    }
                }
            }
            event.setCancelled(true);
        }
    }
}
