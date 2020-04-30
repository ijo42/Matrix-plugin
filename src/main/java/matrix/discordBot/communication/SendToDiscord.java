package matrix.discordBot.communication;

import discord4j.core.spec.EmbedCreateSpec;
import matrix.Matrix;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import matrix.utils.RemoveColors;

import java.time.Instant;


public class SendToDiscord {

    public static void send(String nick, String msg) {
        if (msg.isEmpty()) return;
        msg = msg.replace("@here", ConfigTranslate.get("pingDeleted")).replace("@everyone", ConfigTranslate.get("pingDeleted"));
        EmbedCreateSpec embed = new EmbedCreateSpec()
                .setAuthor(RemoveColors.main(nick), null, Config.get("messagerAvatarURL"))
                .setTimestamp(Instant.now()).setDescription(msg);
        Matrix.INSTANCE.getBot().sendEmbed(Config.get("discordChatChannelId"), embed);
    }
    public static void log(String nick, String msg) {
        if (msg.isEmpty()) return;
        String message = ConfigTranslate.get("loggerName") + "\n" +
                ConfigTranslate.get("usageCmd").replace("{0}", RemoveColors.main(nick))
                + msg;
        Matrix.INSTANCE.getBot().sendMessage(Config.get("discordLogChannelId"), message);
    }
    public static void sendBotMessage(String message) {
        if(message.isEmpty()) return;
        Matrix.INSTANCE.getBot().sendMessage(Config.get("discordLogChannelId"), message);
    }
}
