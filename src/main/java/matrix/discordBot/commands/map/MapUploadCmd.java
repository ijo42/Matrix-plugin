package matrix.discordBot.commands.map;

import arc.Core;
import arc.files.Fi;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.channel.MessageChannel;
import matrix.utils.ConfigTranslate;
import mindustry.Vars;
import mindustry.io.SaveIO;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.InflaterInputStream;

public class MapUploadCmd {
    public static void main(MessageCreateEvent event) {
        MessageChannel channel = event.getMessage().getChannel().block();
        if (channel == null)
            return;
        String message;
        List<Attachment> ml = new LinkedList<>();
        for (Attachment ma : event.getMessage().getAttachments()) {
            if (ma.getFilename().split("\\.", 2)[ 1 ].trim().equals("msav")) {
                ml.add(ma);
            }
        }
        if (ml.size() != 1) {
            message = ConfigTranslate.get("cmd.uploadMap.missing");
        } else if (Core.settings.getDataDirectory().child("maps").child(ml.get(0).getFilename()).exists()) {
            message = ConfigTranslate.get("cmd.uploadMap.already");

        } else {
            //more custom filename checks possible

            byte[] cf = ofUrl(ml.get(0).getUrl()).block();
            Fi fh = Core.settings.getDataDirectory().child("maps").child(ml.get(0).getFilename());

            try {
                if (cf == null)
                    throw new NullPointerException("InputStream is null :/");
                if (!SaveIO.isSaveValid(new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(cf))))) {
                    message = ConfigTranslate.get("cmd.uploadMap.unknown");
                } else {
                    fh.writeBytes(cf, false);
                    Vars.maps.reload();
                    message = ConfigTranslate.get("cmd.uploadMap.successful");
                }
            } catch (Exception e) {
                message = ConfigTranslate.get("cmd.uploadMap.error");
                e.printStackTrace();
            }
        }
        channel.createMessage(message);
    }

    public static Mono<byte[]> ofUrl(final String url) {
        return HttpClient.create()
                .get()
                .uri(url)
                .responseSingle((res, body) -> body.asByteArray());
    }

}
