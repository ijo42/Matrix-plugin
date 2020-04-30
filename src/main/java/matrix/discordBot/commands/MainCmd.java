package matrix.discordBot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;

public class MainCmd {

    public void onMessageReceived(MessageCreateEvent event) {
        if (!event.getMember().isPresent() || event.getMember().get().isBot()) return;
        Message msg = event.getMessage();

        if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.setMsgChannel.name"))) {
            SetMsgChannel.main(event);
        }
        if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.items.name"))) {
            ItemsCmd.main(event);
        }
        if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.memory.name"))) {
            Memory.main(event);
        }
        if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.map.name"))) {
            MapCmd.main(event);
        }
    }
}
