package matrix.discordBot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import matrix.utils.ConfigTranslate;
import matrix.utils.SystemInfo;

import java.awt.*;

public class Memory {
    public static void main(MessageCreateEvent event) {
        MessageChannel channel = event.getMessage().getChannel().block();
        if (channel == null)
            throw new NullPointerException();
        channel.createEmbed(embedCreateSpec -> {
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
        }).block();
    }
}
