package matrix.discordbot.communication;

import matrix.Matrix;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import matrix.utils.RemoveColors;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.time.Instant;

public class SendToDiscord {

    public static void send(String nick, String msg) {
        if (msg.isEmpty()) return;
        msg = msg.replace("@here", ConfigTranslate.get("pingDeleted")).replace("@everyone", ConfigTranslate.get("pingDeleted"));
        //TODO: <@%ROLE_ID> replace by regexp
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(RemoveColors.remove.apply(nick), null, Config.get("messagerAvatarURL"))
                .setTimestamp(Instant.now()).setDescription(msg);
        Matrix.INSTANCE.getBot().sendEmbed(Config.get("liveChannelId"), embed);
    }
    public static void log(String nick, String msg) {
        if (msg.isEmpty()) return;
        String message = ConfigTranslate.get("loggerName") + "\n" +
                ConfigTranslate.get("usageCmd").replace("{0}", RemoveColors.remove.apply(nick))
                + msg;
        Matrix.INSTANCE.getBot().sendMessage(Config.get("stuffChannelID"), message);
    }
    public static void sendBotMessage(String message) {
        if(message.isEmpty()) return;
        Matrix.INSTANCE.getBot().sendMessage(Config.get("liveChannelId"), message);
    }
}
