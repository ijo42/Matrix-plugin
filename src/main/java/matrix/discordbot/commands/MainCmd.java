package matrix.discordbot.commands;

import matrix.discordbot.commands.map.*;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class MainCmd implements MessageCreateListener {

    public MainCmd(MessageCreateEvent messageCreateEvent) {
        onMessageCreate(messageCreateEvent);
    }

    public static String genFullCommand(String name) {
        return Config.get("prefix") + String.format(ConfigTranslate.get("cmd.%s.name"), name);
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.isPrivateMessage() || event.getMessageAuthor().isBotUser()) return;
        String msg = event.getMessage().getContent();
        if (msg.startsWith(genFullCommand(SetMsgChannel.name))) SetMsgChannel.main(event);
        else if (msg.startsWith(genFullCommand(ItemsCmd.name))) ItemsCmd.main(event);
        else if (msg.startsWith(genFullCommand(Memory.name))) Memory.main(event);
        else if (msg.startsWith(genFullCommand(MapsCmd.name))) MapsCmd.main(event);
        else if (msg.startsWith(genFullCommand(MapCmd.name))) MapCmd.main(event);
        else if (msg.startsWith(genFullCommand(ChangeCmd.name))) ChangeCmd.main(event);
        else if (msg.startsWith(genFullCommand(UploadCmd.name))) UploadCmd.main(event);
        else if (msg.startsWith(genFullCommand(DeleteCmd.name))) DeleteCmd.main(event);
        else if (msg.startsWith(genFullCommand(BanCmd.name))) BanCmd.main(event);
    }

    public abstract static class Command {
        public String name;

        public static void main(MessageCreateEvent event) {
        }
    }
}
