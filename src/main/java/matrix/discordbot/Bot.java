package matrix.discordbot;

import arc.util.Log;
import matrix.Matrix;
import matrix.discordbot.commands.MainCmd;
import matrix.discordbot.communication.SendToGame;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.List;
import java.util.Optional;

public class Bot {
    DiscordApi api;

    public Bot() {
        Config.get("token");
        if (!Config.has("token"))
            throw new RuntimeException("DiscordPlugin: Токен не валидный.");
        new DiscordApiBuilder().setToken(Config.get("token")).login().thenAccept(api -> {
            this.api = api;
            api.addMessageCreateListener(SendToGame::new);
            api.addMessageCreateListener(MainCmd::new);
            User bot = api.getYourself();
            Log.info(String.format("Logged in as %s#%s", bot.getName(), bot.getDiscriminator()));
            api.setMessageCacheSize(1, 30);
            Runtime.getRuntime().addShutdownHook(new ShootDownHook());
            api.updateActivity(ActivityType.PLAYING, Config.get("status"));
        }).exceptionally(ExceptionLogger.get());
    }

    public static Role getHighestRole(List<Role> roles) {
        final Role[] highestRole = {null};
        roles.forEach(role -> {
            if (highestRole[ 0 ] == null || role.compareTo(highestRole[ 0 ]) > 0)
                highestRole[ 0 ] = role;
        });
        return highestRole[ 0 ];
    }

    public static Optional<Role> getRoleFromID(String id) {
        return Matrix.INSTANCE.getBot().api.getRoleById(id);
    }

    public void sendMessage(String channelID, String message) {
        Optional<TextChannel> channel = api.getTextChannelById(channelID);
        if (!channel.isPresent()) {
            new RuntimeException("Channel not present Bot::sendMessage").printStackTrace();
            return;
        }
        new MessageBuilder().append(message).send(channel.get());
    }

    public void sendEmbed(String channelID, EmbedBuilder spec) {
        Optional<TextChannel> channel = api.getTextChannelById(channelID);
        if (!channel.isPresent()) {
            new RuntimeException("Channel not present Bot::sendMessage").printStackTrace();
            return;
        }
        new MessageBuilder().setEmbed(spec).send(channel.get());
    }

    public class ShootDownHook extends Thread {
        @Override
        public void run() {
            Optional<Role> stuff = api.getRoleById(Config.get("stuffRoleID"));
            if (stuff.isPresent()) {
                stuff.get().updateMentionableFlag(true);
                String message = ConfigTranslate.get("serverDownMessage");
                message = message.replace("{0}", stuff.get().getMentionTag());
                if (Config.has("serverName"))
                    message = message.replace("{1}", Config.get("serverName"));
                else
                    message = message.substring(0, message.indexOf("{1}")) + message.substring(message.indexOf("{1}"));
                sendMessage(Config.get("stuffChannelID"), message);
            }
        }
    }
}