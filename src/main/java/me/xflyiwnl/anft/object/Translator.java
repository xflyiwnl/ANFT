package me.xflyiwnl.anft.object;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.colorfulgui.util.TextUtil;

public class Translator {

    public static String of(String path) {
        return TextUtil.colorize(ANFT.getInstance().getFileManager().getLanguage().yaml().getString("language." + path));
    }

}
