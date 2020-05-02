package matrix.discordbot.commands.map;

import matrix.utils.ChatGuard;
import matrix.utils.ConfigTranslate;
import mindustry.Vars;
import mindustry.core.GameState;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;

public class MapCmd {
    public static void main(MessageCreateEvent event) {
        if (!Vars.state.is(GameState.State.playing)) {
            return;
        }
        TextChannel channel = event.getMessage().getChannel();
        if (channel == null)
            return;
        EmbedBuilder embedCreateSpec = new EmbedBuilder();
            embedCreateSpec.setTitle(ConfigTranslate.get("cmd.map.title"));

        embedCreateSpec.setColor(
                new Color(
                        Integer.parseInt(ConfigTranslate.get("cmd.map.color.red")),
                        Integer.parseInt(ConfigTranslate.get("cmd.map.color.green")),
                        Integer.parseInt(ConfigTranslate.get("cmd.map.color.blue"))
                ));

        embedCreateSpec.setDescription(ConfigTranslate.get("cmd.map.description"));

        embedCreateSpec.addField(ConfigTranslate.get("cmd.map"), ChatGuard.removeColors.apply(Vars.world.getMap().name()), true);
        embedCreateSpec.addField(ConfigTranslate.get("cmd.map.author"), ChatGuard.removeColors.apply(Vars.world.getMap().author()), true);
        embedCreateSpec.addField(ConfigTranslate.get("cmd.map.wave"), String.valueOf(Vars.state.wave), true);
        embedCreateSpec.addField(ConfigTranslate.get("cmd.map.enemies"), String.valueOf(Vars.state.enemies), true);
        embedCreateSpec.addField(ConfigTranslate.get("cmd.map.online"), String.valueOf(Vars.playerGroup.size()), true);
        embedCreateSpec.addField(ConfigTranslate.get("cmd.map.admins"), String.valueOf(Vars.playerGroup.all().count(p -> p.isAdmin)), true);
        channel.sendMessage(embedCreateSpec);
    }
}
