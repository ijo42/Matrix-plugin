package matrix.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

public class ChatGuard {

    public static boolean check (String content){
        return Arrays.stream(get()).anyMatch(x -> x.equalsIgnoreCase(content));
    }
    
    public static String[] get() {
        String out;
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream("config/mods/Matrix/ChatGuard.properties");
            prop.load(fileInputStream);
            out = prop.getProperty("forbiddenWords");
            out = new String(out.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            return out.split(";");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

}
