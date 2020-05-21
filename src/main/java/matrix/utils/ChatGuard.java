package matrix.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.UnaryOperator;

public class ChatGuard {

    public static UnaryOperator<String> removeColors = str -> {
        boolean ok = true;
        String resultStr;
        while (ok) if (str.contains("[") && str.contains("]")) {
            resultStr = str.substring(str.indexOf('[') + 1, str.indexOf(']'));
            str = str.replace("[" + resultStr + "]", "");
        } else ok = false;
        return str;
    };

    public static UnaryOperator<String> removeMentions = str -> {
        str = str.replaceAll("@([^<]*)", ConfigTranslate.get("pingDeleted"));
        str = str.replaceAll("<@([^<]*)>", ConfigTranslate.get("pingDeleted"));
        return str;
    };

    public static boolean check(String content) {
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
