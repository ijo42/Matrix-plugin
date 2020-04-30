package matrix.utils;

import arc.util.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Config {

    public static void main() {
        // Создаём папку если не существует
        final File dir1 = new File("config/mods/Matrix");
        if (!dir1.exists()) {
            if (!dir1.mkdir()) {
                Log.warn("Dir creation fail");
                return;
            }
        }

        final File dir2 = new File("config/mods/Matrix/bundles");
        if (!dir2.exists()) {
            if (!dir2.mkdir()) {
                Log.warn("Dir creation fail");
                return;
            }
        }

        File file1 = new File("config/mods/Matrix/config.properties");
        if (!file1.exists()) {
            Log.warn("The config file was successfully generated.");
            Log.warn("Don't forget to change the token in the config.");

            try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties");
                 OutputStream out = new FileOutputStream("config/mods/Matrix/config.properties")) {
                int data;
                assert in != null;
                while ((data = in.read()) != -1) {
                    out.write(data);
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }

        File file2 = new File("config/mods/Matrix/bundles/ru_RU.properties");
        if(!file2.exists()) {
            try (InputStream in = Config.class
                    .getClassLoader()
                    .getResourceAsStream("bundles/ru_RU.properties");
                 OutputStream out = new FileOutputStream("config/mods/Matrix/bundles/ru_RU.properties")) {
                int data;
                assert in != null;
                while ((data = in.read()) != -1) {
                    out.write(data);
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }

        File file4 = new File("config/mods/Matrix/bundles/en_US.properties");
        if(!file4.exists()) {
            try (InputStream in = Config.class
                    .getClassLoader()
                    .getResourceAsStream("bundles/en_US.properties");
                 OutputStream out = new FileOutputStream("config/mods/Matrix/bundles/en_US.properties")) {
                int data;
                assert in != null;
                while ((data = in.read()) != -1) {
                    out.write(data);
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }

        File file3 = new File("config/mods/Matrix/ChatGuard.properties");
        if(!file3.exists()) {
            try (InputStream in = Config.class
                    .getClassLoader()
                    .getResourceAsStream("ChatGuard.properties");
                 OutputStream out = new FileOutputStream("config/mods/Matrix/ChatGuard.properties")) {
                int data;
                assert in != null;
                while ((data = in.read()) != -1) {
                    out.write(data);
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }

    }

    public static String get(String nameStr) {
        String out = "CONFIG_ERROR";
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream("config/mods/Matrix/config.properties");
            prop.load(fileInputStream);
            out = prop.getProperty(nameStr);
            out = new String(out.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }
}
