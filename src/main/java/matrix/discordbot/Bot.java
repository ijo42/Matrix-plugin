package matrix.discordbot;

import arc.util.Log;
import javafx.util.Pair;
import matrix.Matrix;
import matrix.discordbot.commands.MainCmd;
import matrix.discordbot.communication.SendToGame;
import matrix.utils.ChatGuard;
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

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class Bot {
    private DiscordApi api;
    private static boolean online = false;
    public Bot() {
        if (!Boolean.parseBoolean(Config.get("botIsEnabled"))) {
            Log.warn("MATRIX: Bot is Disabled");
            return;
        }
        if (!Config.has("token"))
            throw new RuntimeException("MATRIX: Token Invalid.");
        new DiscordApiBuilder().setToken(Config.get("token")).login().thenAccept(api -> {
            this.api = api;
            online = true;
            api.addMessageCreateListener(SendToGame::new);
            api.addMessageCreateListener(MainCmd::new);
            User bot = api.getYourself();
            Log.info(String.format("Logged in as %s#%s", bot.getName(), bot.getDiscriminator()));
            api.setMessageCacheSize(1, 30);
            api.updateActivity(ActivityType.PLAYING, Config.get("status"));
            api.setAutomaticMessageCacheCleanupEnabled(true);
            api.addLostConnectionListener(event -> online = false);
            api.addResumeListener(event -> online = true);
            api.addReconnectListener(event -> online = true);
            BotThread botThread = new BotThread(Thread.currentThread());
            botThread.setDaemon(false);
            botThread.start();
        }).exceptionally(ExceptionLogger.get());
    }

    public static boolean isOnline() {
        return online;
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

    public static Optional<TextChannel> getTextChannelFromID(String id) {
        if (isOnline())
            return Matrix.INSTANCE.getBot().api.getTextChannelById(id);
        else return Optional.empty();
    }

    public static boolean isReportEnabled() {
        return isOnline() && Config.has("stuffRoleID") && Config.has("stuffChannelID") && Bot.getRoleFromID(Config.get("stuffRoleID")).isPresent() && Bot.getTextChannelFromID(Config.get("stuffChannelID")).isPresent();
    }

    public void sendMessage(String channelID, String message) {
        if (!isOnline())
            return;
        Optional<TextChannel> channel = api.getTextChannelById(channelID);
        if (!channel.isPresent()) {
            new RuntimeException("Channel not present Bot::sendMessage").printStackTrace();
            return;
        }
        new MessageBuilder().append(message).send(channel.get());
    }

    public void sendEmbed(String channelID, EmbedBuilder spec) {
        if (!isOnline())
            return;
        Optional<TextChannel> channel = api.getTextChannelById(channelID);
        if (!channel.isPresent()) {
            new RuntimeException("Channel not present Bot::sendMessage").printStackTrace();
            return;
        }
        new MessageBuilder().setEmbed(spec).send(channel.get());
    }

    public static String getMentionTag() {
        return Matrix.INSTANCE.getBot().api.getYourself().getNicknameMentionTag();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void report(Pair<String, String> suspect, String reporter, String reason) {
        Role stuff = Bot.getRoleFromID(Config.get("stuffRoleID")).get();
        TextChannel stuffChat = Bot.getTextChannelFromID(Config.get("stuffChannelID")).get();
        if (!reason.isEmpty()) {
            new MessageBuilder()
                    .setEmbed(new EmbedBuilder()
                            .setTitle(ConfigTranslate.get("cmd.grief.titleMsg"))
                            .setDescription(stuff.getMentionTag())
                            .addField(ConfigTranslate.get("cmd.grief.suspectName"), ChatGuard.removeColors.apply(suspect.getKey()) + " ||#" + suspect.getValue() + "||")
                            .addField(ConfigTranslate.get("cmd.grief.suspectReason"), reason)
                            .setColor(Color.ORANGE)
                            .setFooter(ConfigTranslate.get("cmd.grief.reporter") + ChatGuard.removeColors.apply(reporter)))
                    .send(stuffChat);
        } else {
            new MessageBuilder()
                    .setEmbed(new EmbedBuilder()
                            .setTitle(ConfigTranslate.get("cmd.grief.titleMsg"))
                            .setDescription(stuff.getMentionTag())
                            .addField(Config.get("cmd.grief.suspectName"), ChatGuard.removeColors.apply(suspect.getKey()) + " ||#" + suspect.getValue() + "||")
                            .setColor(Color.ORANGE)
                            .setFooter(ConfigTranslate.get("cmd.grief.reporter") + ChatGuard.removeColors.apply(reporter)))
                    .send(stuffChat);
        }
    }

    public class BotThread extends Thread {
        private final Thread mt;
        public BotThread(Thread _mt) {
            mt = _mt;
            if (!api.getRoleById(Config.get("stuffRoleID")).isPresent() || !getTextChannelFromID(Config.get("stuffChannelID")).isPresent())
                this.interrupt();
        }

        @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
        @Override
        public void run() {
            try {
                do {
                    Thread.sleep(5000);

                } while (true);//Vars.net.server());
            } catch (Exception ignored) {
            }
            if (api.getRoleById(Config.get("stuffRoleID")).isPresent()) {
                Optional<TextChannel> tc = getTextChannelFromID(Config.get("stuffChannelID"));
                if (!tc.isPresent())
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ignored) {
                    }
                else {
                    Role stuff = api.getRoleById(Config.get("stuffRoleID")).get();
                    if (!stuff.isMentionable())
                        stuff.updateMentionableFlag(true);
                    String message = ConfigTranslate.get("serverDownMessage").replace("{0}", stuff.getMentionTag());
                    if (Config.has("serverName"))
                        message = message.replace("{1}", Config.get("serverName"));
                    else
                        message = message.substring(0, message.indexOf("{1}")) + message.substring(message.indexOf("{1}"));

                    tc.get().sendMessage(message);
                }
            }
            api.disconnect();
        }
    }
}