package matrix.discordBot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.shard.LocalShardCoordinator;
import discord4j.core.shard.ShardingStrategy;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ImmutableMessageCreateRequest;
import discord4j.discordjson.json.MessageCreateRequest;
import discord4j.rest.util.Snowflake;
import discord4j.store.jdk.JdkStoreService;
import matrix.discordBot.commands.MainCmd;
import matrix.discordBot.communication.SendToGame;
import matrix.utils.Config;
import org.graalvm.compiler.replacements.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Bot {
    GatewayDiscordClient gatewayClient;

    public Bot() {
        if (Config.get("token") == null || Config.get("token").isEmpty())
            throw new RuntimeException("Bot token is null");
        DiscordClient client = DiscordClient.create(Config.get("token"));
        gatewayClient = client.gateway()
                .setInitialStatus(shard -> Presence.idle())
                .setSharding(ShardingStrategy.recommended())
                .setShardCoordinator(LocalShardCoordinator.create())
                .setAwaitConnections(true)
                .setStoreService(new JdkStoreService())
                .setEventDispatcher(EventDispatcher.buffering())
                .login()
                .block();

        client.withGateway(gateway -> {
            Flux<ReadyEvent> hello = gateway.on(ReadyEvent.class)
                    .doOnNext(ready -> {
                        User self = ready.getSelf();
                        Log.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
                        ready.getClient().updatePresence(Presence.online(Activity.watching(Config.get("status"))));
                    });

            MainCmd mainCmd = new MainCmd();
            SendToGame sendToGame = new SendToGame();
            Flux<MessageCreateEvent> command = gateway.on(MessageCreateEvent.class)
                    .filter(x -> !x.getMessage().getContent().isEmpty())
                    .filter(x -> x.getMessage().getContent().startsWith(Config.get("prefix")))
                    .doOnNext(mainCmd::onMessageReceived);

            Flux<MessageCreateEvent> integrate = gateway.on(MessageCreateEvent.class)
                    .filter(x -> !x.getMessage().getContent().isEmpty())
                    .filter(x -> x.getMessage().getContent().startsWith(Config.get("prefix")))
                    .doOnNext(sendToGame::onMessageReceived);

            return Mono.when(hello, command, integrate);
        }).block();
        client.login().block();
    }

    public void sendMessage(String channelID, String message) {
        Channel channel = gatewayClient.getChannelById(Snowflake.of(channelID)).block();
        if (channel == null)
            return;
        ImmutableMessageCreateRequest content = MessageCreateRequest.builder().content(message).build();
        channel.getRestChannel().createMessage(content).block();
    }

    public void sendEmbed(String channelID, EmbedCreateSpec spec) {
        Channel channel = gatewayClient.getChannelById(Snowflake.of(channelID)).block();
        if (channel == null)
            return;
        ImmutableMessageCreateRequest content = MessageCreateRequest.builder().embed(spec.asRequest()).build();
        channel.getRestChannel().createMessage(content).block();
    }
}

/*
public class BotThread extends Thread{
    public DiscordApi api;
    private Thread mt;
    private JSONObject data;

    public BotThread(DiscordApi _api, Thread _mt, JSONObject _data) {
        api = _api; //new DiscordApiBuilder().setToken(data.get(0)).login().join();
        mt = _mt;
        data = _data;

        //communication commands
        api.addMessageCreateListener(new ComCommands());
        //server manangement commands
        api.addMessageCreateListener(new ServerCommands(data));
        api.addMessageCreateListener(new MapCommands(data));
    }

    public void run(){
        while (this.mt.isAlive()){
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {

            }
        }
        if (data.has("serverdown_role_id")){
            Role r = new UtilMethods().getRole(api, data.getString("serverdown_role_id"));
            TextChannel tc = new UtilMethods().getTextChannel(api, data.getString("serverdown_channel_id"));
            if (r == null || tc ==  null) {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }
            } else {
                if (data.has("serverdown_name")){
                    String serverName = data.getString("serverdown_name");
                    new MessageBuilder()
                            .append(String.format("%s\nСервер %s упал", r.getMentionTag(), ((!serverName.equals("")) ? ("**" + serverName + "**") : "")))
                            .send(tc);
                } else {
                    new MessageBuilder()
                            .append(String.format("%s\nСервер упал.", r.getMentionTag()))
                            .send(tc);
                }
            }
        }
        api.disconnect();
    }
}*/
