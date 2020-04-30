package matrix.discordBot.communication;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.*;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import mindustry.gen.Call;

public class SendToGame {

    public void onMessageReceived(MessageCreateEvent event) {
        if (preCheck(event))
            return;
        Guild guild = event.getMessage().getGuild().block();
        if (guild == null) return;
        User author = event.getMember().get();
        Message message = event.getMessage();
        String content = message.getContent().replace("\n", " ");
        Member member = guild.getMemberById(author.getId()).block();
        if (member == null)
            return;
        Role role = member.getHighestRole().block();
        // Если участник без роли то отправляем сообщение без названия роли.
        if (role == null || role.getName().isEmpty()) {
            Call.sendMessage(ConfigTranslate.get("msgToGame.prefix") + "[" + author.getUsername() + "]" + ConfigTranslate.get("msgToGame.suffix") + content);
        } else {
            // Конвертируем RGB в HEX
            String hex = String.format("#%02x%02x%02x", role.getColor().getRed(), role.getColor().getGreen(), role.getColor().getBlue());
            String roleName = "[" + hex + "][" + role.getName() + "]";
            Call.sendMessage(ConfigTranslate.get("msgToGame.prefix") + roleName
                    + ConfigTranslate.get("msgToGame.nickColor") + "[" + author.getUsername() + "]" + ConfigTranslate.get("msgToGame.suffix") + content);
        }
    }

    private boolean preCheck(MessageCreateEvent event) {
        boolean result = false;
        if (!event.getMember().isPresent() || event.getMessage().getChannel().block() != null)
            result = true;
        if (event.getMember().get().isBot()) result = true;
        if (!event.getMessage().getChannelId().asString().equals(Config.get("discordChatChannelId"))) result = true;
        if (event.getMessage().getContent().isEmpty()) result = true;
        if (event.getMessage().getGuild().block() == null)
            result = true;
        return result;
    }

}
