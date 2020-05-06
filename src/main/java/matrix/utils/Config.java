package matrix.utils;

import arc.util.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Config {
    private static Properties prop;
    private static final String MAIN_CONFIG = "config/mods/Matrix/config.properties";
    private static final String CHAT_GUARD_CONFIG = "config/mods/Matrix/ChatGuard.properties";
    private static final String MAIN_DIR = "config/mods/Matrix";
    private static final String BUNDLES_DIR = "config/mods/Matrix/bundles";

    public static void main() {
        // Создаём папку если не существует
        final File dir1 = new File(MAIN_DIR);
        if (!dir1.exists()) {
            if (!dir1.mkdir()) {
                Log.warn("Dir creation fail");
                return;
            }
        }

        final File dir2 = new File(BUNDLES_DIR);
        if (!dir2.exists()) {
            if (!dir2.mkdir()) {
                Log.warn("Dir creation fail");
                return;
            }
        }

        File file1 = new File(MAIN_CONFIG);
        if (!file1.exists()) {
            Log.warn("The config file was successfully generated.");
            Log.warn("Don't forget to change the token in the config.");

            try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties");
                 OutputStream out = new FileOutputStream(MAIN_CONFIG)) {
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

        File file3 = new File(CHAT_GUARD_CONFIG);
        if(!file3.exists()) {
            try (InputStream in = Config.class
                    .getClassLoader()
                    .getResourceAsStream("ChatGuard.properties");
                 OutputStream out = new FileOutputStream(CHAT_GUARD_CONFIG)) {
                int data;
                assert in != null;
                while ((data = in.read()) != -1) {
                    out.write(data);
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
        try {
            FileInputStream fileInputStream;
            prop = new Properties();
            fileInputStream = new FileInputStream(MAIN_CONFIG);
            prop.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String nameStr) {
        String out = "CONFIG_ERROR";
        try {
            out = prop.getProperty(nameStr);
            out = new String(out.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (NullPointerException npe) {
            Log.err("Config missed an {0} parameter".replace("{0}", nameStr));
        }
        return out;
    }

    public static boolean has(String nameStr) {
        String out;
        out = prop.getProperty(nameStr);
        out = new String(out.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return !out.isEmpty();
    }

    public static void set(String key, String value) {
        OutputStream out = null;
        try {
            prop.setProperty(key, value);
            out = new FileOutputStream(MAIN_CONFIG);
            prop.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
