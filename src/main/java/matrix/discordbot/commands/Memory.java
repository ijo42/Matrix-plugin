package matrix.discordbot.commands;

import matrix.utils.ConfigTranslate;
import matrix.utils.SystemInfo;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class Memory {
    public static void main(MessageCreateEvent event) {
        TextChannel channel = event.getMessage().getChannel();
        if (channel == null)
            throw new NullPointerException();
        EmbedBuilder embedCreateSpec = new EmbedBuilder();
        embedCreateSpec.setTitle(ConfigTranslate.get("cmd.memory.title"));

        embedCreateSpec.setColor(
                new Color(
                        Integer.parseInt(ConfigTranslate.get("cmd.memory.color.red")),
                        Integer.parseInt(ConfigTranslate.get("cmd.memory.color.green")),
                        Integer.parseInt(ConfigTranslate.get("cmd.memory.color.blue"))
                ));
        embedCreateSpec.setDescription(ConfigTranslate.get("cmd.memory.description"));

        embedCreateSpec.addField(ConfigTranslate.get("cpu"), SystemInfo.cpu() + "%", true);
        embedCreateSpec.addField(ConfigTranslate.get("cpuServer"), SystemInfo.cpuProcess() + "%", true);
        embedCreateSpec.addField(ConfigTranslate.get("ram"), SystemInfo.ram() + "%", true);
        channel.sendMessage(embedCreateSpec);
    }
}
