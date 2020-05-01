package matrix.discordBot.commands.map;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import matrix.utils.ConfigTranslate;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.maps.Map;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;

public class MapChangeCmd {
    public static void main(MessageCreateEvent event) {
        TextChannel channel = event.getMessage().getChannel();
        if (channel == null)
            return;
        String[] splitted = event.getMessage().getContent().split(" ", 2);
        String message;
        if (splitted.length == 1) {
            message = ConfigTranslate.get("cmd.changeMap.params");
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
        channel.sendMessage(message);
    }
}
