package EtherHack.cheat;

import EtherHack.cheat.ui.Window;
import org.lwjglx.input.Keyboard;
import zombie.input.GameKeyboard;
import zombie.ui.UIManager;

public class EtherGUI {
    /**
     * Главное окно EtherHack
     */
    private Window etherWindow;

    /**
     * Флаг показа окна
     */
    private boolean isEnableGUI = true;

    /**
     * Проверяет была ли нажата кнопка
     */
    private boolean wasButtonDown = false;

    /**
     * Реализация инициализации окна EtherHack
     */
    public void InitGUI() {
        etherWindow = new Window();
        etherWindow.ResizeToFitY = false;
    }

    /**
     * Одновление пользовательского окна EtherHack
     */
    public void UpdateGUI() {
        boolean isDown = GameKeyboard.isKeyDown(Keyboard.KEY_NUMPAD5);

        if (isDown && !wasButtonDown) {
            isEnableGUI = !isEnableGUI;
        }

        wasButtonDown = isDown;

        if (!UIManager.UI.contains(etherWindow)) {
            UIManager.UI.add(etherWindow);
        }

        if (UIManager.UI.contains(etherWindow) && !isEnableGUI) {
            UIManager.UI.remove(etherWindow);
        }

        etherWindow.visible = isEnableGUI;
        etherWindow.setEnabled(isEnableGUI);
    }
}
