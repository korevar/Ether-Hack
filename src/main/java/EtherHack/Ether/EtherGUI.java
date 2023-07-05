package EtherHack.cheat;

import EtherHack.cheat.ui.Window;
import EtherHack.hooks.GameCoreHook;
import EtherHack.hooks.IGameCoreListener;
import org.lwjglx.input.Keyboard;
import zombie.core.Core;
import zombie.input.GameKeyboard;
import zombie.ui.UIManager;

import java.util.concurrent.atomic.AtomicBoolean;

public class EtherGUI implements IGameCoreListener {
    /**
     * Конструктор, реализующий подписку на события GameCoreHook
     */
    public EtherGUI() {
        GameCoreHook.addListener(this);
    }

    /**
     * Главное окно EtherHack
     */
    private Window etherWindow;

    /**
     * Флаг показа окна
     */
    private boolean isEnableGUI = true;

    /**
     * Проверка, инциализированн ли пользовательский интерфейс
     */
    private static final AtomicBoolean isGUIInitialized = new AtomicBoolean(false);

    /**
     * Проверяет была ли нажата кнопка
     */
    private boolean wasButtonDown = false;

    /**
     * Проверяет, готово ли UI API к инициализации
     */
    private static boolean isGUIReadyToInit() {
        return !UIManager.UI.isEmpty();
    }

    /**
     * Реализация инициализации окна EtherHack
     */
    public void initGUI() {
        etherWindow = new Window();
        etherWindow.ResizeToFitY = false;
    }

    /**
     * Одновление пользовательского окна EtherHack
     */
    private void updateGUI(){

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

    /**
     * Реализация функционала при вызове хука GameCoreHook
     */
    @Override
    public void onCall(Core self) {
        if (isGUIReadyToInit()) {
            if (isGUIInitialized.compareAndSet(false, true)) {
                initGUI();
            }

            updateGUI();
        }
    }
}
