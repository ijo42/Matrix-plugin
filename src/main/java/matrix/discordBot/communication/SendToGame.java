package matrix.discordBot.communication;

import matrix.discordBot.Bot;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import mindustry.gen.Call;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.Optional;

public class SendToGame implements MessageCreateListener {

    public SendToGame(MessageCreateEvent messageCreateEvent) {
        onMessageCreate(messageCreateEvent);
    }

    private boolean preCheck(MessageCreateEvent event) {
        boolean result = false;
        if (!event.getMessage().getAuthor().isUser()) result = true;
        if (event.getMessage().isPrivateMessage()) result = true;
        if (event.getMessage().getContent().isEmpty()) result = true;
        if (!event.getChannel().getIdAsString().equals(Config.get("liveChannelId"))) result = true;
        return result;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (preCheck(event))
            return;
        Optional<Server> guild = event.getMessage().getServer();
        if (!guild.isPresent()) return;
        MessageAuthor author = event.getMessageAuthor();
        Message message = event.getMessage();
        String content = message.getContent().replace("\n", " ");
        Optional<User> member = guild.get().getMemberById(author.getId());
        if (!member.isPresent())
            return;
        Role role = Bot.getHighestRole(member.get().getRoles(guild.get()));
        if (role == null || role.getName().isEmpty() || !role.getColor().isPresent()) {
            Call.sendMessage(ConfigTranslate.get("msgToGame.prefix") + "[" + author.getDisplayName() + "]" + ConfigTranslate.get("msgToGame.suffix") + content);
        } else {
            String hex = String.format("#%02x%02x%02x", role.getColor().get().getRed(), role.getColor().get().getGreen(), role.getColor().get().getBlue());
            String roleName = "[" + hex + "][" + role.getName() + "]";
            Call.sendMessage(ConfigTranslate.get("msgToGame.prefix") + roleName
                    + ConfigTranslate.get("msgToGame.nickColor") + "[" + author.getDisplayName() + "]" + ConfigTranslate.get("msgToGame.suffix") + content);
        }
    }
}
