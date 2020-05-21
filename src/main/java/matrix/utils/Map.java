package matrix.utils;

import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.type.Player;
import mindustry.game.Team;
import mindustry.world.Block;

public class Map {

    public static int getCapacity(Team team) {
        int capacity = 0;
            //TODO:fix zero output
        for(int x = 0; x < Vars.state.teams.get(team).cores.size; x++){
            if(Vars.state.teams.get(team).cores.get(x).block == Blocks.coreShard) capacity+=Blocks.coreShard.itemCapacity;
            if(Vars.state.teams.get(team).cores.get(x).block == Blocks.coreFoundation) capacity+=Blocks.coreFoundation.itemCapacity;
            if(Vars.state.teams.get(team).cores.get(x).block == Blocks.coreNucleus) capacity+=Blocks.coreNucleus.itemCapacity;
        }

        return capacity;
    }

    public static void spawnOre(Player player, String[] Args) {

        String[] args = Args[0].split(" ");
        Block block = Blocks.oreCopper;
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
        if (id == 0) {
            player.sendMessage(ConfigTranslate.get("cmd.spawnOre.error"));
            return;
        }
        for (int rx = -radius; rx <= radius; rx++) {
            for (int ry = -radius; ry <= radius; ry++) {
                if (Mathf.dst2(rx, ry) <= (radius - 0.5f) * (radius - 0.5f)) {
                    int wx = x + rx, wy = y + ry;

                    if(wx < 0 || wy < 0 || wx >= Vars.world.width() || wy >= Vars.world.height()){
                            continue;
                    }
                    Vars.world.tile(wx, wy).setOverlayID(id);
                }
            }
        }
        Vars.playerGroup.all().list().forEach(Vars.netServer::sendWorldData);

        player.sendMessage(ConfigTranslate.get("cmd.spawnOre.ok"));
    }

    public static void setBlock(Player player, String[] args) {

        int x = Math.round(player.x/8);
        int y = Math.round(player.y/8);

        Block block = Vars.content.blocks().find(b -> b.name.equals(args[0]));

        if (block != null) {
            player.sendMessage(ConfigTranslate.get("cmd.setBlock.successful"));
            Vars.world.tile(x, y).setBlock(block);
            Vars.world.tile(x, y).setTeam(player.getTeam());

            Vars.playerGroup.all().list().forEach(Vars.netServer::sendWorldData);

        } else player.sendMessage(ConfigTranslate.get("cmd.setBlock.failed"));
    }

}
