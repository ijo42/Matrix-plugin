package matrix.discordBot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import matrix.utils.ConfigTranslate;

import java.io.*;
import java.util.Properties;


public class SetMsgChannel {

    public static void main(MessageCreateEvent event) {
        MessageChannel channel = event.getMessage().getChannel().block();
        if (channel == null)
            return;
        /*if(!event.getMember().get().hasHigherRoles(){//.block().contains(Permission.ADMINISTRATOR)) {
            channel.createMessage(ConfigTranslate.get("noPerm")).block();
            return;
        }*/ //TODO:Perms check
        channel.createMessage(ConfigTranslate.get("cmd.setMsgChannel.ok")).block();
        String id = channel.getId().asString();

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