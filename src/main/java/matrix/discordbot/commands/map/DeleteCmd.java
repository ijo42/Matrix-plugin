package matrix.discordbot.commands.map;

import matrix.discordbot.Bot;
import matrix.discordbot.commands.MainCmd;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import mindustry.Vars;
import mindustry.maps.Map;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Optional;

public class DeleteCmd extends MainCmd.Command {
    public static String name = "deleteMap";

    public static void main(MessageCreateEvent event) {
        TextChannel channel = event.getMessage().getChannel();
        Optional<Role> optRole = Bot.getRoleFromID(Config.get("stuffRoleID"));
        if (channel == null || !event.getMessageAuthor().asUser().isPresent() || !event.getServer().isPresent() || !optRole.isPresent())
            return;
        String message;

        if (!event.getMessageAuthor().asUser().get().getRoles(event.getServer().get())
                .contains(optRole.get())) {
            message = ConfigTranslate.get("noPerm");
        } else {
            String[] splitted = event.getMessageContent().split(" ", 2);
            if (splitted.length == 1) {
                message = ConfigTranslate.get("cmd.deleteMap.usage");
            } else {
                Map found = null;
                try {
                    splitted[ 1 ] = splitted[ 1 ].trim();
                    found = Vars.maps.customMaps().get(Integer.parseInt(splitted[ 1 ]) - 1);
                } catch (Exception e) {
                    for (Map m : Vars.maps.customMaps())
                        if (m.name().equals(splitted[ 1 ])) {
                            found = m;
                            break;
                        }
                }
                if (found == null) {
                    message = ConfigTranslate.get("cmd.deleteMap.404");
                    MapsCmd.main(event);
                } else {
                    Vars.maps.removeMap(found);
                    Vars.maps.reload();
                    message = ConfigTranslate.get("cmd.deleteMap.successful").replace("{0}", found.name());
                }
            }
        }
        channel.sendMessage(message);
    }
}
