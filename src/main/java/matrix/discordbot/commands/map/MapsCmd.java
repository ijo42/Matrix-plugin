package matrix.discordbot.commands.map;

import matrix.discordbot.commands.MainCmd;
import matrix.utils.ChatGuard;
import matrix.utils.ConfigTranslate;
import mindustry.Vars;
import mindustry.maps.Map;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

public class MapsCmd extends MainCmd.Command {
    public static String name = "maps";

    public static void main(MessageCreateEvent event) {

        TextChannel channel = event.getMessage().getChannel();
        if (channel == null)
            return;
        EmbedBuilder embedCreateSpec = new EmbedBuilder();
        embedCreateSpec.setTitle(ConfigTranslate.get("cmd.maps.title"));

        for (Map m : Vars.maps.customMaps()) {
            embedCreateSpec.addField(ConfigTranslate.get("cmd.maps.entryKey")
                            .replace("{0}", ChatGuard.removeColors.apply(m.name())),
                    ConfigTranslate.get("cmd.maps.entryValue")
                            .replace("{0}", String.valueOf(m.width))
                            .replace("{1}", String.valueOf(m.width)), true);
        }

        embedCreateSpec.setFooter(ConfigTranslate.get("cmd.maps.footer").replace("{0}", String.valueOf(Vars.maps.customMaps().size)));
        channel.sendMessage(embedCreateSpec);
    }
}
