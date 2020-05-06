package matrix.discordbot.commands;

import matrix.discordbot.commands.map.*;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class MainCmd implements MessageCreateListener {

    public MainCmd(MessageCreateEvent messageCreateEvent) {
        onMessageCreate(messageCreateEvent);
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.isPrivateMessage() || !event.getMessageAuthor().isUser()) return;
        Message msg = event.getMessage();
        if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.setMsgChannel.name"))) {
            SetMsgChannel.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.items.name"))) {
            ItemsCmd.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.memory.name"))) {
            Memory.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.maps.name"))) {
            MapsCmd.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.map.name"))) {
            MapCmd.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.changeMap.name"))) {
            ChangeCmd.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.uploadMap.name"))) {
            UploadCmd.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.deleteMap.name"))) {
            DeleteCmd.main(event);
        } else if (msg.getContent().startsWith(Config.get("prefix") + ConfigTranslate.get("cmd.banCmd.name"))) {
            BanCmd.main(event);
        }
    }
}
