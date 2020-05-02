package matrix.discordbot.commands;

import matrix.Matrix;
import matrix.discordbot.Bot;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Optional;


public class SetMsgChannel {

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
        Matrix.INSTANCE.getBot().sendMessage(channel.getIdAsString(), (ConfigTranslate.get("cmd.setMsgChannel.ok")));
        String id = channel.getIdAsString();
        Config.set("liveChannelId", id);
    }
}