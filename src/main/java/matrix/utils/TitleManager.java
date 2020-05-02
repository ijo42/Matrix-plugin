package matrix.utils;

import mindustry.gen.Call;

public class TitleManager {
    public static void main() {
        if (Boolean.parseBoolean(Config.get("titleEnabled")))
            Call.onInfoToast(ConfigTranslate.get("title"), 5000);
    }
}
