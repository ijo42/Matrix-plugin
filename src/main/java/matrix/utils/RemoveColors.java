package matrix.utils;

import java.util.function.UnaryOperator;

public class RemoveColors {

    public static UnaryOperator<String> remove = str -> {
        boolean ok = true;
        String resultStr;
        while (ok) if (str.contains("[") && str.contains("]")) {
            resultStr = str.substring(str.indexOf('[') + 1, str.indexOf(']'));
            str = str.replace("[" + resultStr + "]", "");
        } else ok = false;
        return str;
    };
}
