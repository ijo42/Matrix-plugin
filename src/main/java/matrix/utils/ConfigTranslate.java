package matrix.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigTranslate {

    public static String get(String nameStr) {
        String out = "TRANSLATE_ERROR("+nameStr+")";
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream("config/mods/Matrix/bundles/"+Config.get("language")+".properties");
            prop.load(fileInputStream);
            if (prop.getProperty(nameStr) != null) {
                out = prop.getProperty(nameStr);
                out = new String(out.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

}
