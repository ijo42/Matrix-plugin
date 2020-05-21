package matrix.discordbot.communication;

import matrix.Matrix;
import matrix.discordbot.Bot;
import matrix.utils.ChatGuard;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.time.Instant;

public class SendToDiscord {

    public static void sendChatMessage(String nick, String msg, boolean isAdmin) {
        if (msg.isEmpty() || !Bot.isOnline()) return;
        msg = ChatGuard.removeMentions.apply(msg);
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(isAdmin ? Color.RED : Color.CYAN)
                .setAuthor(ChatGuard.removeColors.apply(nick), null, (Config.has("messagerAvatarURL") ? Config.get("messagerAvatarURL") : null))
                .setTimestamp(Instant.now()).setDescription(msg);
        Matrix.INSTANCE.getBot().sendEmbed(Config.get("liveChannelId"), embed);
    }

    public static void log(String nick, String msg) {
        if (msg.isEmpty() || !Bot.isOnline()) return;
        String message = ConfigTranslate.get("loggerName") + "\n" +
                ConfigTranslate.get("usageCmd").replace("{0}", ChatGuard.removeColors.apply(nick))
                + msg;
        Matrix.INSTANCE.getBot().sendMessage(Config.get("stuffChannelID"), message);
    }
    public static void sendBotMessage(String message) {
        if (message.isEmpty() || !Bot.isOnline()) return;
        Matrix.INSTANCE.getBot().sendMessage(Config.get("liveChannelId"), message);
    }
}
