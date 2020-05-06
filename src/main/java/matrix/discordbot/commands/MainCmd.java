package matrix.discordbot.commands;

import matrix.discordbot.Bot;
import matrix.discordbot.commands.map.*;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.LinkedList;
import java.util.List;

public class MainCmd implements MessageCreateListener {

    public MainCmd(MessageCreateEvent messageCreateEvent) {
        onMessageCreate(messageCreateEvent);
    }

    private static List<String> commands = new LinkedList<>();

    public static List<String> getCommands() {
        if (commands.isEmpty()) {
            commands.add(SetMsgChannel.name);
            commands.add(ItemsCmd.name);
            commands.add(Memory.name);
            commands.add(MapsCmd.name);
            commands.add(MapCmd.name);
            commands.add(ChangeCmd.name);
            commands.add(UploadCmd.name);
            commands.add(DeleteCmd.name);
            commands.add(UploadCmd.name);
            commands.add(BanCmd.name);
        }
        return commands;
    }

    public static String genFullCommand(String name) {
        return Config.get("prefix") + ConfigTranslate.get("cmd." + name + ".name");
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.isPrivateMessage() || !event.getMessageAuthor().isRegularUser()) return;
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

        if (msg.startsWith(genFullCommand(HelpCmd.name)) || msg.contains(Bot.getMentionTag())) HelpCmd.main(event);
    }

    public abstract static class Command {
        public String name;

        public static void main(MessageCreateEvent event) {
        }
    }
}
