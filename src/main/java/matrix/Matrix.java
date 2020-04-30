package matrix;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import matrix.commands.client.*;
import matrix.discordBot.Bot;
import matrix.discordBot.communication.SendToDiscord;
import matrix.utils.*;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.entities.type.Player;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.plugin.Plugin;

import javax.security.auth.login.LoginException;

public class Matrix extends Plugin {

    public Matrix() throws LoginException, InterruptedException {
        Config.main();
        if (Config.get("botIsEnabled").equalsIgnoreCase("true")) {
            Bot.main();
        } else Log.warn("BOT IS DISABLED");
        Events.on(EventType.PlayerJoin.class, event -> {
            TitleManager.main();

            if (Config.get("botIsEnabled").equalsIgnoreCase("true")) {
                if (Vars.netServer.admins.getPlayerLimit() != 0) {
                    String sendString = ConfigTranslate.get("onJoin")
                            .replace("{0}", RemoveColors.main(event.player.name))
                            .replace("{1}", String.valueOf(Vars.playerGroup.size() + 1))
                            .replace("{2}", String.valueOf(Vars.netServer.admins.getPlayerLimit()));
                    SendToDiscord.sendBotMessage(sendString);
                } else {
                    String sendString = ConfigTranslate.get("onJoinUnlimited")
                            .replace("{0}", RemoveColors.main(event.player.name))
                            .replace("{1}", String.valueOf(Vars.playerGroup.size() + 1));
                    SendToDiscord.sendBotMessage(sendString);
                }
            }

        });

        Events.on(EventType.PlayerLeave.class, event -> {
            if (Config.get("botIsEnabled").equalsIgnoreCase("true")) {
                if (Vars.netServer.admins.getPlayerLimit() != 0) {
                    String sendString = ConfigTranslate.get("onLeave")
                            .replace("{0}", RemoveColors.main(event.player.name))
                            .replace("{1}", String.valueOf(Vars.playerGroup.size() - 1))
                            .replace("{2}", String.valueOf(Vars.netServer.admins.getPlayerLimit()));
                    SendToDiscord.sendBotMessage(sendString);
                } else {
                    String sendString = ConfigTranslate.get("onLeaveUnlimited")
                            .replace("{0}", RemoveColors.main(event.player.name))
                            .replace("{1}", String.valueOf(Vars.playerGroup.size() - 1));
                    SendToDiscord.sendBotMessage(sendString);
                }
            }
        });

        Events.on(EventType.PlayerChatEvent.class, (event) -> {
            String msg = event.message;
            String nick = event.player.name;

            // Запускаем проверку на запрещенные слова
            if (Config.get("botIsEnabled").equalsIgnoreCase("true")) {
                if (!msg.startsWith("/")) {
                    if (!event.player.isAdmin && Boolean.parseBoolean(Config.get("chatGuard"))) {
                        if (!ChatGuard.check(msg)) {
                            SendToDiscord.send(nick, RemoveColors.main(msg));
                        } else event.player.sendMessage(ConfigTranslate.get("dontSwear"));
                    } else {
                        SendToDiscord.send(nick, RemoveColors.main(msg));
                    }
                } else SendToDiscord.log(nick, msg);
            }

        });

    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        handler.register("ping", "Return \"Pong!\"", arg -> Log.info("Pong!"));

        handler.register("nogui", "Auto start for minecraft hosting", arg -> {
            if(Vars.state.is(GameState.State.playing)) {
                Log.err("Already hosting. Type 'stop' to stop hosting first.");
                return;
            }
            Log.info(SystemInfo.cpuProcess());
        });

        handler.register("memory", "Return \"Pong!\"", arg -> {
            Log.info("SYSTEM CPU LOAD: "+SystemInfo.cpu()+"%");
            Log.info("PROCESS CPU LOAD: "+SystemInfo.cpuProcess()+"%");
            Log.info("TOTAL RAM LOAD: "+ (100-SystemInfo.ram()) +"%");
        });

    }

