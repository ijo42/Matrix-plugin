package matrix;

import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import arc.util.Strings;
import matrix.commands.client.*;
import matrix.discordbot.Bot;
import matrix.discordbot.communication.SendToDiscord;
import matrix.utils.*;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.entities.type.Player;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.plugin.Plugin;

import java.util.HashMap;

public class Matrix extends Plugin {

    public static Matrix INSTANCE;
    private final Bot bot;
    private final Long CDT = 300L;
    private HashMap<Long, String> cooldowns = new HashMap<>();

    public Matrix() {
        Config.main();
        if (!Config.get("botIsEnabled").equalsIgnoreCase("true")) {
            Log.warn("BOT IS DISABLED");
            throw new RuntimeException("Enable bot :)");
        }
        INSTANCE = this;
        bot = new Bot();
        Events.on(EventType.PlayerJoin.class, event -> {
            TitleManager.main();
            if (Config.get("botIsEnabled").equalsIgnoreCase("true")) {
                if (Vars.netServer.admins.getPlayerLimit() != 0) {
                    String sendString = ConfigTranslate.get("onJoin")
                            .replace("{0}", ChatGuard.removeColors.apply(event.player.name))
                            .replace("{1}", String.valueOf(Vars.playerGroup.size() + 1))
                            .replace("{2}", String.valueOf(Vars.netServer.admins.getPlayerLimit()));
                    SendToDiscord.sendBotMessage(sendString);
                } else {
                    String sendString = ConfigTranslate.get("onJoinUnlimited")
                            .replace("{0}", ChatGuard.removeColors.apply(event.player.name))
                            .replace("{1}", String.valueOf(Vars.playerGroup.size() + 1));
                    SendToDiscord.sendBotMessage(sendString);
                }
            }
        });

        Events.on(EventType.PlayerLeave.class, event -> {
            if (Config.get("botIsEnabled").equalsIgnoreCase("true")) {
                if (Vars.netServer.admins.getPlayerLimit() != 0) {
                    String sendString = ConfigTranslate.get("onLeave")
                            .replace("{0}", ChatGuard.removeColors.apply(event.player.name))
                            .replace("{1}", String.valueOf(Vars.playerGroup.size() - 1))
                            .replace("{2}", String.valueOf(Vars.netServer.admins.getPlayerLimit()));
                    SendToDiscord.sendBotMessage(sendString);
                } else {
                    String sendString = ConfigTranslate.get("onLeaveUnlimited")
                            .replace("{0}", ChatGuard.removeColors.apply(event.player.name))
                            .replace("{1}", String.valueOf(Vars.playerGroup.size() - 1));
                    SendToDiscord.sendBotMessage(sendString);
                }
            }
        });

        Events.on(EventType.PlayerChatEvent.class, (event) -> {
            String msg = event.message;
            String nick = event.player.name;

            // Запускаем проверку на запрещенные слова
            if (Boolean.parseBoolean(Config.get("botIsEnabled"))) {
                if (!msg.startsWith("/")) {
                    if (!event.player.isAdmin && Boolean.parseBoolean(Config.get("chatGuard"))) {
                        if (!ChatGuard.check(msg)) {
                            SendToDiscord.sendChatMessage(nick, ChatGuard.removeColors.apply(msg), false);
                        } else event.player.sendMessage(ConfigTranslate.get("dontSwear"));
                    } else {
                        SendToDiscord.sendChatMessage(nick, ChatGuard.removeColors.apply(msg), true);
                    }
                } else SendToDiscord.log(nick, msg);
            }

        });

    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("ping", "Return \"Pong!\"", arg -> Log.info("Pong!"));

        handler.register("nogui", "Auto start for hosting", arg -> {
            if (Vars.state.is(GameState.State.playing)) {
                Log.err("Already hosting. Type 'stop' to stop hosting first.");
                return;
            }
            Log.info(SystemInfo.cpuProcess());
        });

        handler.register("memory", "Return \"Pong!\"", arg -> {
            Log.info("SYSTEM CPU LOAD: " + SystemInfo.cpu() + "%");
            Log.info("PROCESS CPU LOAD: " + SystemInfo.cpuProcess() + "%");
            Log.info("TOTAL RAM LOAD: " + (100 - SystemInfo.ram()) + "%");
        });

    }

    private static void jsCommand(String[] arg, Player player) {
        if (player.isAdmin) {
            String result = Vars.mods.getScripts().runConsole(arg[ 0 ]);

            player.sendMessage("[gray][[[#F7E018]JS[gray]]: [lightgray]" + result);
            Log.info("&lmJS: &lc" + result);

        } else player.sendMessage("[gray][[[#F7E018]JS[gray]]: [coral]" + ConfigTranslate.get("cmd.js.isNotAdmin"));
    }

    private static void memoryCommand(String[] args, Player player) {
        if (player.isAdmin) {
            player.sendMessage(ConfigTranslate.get("cmd.memory.msg")
                    .replace("{0}", String.valueOf(SystemInfo.cpu()))
                    .replace("{1}", String.valueOf(SystemInfo.cpuProcess()))
                    .replace("{2}", String.valueOf(SystemInfo.ram()))
            );
        } else {
            player.sendMessage(ConfigTranslate.get("cmd.memory.noPex"));
        }
    }

