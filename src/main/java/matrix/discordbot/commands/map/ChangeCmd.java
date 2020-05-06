package matrix.discordbot.commands.map;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import matrix.discordbot.Bot;
import matrix.discordbot.commands.MainCmd;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.maps.Map;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Optional;

public class ChangeCmd extends MainCmd.Command {
    public static String name = "changeMap";

    private static long lastMapChange = 0L;

    public static void main(MessageCreateEvent event) {
        TextChannel channel = event.getMessage().getChannel();
        Optional<Role> optRole = Bot.getRoleFromID(Config.get("activePlayerRoleID"));
        if (channel == null || !event.getMessageAuthor().asUser().isPresent() || !event.getServer().isPresent() || !optRole.isPresent())
            return;

        if (!event.getMessageAuthor().asUser().get().getRoles(event.getServer().get())
                .contains(optRole.get())) {
            channel.sendMessage(ConfigTranslate.get("noPerm"));
            return;
        }
        long minMapChangeTime = Long.parseLong(Config.get("minMapChangeTime"));

        if (System.currentTimeMillis() / 1000L - lastMapChange < minMapChangeTime) {
            event.getChannel().sendMessage(ConfigTranslate.get("cooldown").replace("{0}", String.valueOf(minMapChangeTime)));
            return;
        }

        String[] splitted = event.getMessage().getContent().split(" ", 2);
        String message;
        if (splitted.length == 1) {
            message = ConfigTranslate.get("cmd.changeMap.usage");
        } else {
            Map found;
            try {
                splitted[ 1 ] = splitted[ 1 ].trim();
                found = Vars.maps.customMaps().get(Integer.parseInt(splitted[ 1 ]) - 1);
            } catch (Exception e) {
                found = Vars.maps.byName(splitted[ 1 ]);
            }
            if (found == null) {
                message = ConfigTranslate.get("cmd.changeMap.404");
                MapsCmd.main(event);
            } else {

                Fi temp = Core.settings.getDataDirectory().child("maps").child("temp");
                if (!temp.mkdirs())
                    message = ConfigTranslate.get("cmd.changeMap.error");
                else {
                    for (Map m1 : Vars.maps.customMaps()) {
                        if (m1.equals(Vars.world.getMap())) continue;
                        if (m1.equals(found)) continue;
                        m1.file.moveTo(temp);
                    }

                    Vars.maps.reload();
                    Events.fire(new EventType.GameOverEvent(Team.crux));
                    Vars.maps.reload();
                    Fi mapsDir = Core.settings.getDataDirectory().child("maps");
                    for (Fi fh : temp.list()) {
                        fh.moveTo(mapsDir);
                    }
                    temp.deleteDirectory();
                    Vars.maps.reload();

                    message = ConfigTranslate.get("cmd.changeMap.successful").replace("{0}", found.name());
                }
            }
        }
        lastMapChange = System.currentTimeMillis() / 1000L;
        channel.sendMessage(message);
    }
}
