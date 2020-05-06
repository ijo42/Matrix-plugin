package matrix.discordbot.commands;

import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

public class HelpCmd extends MainCmd.Command {
    public static String name = "help";

    public static void main(MessageCreateEvent event) {
        TextChannel channel = event.getMessage().getChannel();
        if (channel == null)
            return;
        MessageBuilder message = new MessageBuilder();
        if (event.getMessageAuthor().asUser().isPresent())
            message.append(event.getMessageAuthor().asUser().get()).append(",").append("\n");
        message.append(ConfigTranslate.get("cmd.help.header")).append("\n");
        for (String com : MainCmd.getCommands())
            message.append(ConfigTranslate.get("cmd.help.entry").replace("{0}", com)).append("\n");
        message.append(ConfigTranslate.get("cmd.help.footer").replace("{0}", Config.get("prefix"))).append("\n");
        message.send(channel);
    }
}
