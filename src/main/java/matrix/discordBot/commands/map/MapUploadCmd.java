package matrix.discordBot.commands.map;

import arc.Core;
import arc.files.Fi;
import matrix.utils.ConfigTranslate;
import mindustry.Vars;
import mindustry.io.SaveIO;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.InflaterInputStream;

public class MapUploadCmd {
    public static void main(MessageCreateEvent event) {
        TextChannel channel = event.getMessage().getChannel();
        if (channel == null)
            return;
        String message;
        List<MessageAttachment> ml = new LinkedList<>();
        for (MessageAttachment ma : event.getMessage().getAttachments()) {
            if (ma.getFileName().split("\\.", 2)[ 1 ].trim().equals("msav")) {
                ml.add(ma);
            }
        }
        if (ml.size() != 1) {
            message = ConfigTranslate.get("cmd.uploadMap.missing");
        } else if (Core.settings.getDataDirectory().child("maps").child(ml.get(0).getFileName()).exists()) {
            message = ConfigTranslate.get("cmd.uploadMap.already");

        } else {

            CompletableFuture<byte[]> cf = ml.get(0).downloadAsByteArray();
            Fi fh = Core.settings.getDataDirectory().child("maps").child(ml.get(0).getFileName());

            try {
                byte[] data = cf.get();
                if (!SaveIO.isSaveValid(new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data))))) {
                    message = ConfigTranslate.get("cmd.uploadMap.unknown");
                } else {
                    fh.writeBytes(data, false);
                    Vars.maps.reload();
                    message = ConfigTranslate.get("cmd.uploadMap.successful");
                }
            } catch (Exception e) {
                message = ConfigTranslate.get("cmd.uploadMap.error");
                e.printStackTrace();
            }
        }
        channel.sendMessage(message);
    }

}
