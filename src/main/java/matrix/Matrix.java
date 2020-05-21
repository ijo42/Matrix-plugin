package matrix;

import arc.util.CommandHandler;
import arc.util.Log;
import matrix.commands.client.ClientCommands;
import matrix.commands.client.SetTeam;
import matrix.discordbot.Bot;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import matrix.utils.PlayersEvents;
import matrix.utils.SystemInfo;
import mindustry.plugin.Plugin;

public class Matrix extends Plugin {

    public static Matrix INSTANCE;
    private final Bot bot;

    public Matrix() {
        Config.main();
        INSTANCE = this;
        bot = new Bot();
        PlayersEvents events = new PlayersEvents();
        events.register();
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        handler.register("ping", "Return \"Pong!\"", arg -> Log.info("Pong!"));

        handler.register("memory", "Return \"Pong!\"", arg -> {
            Log.info("SYSTEM CPU LOAD: " + SystemInfo.cpu() + "%");
            Log.info("PROCESS CPU LOAD: " + SystemInfo.cpuProcess() + "%");
            Log.info("TOTAL RAM LOAD: " + (100 - SystemInfo.ram()) + "%");
        });

    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        ClientCommands comm = new ClientCommands();
        handler.register(ConfigTranslate.get("cmd.setTeam.name"), ConfigTranslate.get("cmd.setTeam.params"), ConfigTranslate.get("cmd.setTeam.description"), SetTeam::set);
        handler.register(ConfigTranslate.get("cmd.gameOver.name"), ConfigTranslate.get("cmd.gameOver.params"), ConfigTranslate.get("cmd.gameOver.description"), ClientCommands::gameOverCommand);
        handler.register(ConfigTranslate.get("cmd.tp.name"), ConfigTranslate.get("cmd.tp.params"), ConfigTranslate.get("cmd.tp.description"), ClientCommands::tpCommand);
        handler.register(ConfigTranslate.get("cmd.tpToPlayer.name"), ConfigTranslate.get("cmd.tpToPlayer.params"), ConfigTranslate.get("cmd.tpToPlayer.description"), ClientCommands::teleportToPlayerCommand);
        handler.register(ConfigTranslate.get("cmd.spawnOre.name"), ConfigTranslate.get("cmd.spawnOre.params"), ConfigTranslate.get("cmd.spawnOre.description"), ClientCommands::spawnOreCommand);
        handler.register(ConfigTranslate.get("cmd.setBlock.name"), ConfigTranslate.get("cmd.setBlock.params"), ConfigTranslate.get("cmd.setBlock.description"), ClientCommands::setBlockCommand);
        handler.register(ConfigTranslate.get("cmd.infiniteResources.name"), "<on/off>", ConfigTranslate.get("cmd.infiniteResources.description"), ClientCommands::infinityResourceCommand);
        handler.register(ConfigTranslate.get("cmd.broadcast.name"), "<info...>", ConfigTranslate.get("cmd.broadcast.description"), ClientCommands::broadcastCommand);
        handler.register(ConfigTranslate.get("cmd.memory.name"), "", ConfigTranslate.get("cmd.memory.description"), ClientCommands::memoryCommand);
        handler.register(ConfigTranslate.get("cmd.sendWaves.name"), "<count>", "Run as much as you want waves.", ClientCommands::sendWaves);
        handler.register(ConfigTranslate.get("cmd.grief.name"), ConfigTranslate.get("cmd.grief.params"), ConfigTranslate.get("cmd.grief.description").replace("{0}", ConfigTranslate.get("cmd.grief.name")), comm::griefCommand);
        handler.register(ConfigTranslate.get("cmd.js.name"), "<script...>", "Run arbitrary Javascript.", ClientCommands::jsCommand);
    }

    public Bot getBot() {
        return bot;
    }
}

