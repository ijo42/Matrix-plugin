package matrix.commands.client;

import arc.Events;
import arc.util.Log;
import arc.util.Strings;
import javafx.util.Pair;
import matrix.Matrix;
import matrix.discordbot.Bot;
import matrix.utils.*;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.entities.type.Player;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;

import java.util.HashMap;

public class ClientCommands {
    private final Long CDT = 300L;
    private final HashMap<Long, String> cooldowns = new HashMap<>();

    public static void jsCommand(String[] arg, Player player) {
        if (player.isAdmin) {
            String result = Vars.mods.getScripts().runConsole(arg[ 0 ]);

            player.sendMessage("[gray][[[#F7E018]JS[gray]]: [lightgray]" + result);
            Log.info("&lmJS: &lc" + result);

        } else player.sendMessage("[gray][[[#F7E018]JS[gray]]: [coral]" + ConfigTranslate.get("cmd.js.isNotAdmin"));
    }

    public static void sendWaves(String[] arg, Player player) {
        if (player.isAdmin) {
            if (Integer.parseInt(arg[ 0 ]) > 0 && Integer.parseInt(arg[ 0 ]) < 100) {
                player.sendMessage("[gray][[[#F7E018]Starting..[gray]]");
                for (int i = 0; i < Integer.parseInt(arg[ 0 ]); i++) {
                    Vars.logic.runWave();
                }
            } else
                player.sendMessage("[gray][[[#F7E018]CMD[gray]]: [coral]" + ConfigTranslate.get("cmd.sendWaves.limit"));
        } else
            player.sendMessage("[gray][[[#F7E018]CMD[gray]]: [coral]" + ConfigTranslate.get("cmd.sendWaves.noPerms"));
    }

    public static void memoryCommand(String[] args, Player player) {
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

    public static void broadcastCommand(String[] args, Player player) {
        if (player.isAdmin) Broadcast.bc(args, player);
    }

    public static void infinityResourceCommand(String[] args, Player player) {
        if (player.isAdmin && Boolean.parseBoolean(Config.get("infiniteResourcesCmd"))) {
            InfiniteResources.set(args, player);
        }
    }

    public static void setBlockCommand(String[] args, Player player) {
        if (player.isAdmin) Map.setBlock(player, args);
    }

    public static void spawnOreCommand(String[] args, Player player) {
        if (player.isAdmin) Map.spawnOre(player, args);
    }

    public static void teleportToPlayerCommand(String[] args, Player player) {
        Teleport.toPlayer(player, args);
    }

    public static void tpCommand(String[] args, Player player) {
        Teleport.toPoint(player, args);
    }

    public static void gameOverCommand(String[] args, Player player) {
        if (!player.isAdmin) return;
        if (Vars.state.is(GameState.State.menu)) {
            Log.err("Not playing a map.");
            return;
        }
        Events.fire(new EventType.GameOverEvent(Team.crux));
    }

    public void griefCommand(String[] args, Player player) {
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
                Matrix.INSTANCE.getBot().report(new Pair<>(found.name, String.valueOf(found.id)), player.name, (args.length == 2 ? args[ 1 ] : ""));
                player.sendMessage(ChatGuard.removeColors.apply(found.name) + ConfigTranslate.get("cmd.grief.successfulSend"));
                cooldowns.put(System.currentTimeMillis() / 1000L, player.uuid);
            } else player.sendMessage(ConfigTranslate.get("cmd.grief.404").replace("{0}", args[ 0 ]));
        }
    }
}
