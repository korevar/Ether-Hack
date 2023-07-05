package EtherHack.utils;

import lombok.experimental.UtilityClass;
import zombie.debug.DebugLog;

@UtilityClass
public class Console {
    /**
     * Вывод текста в консоль
     * @param text Текст для вывода
     */
    public static void print(String text) {
        System.out.println(Info.CHEAT_TAG + text);
    }

    /**
     * Вывод текста в log файл с типом логирования LUA
     * @param text Текст для вывода
     */
    public static void printLuaLog(String text) {
        DebugLog.Lua.println(Info.CHEAT_TAG + text);
    }

    /**
     * Вывод текста в log файл с типом логирования General
     * @param text Текст для вывода
     */
    public static void printLog(String text){
        DebugLog.General.print(Info.CHEAT_TAG + text);
    }
}