    @Override
    public void registerClientCommands(CommandHandler handler){

        handler.register(ConfigTranslate.get("cmd.setTeam.name"), ConfigTranslate.get("cmd.setTeam.params"), ConfigTranslate.get("cmd.setTeam.description"), SetTeam::set);

        handler.<Player>register(ConfigTranslate.get("cmd.gameOver.name"), ConfigTranslate.get("cmd.gameOver.params"), ConfigTranslate.get("cmd.gameOver.description"), (args, player) -> {
            if (!player.isAdmin) return;
            if(Vars.state.is(GameState.State.menu)){
                Log.err("Not playing a map.");
                return;
            }
            Events.fire(new EventType.GameOverEvent(Team.crux));
        });

        handler.<Player>register(ConfigTranslate.get("cmd.tp.name"), ConfigTranslate.get("cmd.tp.params"), ConfigTranslate.get("cmd.tp.description"), (args, player) -> Teleport.toPoint(player, args));

        handler.<Player>register(ConfigTranslate.get("cmd.tpToPlayer.name"), ConfigTranslate.get("cmd.tpToPlayer.params"), ConfigTranslate.get("cmd.tpToPlayer.description"), (args, player) -> Teleport.toPlayer(player, args));

        handler.<Player>register(ConfigTranslate.get("cmd.spawnOre.name"), ConfigTranslate.get("cmd.spawnOre.params"), ConfigTranslate.get("cmd.spawnOre.description"), (args, player) -> {
            if (player.isAdmin) SpawnOre.main(player, args);
        });

        handler.<Player>register(ConfigTranslate.get("cmd.setBlock.name"), ConfigTranslate.get("cmd.setBlock.params"), ConfigTranslate.get("cmd.setBlock.description"), (args, player) -> {
            if (player.isAdmin) SetBlock.main(player, args);
        });

        handler.<Player>register(ConfigTranslate.get("cmd.infiniteResources.name"), "<on/off>", ConfigTranslate.get("cmd.infiniteResources.description"), (args, player) -> {
            if(player.isAdmin && Boolean.parseBoolean(Config.get("infiniteResourcesCmd"))) {
                InfiniteResources.set(args, player);
            }
        });
        handler.<Player>register(ConfigTranslate.get("cmd.broadcast.name"), "<info...>", ConfigTranslate.get("cmd.broadcast.description"), (args, player) -> {
            if (player.isAdmin) Broadcast.bc(args, player);
        });
        handler.<Player>register(ConfigTranslate.get("cmd.memory.name"), "", ConfigTranslate.get("cmd.memory.description"), (args, player) -> {
            if (player.isAdmin) {
                player.sendMessage(ConfigTranslate.get("cmd.memory.msg")
                        .replace("{0}", String.valueOf(SystemInfo.cpu()))
                        .replace("{1}", String.valueOf(SystemInfo.cpuProcess()))
                        .replace("{2}", String.valueOf(SystemInfo.ram()))
                );
            } else {
                player.sendMessage(ConfigTranslate.get("cmd.memory.noPex"));
            }
        });
        handler.<Player>register("js", "<script...>", "Run arbitrary Javascript.", (arg, player) -> {
            if (player.isAdmin) {
                // Ответ js интерпретатора сервера
                String result = Vars.mods.getScripts().runConsole(arg[0]);
                
                player.sendMessage("[gray][[[#F7E018]JS[gray]]: [lightgray]" + result);
                Log.info("&lmJS: &lc" + result);
                
                // Послать всем игрокам
                //Call.sendMessage("[gray][[[#F7E018]JS[gray]]: [lightgray]" + result);
            } else player.sendMessage("[gray][[[#F7E018]JS[gray]]: [coral]" + ConfigTranslate.get("cmd.js.isNotAdmin"));
            
        });
    }
}
