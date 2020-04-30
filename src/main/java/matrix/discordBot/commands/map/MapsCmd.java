package matrix.discordBot.commands.map;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import matrix.utils.ConfigTranslate;
import mindustry.Vars;
import mindustry.maps.Map;

public class MapsCmd {
    public static void main(MessageCreateEvent event) {

        MessageChannel channel = event.getMessage().getChannel().block();
        if (channel == null)
            return;
        channel.createEmbed(embedCreateSpec -> {
            embedCreateSpec.setTitle(ConfigTranslate.get("cmd.maps.title"));

            for (Map m : Vars.maps.customMaps()) {
                embedCreateSpec.addField(ConfigTranslate.get("cmd.maps.entryKey")
                        .replace("{0}", m.name()), ConfigTranslate.get("cmd.maps.entryValue")
                        .replace("{1}", String.valueOf(m.width))
                        .replace("{1}", String.valueOf(m.width)), true);
            }

            embedCreateSpec.setFooter(ConfigTranslate.get("cmd.maps.footer"), null);
        }).block();
    }
}
