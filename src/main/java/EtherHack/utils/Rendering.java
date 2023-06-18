package EtherHack.utils;

import lombok.experimental.UtilityClass;
import zombie.ui.TextManager;

/**
 * Предоставляет доступ к функциональности рендеринга в игре.
 */
@UtilityClass
public class Rendering {
    /**
     * Рисует текст поверх игрового окна
     */
    public static void DrawText(String text, float x, float y, float r, float g, float b, float a) {
        TextManager.instance.DrawString(x, y, text, r, g, b, a);
    }
}
