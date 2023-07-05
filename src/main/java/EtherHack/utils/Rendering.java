package EtherHack.utils;

import lombok.experimental.UtilityClass;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

/**
 * Предоставляет доступ к функциональности рендеринга в игре.
 */
@UtilityClass
public class Rendering {
    /**
     * Рисует текст поверх игрового окна
     */
    public static void DrawText(String text, UIFont font, float x, float y, float r, float g, float b, float a) {
        TextManager.instance.DrawString(font, x, y, text, r, g, b, a);
    }

    /**
     * Рисует текст поверх игрового окна
     */
    public static void DrawTextRight(String text, UIFont font, float x, float y, float r, float g, float b, float a) {
        TextManager.instance.DrawStringRight(font, x, y, text, r, g, b, a);
    }
}
