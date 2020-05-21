package matrix.utils;

import arc.Events;
import matrix.discordbot.communication.SendToDiscord;
import mindustry.Vars;
import mindustry.game.EventType;

public class PlayersEvents {

    public void register() {
        Events.on(EventType.PlayerJoin.class, event -> TitleManager.main());
        Events.on(EventType.PlayerJoin.class, event -> {
            if (Boolean.parseBoolean(Config.get("botIsEnabled"))) {
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
        });
        Events.on(EventType.PlayerChatEvent.class, (event) -> {
            String msg = event.message;
            String nick = event.player.name;

            // Запускаем проверку на запрещенные слова
            if (!msg.startsWith("/")) {
                if (!event.player.isAdmin && Boolean.parseBoolean(Config.get("chatGuard"))) {
                    if (!ChatGuard.check(msg)) {
                        SendToDiscord.sendChatMessage(nick, msg, false);
                    } else event.player.sendMessage(ConfigTranslate.get("dontSwear"));
                } else {
                    SendToDiscord.sendChatMessage(nick, msg, true);
                }
            } else SendToDiscord.log(nick, msg);
        });
    }
}
