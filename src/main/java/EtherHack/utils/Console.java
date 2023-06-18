package EtherHack.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Console {
    public static void Print(String text) {
        System.out.println(Info.CHEAT_NAME_TAG + text);
    }
}