    private static void broadcastCommand(String[] args, Player player) {
        if (player.isAdmin) Broadcast.bc(args, player);
    }

    private static void infinityResourceCommand(String[] args, Player player) {
        if (player.isAdmin && Boolean.parseBoolean(Config.get("infiniteResourcesCmd"))) {
            InfiniteResources.set(args, player);
        }
    }

    private static void setBlockCommand(String[] args, Player player) {
        if (player.isAdmin) SetBlock.main(player, args);
    }

    private static void spawnOreCommand(String[] args, Player player) {
        if (player.isAdmin) SpawnOre.main(player, args);
    }

    private static void teleportToPlayerCommand(String[] args, Player player) {
        Teleport.toPlayer(player, args);
    }

    private static void tpCommand(String[] args, Player player) {
        Teleport.toPoint(player, args);
    }

    private static void gameOverCommand(String[] args, Player player) {
        if (!player.isAdmin) return;
        if (Vars.state.is(GameState.State.menu)) {
            Log.err("Not playing a map.");
            return;
        }
        Events.fire(new EventType.GameOverEvent(Team.crux));
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {

        handler.register(ConfigTranslate.get("cmd.setTeam.name"), ConfigTranslate.get("cmd.setTeam.params"), ConfigTranslate.get("cmd.setTeam.description"), SetTeam::set);

        handler.register(ConfigTranslate.get("cmd.gameOver.name"), ConfigTranslate.get("cmd.gameOver.params"), ConfigTranslate.get("cmd.gameOver.description"), Matrix::gameOverCommand);

        handler.register(ConfigTranslate.get("cmd.tp.name"), ConfigTranslate.get("cmd.tp.params"), ConfigTranslate.get("cmd.tp.description"), Matrix::tpCommand);

        handler.register(ConfigTranslate.get("cmd.tpToPlayer.name"), ConfigTranslate.get("cmd.tpToPlayer.params"), ConfigTranslate.get("cmd.tpToPlayer.description"), Matrix::teleportToPlayerCommand);

        handler.register(ConfigTranslate.get("cmd.spawnOre.name"), ConfigTranslate.get("cmd.spawnOre.params"), ConfigTranslate.get("cmd.spawnOre.description"), Matrix::spawnOreCommand);

        handler.register(ConfigTranslate.get("cmd.setBlock.name"), ConfigTranslate.get("cmd.setBlock.params"), ConfigTranslate.get("cmd.setBlock.description"), Matrix::setBlockCommand);

        handler.register(ConfigTranslate.get("cmd.infiniteResources.name"), "<on/off>", ConfigTranslate.get("cmd.infiniteResources.description"), Matrix::infinityResourceCommand);
        handler.register(ConfigTranslate.get("cmd.broadcast.name"), "<info...>", ConfigTranslate.get("cmd.broadcast.description"), Matrix::broadcastCommand);
        handler.register(ConfigTranslate.get("cmd.memory.name"), "", ConfigTranslate.get("cmd.memory.description"), Matrix::memoryCommand);
        handler.register(ConfigTranslate.get("cmd.js.name"), "<script...>", "Run arbitrary Javascript.", Matrix::jsCommand);

        handler.register(ConfigTranslate.get("cmd.grief.name"), "<id> <причина>", ConfigTranslate.get("cmd.grief.description").replace("{0}", ConfigTranslate.get("cmd.grief.name")), this::griefCommand);
    }

    private void griefCommand(String[] args, Player player) {
        if (!Bot.isReportEnabled()) {
            player.sendMessage(ConfigTranslate.get("cmd.grief.disabled"));
            return;
        }

        for (Long key : cooldowns.keySet()) {
            if (key + CDT < System.currentTimeMillis() / 1000L) cooldowns.remove(key);
            else if (player.uuid.equals(cooldowns.get(key))) {
                player.sendMessage(ConfigTranslate.get("cmd.grief.cooldown"));
                return;
            }
        }

        if (args.length == 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(ConfigTranslate.get("cmd.grief.available"));
            for (Player p : Vars.playerGroup.all()) {
                if (p.isAdmin || p.con == null) continue;
                builder.append("[lightgray] ").append(p.name).append("[accent] (#").append(p.id).append(")\n");
            }
            player.sendMessage(builder.toString());
        } else {
            Player found = null;
            if (args[ 0 ].length() > 1 && args[ 0 ].startsWith("#") && Strings.canParseInt(args[ 0 ].substring(1))) {
                int id = Strings.parseInt(args[ 0 ].substring(1));
                for (Player p : Vars.playerGroup.all())
                    if (p.id == id) {
                        found = p;
                        break;
                    }
            } else for (Player p : Vars.playerGroup.all())
                if (p.name.equalsIgnoreCase(args[ 0 ])) {
                    found = p;
                    break;
                }
            if (found != null) {
                bot.report(found.name, player.name, args[ 1 ]);
                Call.sendMessage(found.name + ConfigTranslate.get("cmd.grief.successfulSend"));
                cooldowns.put(System.currentTimeMillis() / 1000L, player.uuid);
            } else player.sendMessage(ConfigTranslate.get("cmd.grief.notFound"));
        }
    }

    public Bot getBot() {
        return bot;
    }
}

