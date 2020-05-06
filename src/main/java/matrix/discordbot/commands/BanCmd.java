package matrix.discordbot.commands;

import arc.util.Strings;
import matrix.discordbot.Bot;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import mindustry.Vars;
import mindustry.entities.type.Player;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Optional;

public class BanCmd {
    public static void main(MessageCreateEvent event) {
        TextChannel channel = event.getMessage().getChannel();
        Optional<Role> optRole = Bot.getRoleFromID(Config.get("stuffRoleID"));
        if (event.isPrivateMessage() || channel == null || !event.getMessageAuthor().asUser().isPresent() || !event.getServer().isPresent() || !optRole.isPresent())
            return;

        if (!event.getMessageAuthor().asUser().get().getRoles(event.getServer().get())
                .contains(optRole.get())) {
            channel.sendMessage(ConfigTranslate.get("noPerm"));
            return;
        }
        String[] splitted = event.getMessage().getContent().split(" ", 2);
        String message;
        if (splitted.length == 1) {
            message = ConfigTranslate.get("cmd.banCmd.usage");
        } else {
            String badBoy = splitted[ 1 ];
            Player found = null;
            if (badBoy.length() > 1 && badBoy.startsWith("#") && Strings.canParseInt(badBoy.substring(1))) {
                int id = Strings.parseInt(badBoy.substring(1));
                for (Player p : Vars.playerGroup.all())
                    if (p.id == id) {
                        found = p;
                        break;
                    }
            } else
                for (Player p : Vars.playerGroup.all())
                    if (p.name.equalsIgnoreCase(badBoy)) {
                        found = p;
                        break;
                    }
            if (found != null) {
                if (Vars.netServer.admins.banPlayer(found.uuid))
                    message = ConfigTranslate.get("cmd.banCmd.200").replace("{0}", found.name);
                else
                    message = ConfigTranslate.get("cmd.banCmd.error");
            } else message = ConfigTranslate.get("cmd.banCmd.404").replace("{0}", badBoy);
        }
        channel.sendMessage(message);
    }
}
