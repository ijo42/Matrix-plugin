package matrix.discordBot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import matrix.discordBot.commands.map.MapChangeCmd;
import matrix.discordBot.commands.map.MapCmd;
import matrix.discordBot.commands.map.MapUploadCmd;
import matrix.discordBot.commands.map.MapsCmd;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;

public class MainCmd {

    public void onMessageReceived(MessageCreateEvent event) {
        if (!event.getMember().isPresent() || event.getMember().get().isBot()) return;
        Message msg = event.getMessage();

        if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.setMsgChannel.name"))) {
            SetMsgChannel.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.items.name"))) {
            ItemsCmd.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.memory.name"))) {
            Memory.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.map.name"))) {
            MapCmd.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.maps.name"))) {
            MapsCmd.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.changeMap.name"))) {
            MapChangeCmd.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.uploadMap.name"))) {
            MapUploadCmd.main(event);
        }
    }
}
