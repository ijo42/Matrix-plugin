package matrix.utils;

import mindustry.gen.Call;

public class TitleManager {
    public static void main() {
        Call.onInfoToast(ConfigTranslate.get("title"), 5000);
    }
}
