package matrix.utils;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.type.Player;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.world.Block;
import mindustry.world.blocks.Floor;

public class Map {

    public static int getCapacity(Team team) {
        int capacity = 0;
        //TODO:fix zero output
        for (int x = 0; x < Vars.state.teams.get(team).cores.size; x++) {
            if (Vars.state.teams.get(team).cores.get(x).block == Blocks.coreShard)
                capacity += Blocks.coreShard.itemCapacity;
            if (Vars.state.teams.get(team).cores.get(x).block == Blocks.coreFoundation)
                capacity += Blocks.coreFoundation.itemCapacity;
            if (Vars.state.teams.get(team).cores.get(x).block == Blocks.coreNucleus)
                capacity += Blocks.coreNucleus.itemCapacity;
        }

        return capacity;
    }

    public static void spawnOre(Player player, String[] Args) {

        String[] args = Args[ 0 ].split(" ");
        int radius;

        if (!args[ 0 ].matches("\\d+")) {
            player.sendMessage(ConfigTranslate.get("cmd.spawnOre.error"));
            return;
        } else if (Long.parseLong(args[ 0 ]) >= 100) {
            radius = 100;
        } else radius = Integer.parseInt(args[ 0 ]);

        int x = Math.round(player.x / 8);
        int y = Math.round(player.y / 8);
        short id = 0;
        if (args.length >= 2) {
            try {
                id = Short.parseShort(args[ 1 ]);
            } catch (NumberFormatException e) {
                switch (args[ 1 ]) {
                    case "lead":
                        id = Blocks.oreLead.id;
                        break;
                    case "coal":
                        id = Blocks.oreCoal.id;
                        break;
                    case "titanium":
                        id = Blocks.oreTitanium.id;
                        break;
                    case "thorium":
                        id = Blocks.oreThorium.id;
                        break;
                    case "scrap":
                        id = Blocks.oreScrap.id;
                        break;
                }
            }
        }
        if (id == 0 || Vars.content.block(id) == null || !(Vars.content.block(id) instanceof Floor)) {
            player.sendMessage(ConfigTranslate.get("cmd.spawnOre.error"));
            return;
        }
        for (int rx = -radius; rx <= radius; rx++) {
            for (int ry = -radius; ry <= radius; ry++) {
                if (Mathf.dst2(rx, ry) <= (radius - 0.5f) * (radius - 0.5f)) {
                    int wx = x + rx, wy = y + ry;

                    if (wx < 0 || wy < 0 || wx >= Vars.world.width() || wy >= Vars.world.height()) {
                        continue;
                    }
                    Vars.world.tile(wx, wy).setOverlayID(id);
                }
            }
        }
        player.sendMessage(ConfigTranslate.get("cmd.spawnOre.ok"));

        Vars.playerGroup.all().list().forEach(Map::sync);
    }

    public static void setBlock(Player player, String[] args) {

        int x = Math.round(player.x/8);
        int y = Math.round(player.y/8);

        Block block = Vars.content.blocks().find(b -> b.name.equals(args[ 0 ]));

        if (block != null) {
            player.sendMessage(ConfigTranslate.get("cmd.setBlock.successful"));
            Vars.world.tile(x, y).setNet(block, player.getTeam(), 0);
            Vars.playerGroup.all().list().forEach(Map::sync);

        } else player.sendMessage(ConfigTranslate.get("cmd.setBlock.failed"));
    }

    static void sync(Player player) {
        if (player.isLocal) {
            player.sendMessage("[scarlet]Re-synchronizing as the host is pointless.");
        } else {
            if (Time.timeSinceMillis(player.getInfo().lastSyncTime) < 5000L) {
                player.sendMessage("[scarlet]You may only /sync every 5 seconds.");
                return;
            }

            player.getInfo().lastSyncTime = Time.millis();
            Call.onWorldDataBegin(player.con);
            Vars.netServer.sendWorldData(player);
        }

    }
}
