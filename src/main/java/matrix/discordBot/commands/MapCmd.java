package matrix.discordBot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import matrix.utils.ConfigTranslate;
import mindustry.Vars;
import mindustry.core.GameState;

import java.awt.*;

public class MapCmd {
    public static void main(MessageCreateEvent event) {

        if (!Vars.state.is(GameState.State.playing)) {
            return;
        }
        MessageChannel channel = event.getMessage().getChannel().block();
        if (channel == null)
            return;
        channel.createEmbed(embedCreateSpec -> {
            embedCreateSpec.setTitle(ConfigTranslate.get("cmd.map.title"));

            embedCreateSpec.setColor(
                    new Color(
                            Integer.parseInt(ConfigTranslate.get("cmd.map.color.red")),
                            Integer.parseInt(ConfigTranslate.get("cmd.map.color.green")),
                            Integer.parseInt(ConfigTranslate.get("cmd.map.color.blue"))
                    ));

            embedCreateSpec.setDescription(ConfigTranslate.get("cmd.map.description"));

            embedCreateSpec.addField(ConfigTranslate.get("cmd.map"), Vars.world.getMap().name(), true);
            embedCreateSpec.addField(ConfigTranslate.get("cmd.map.author"), Vars.world.getMap().author(), true);
            embedCreateSpec.addField("Волна: ", String.valueOf(Vars.state.wave), true);
            embedCreateSpec.addField("Врагов: ", String.valueOf(Vars.state.enemies), true);
            embedCreateSpec.addField("Игроков: ", String.valueOf(Vars.playerGroup.size()), true);
            embedCreateSpec.addField("Админов онлайн: ", String.valueOf(Vars.playerGroup.all().count(p -> p.isAdmin)), true);

        }).block();
    }
}
