package matrix.discordbot.commands;

import matrix.Matrix;
import matrix.discordbot.Bot;
import matrix.utils.Config;
import matrix.utils.ConfigTranslate;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.*;
import java.util.Optional;
import java.util.Properties;


public class SetMsgChannel {

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
        Matrix.INSTANCE.getBot().sendMessage(channel.getIdAsString(), (ConfigTranslate.get("cmd.setMsgChannel.ok")));
        String id = channel.getIdAsString();

        File file = new File("config/mods/Matrix/config.properties");

        InputStream in = null;
        OutputStream out = null;

        Properties props = new Properties();
        try {
            in = new FileInputStream(file);
            props.load(in);
            props.setProperty("discordChannelId", id);
            out = new FileOutputStream(file);
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null ) {
                try{in.close();}catch(IOException e){e.printStackTrace();}
            }
            if (out != null) {
                try{out.close();}catch(IOException e){e.printStackTrace();}
            }
        }
    }
}