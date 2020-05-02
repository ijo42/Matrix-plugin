package matrix.discordbot.commands.map;

import arc.Core;
import arc.files.Fi;
import matrix.discordbot.Bot;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import mindustry.Vars;
import mindustry.io.SaveIO;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.zip.InflaterInputStream;

public class UploadCmd {
    public static void main(MessageCreateEvent event) {
        TextChannel channel = event.getMessage().getChannel();
        Optional<Role> optRole = Bot.getRoleFromID(Config.get("stuffRoleID"));
        if (event.isPrivateMessage() || channel == null || !event.getMessageAuthor().asUser().isPresent() || !event.getServer().isPresent() || !optRole.isPresent())
            return;

        if (!event.getMessageAuthor().asUser().get().getRoles(event.getServer().get())
                .contains(optRole.get())) {
            channel.sendMessage(ConfigTranslate.get("noPerm"));
            return;
        }

        String message;
        MessageAttachment attachment = event.getMessage().getAttachments().get(0);
        if (attachment == null || !isMSAVFile(attachment)) {
            message = ConfigTranslate.get("cmd.uploadMap.missing");
        } else if (Core.settings.getDataDirectory().child("maps").child(attachment.getFileName()).exists()) {
            message = ConfigTranslate.get("cmd.uploadMap.already");
        } else {
            CompletableFuture<byte[]> cf = attachment.downloadAsByteArray();
            Fi fh = Core.settings.getDataDirectory().child("maps").child(attachment.getFileName());

            try {
                byte[] data = cf.get();
                if (!SaveIO.isSaveValid(new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data))))) {
                    message = ConfigTranslate.get("cmd.uploadMap.unknown");
                } else {
                    fh.writeBytes(data, false);
                    Vars.maps.reload();
                    message = ConfigTranslate.get("cmd.uploadMap.successful").replace("{0}", attachment.getFileName());
                }
            } catch (java.lang.RuntimeException e) {
                if (!e.getMessage().equals("incorrect header check"))
                    e.printStackTrace();
                message = ConfigTranslate.get("cmd.uploadMap.error");

            } catch (Exception e) {
                message = ConfigTranslate.get("cmd.uploadMap.error");
                e.printStackTrace();
            }
        }
        channel.sendMessage(message);
    }

    private static boolean isMSAVFile(MessageAttachment attachment) {
        return attachment.getFileName().endsWith(Vars.mapExtension);
    }

}
