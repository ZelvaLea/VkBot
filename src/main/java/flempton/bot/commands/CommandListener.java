package flempton.bot.commands;

import flempton.bot.events.EventLabel;
import flempton.bot.events.EventPriority;
import flempton.bot.events.Listener;
import flempton.bot.events.messages.NewMessageEvent;
import flempton.bot.sdk.response.messages.Message;

public class CommandListener implements Listener {
    private static final int CMD_MASK = 1 << '/' | 1 << '!';

    private final CommandHandler handler;

    CommandListener(CommandHandler handler) {
        this.handler = handler;
    }

    @EventLabel(priority = EventPriority.HIGHEST)
    public void onMessage(NewMessageEvent event) {
        Message msg = event.getMessage();
        String text = msg.getText();
        if (text.length() > 1 &&
                (CMD_MASK >> (int) text.charAt(0) & 1) != 0) {
            String[] args = text.substring(1).split(" ");
            handler.getCommand(args[0]).ifPresent(cmd -> {
                int len = args.length;
                String[] args0 = new String[len-1];
                System.arraycopy(args, 1, args0, 0, len - 1);
                cmd.execute(args0, event);
            });
        }
    }
}
