package EtherHack.hooks;

import EtherHack.cheat.EtherGUI;
import EtherHack.cheat.EtherMain;
import zombie.core.Core;
import lombok.experimental.UtilityClass;
import zombie.ui.UIManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@UtilityClass
public class GameCoreHook {
    private static final AtomicBoolean isInitialized = new AtomicBoolean(false);

    /**
     * Список слушателей, ожидающих события от объекта {@code Core}.
     */
    private static List<IGameCoreListener> listeners = new ArrayList<>();

    /**
     * Регистрирует новый объект {@code IGameCoreListener} для уведомления о наступлении события в {@code Core}.
     *
     * @param listener объект слушателя для добавления. Не может быть {@code null}.
     * @throws NullPointerException если предоставленный слушатель равен {@code null}.
     */
    public static void addListener(IGameCoreListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null");
        }
        listeners.add(listener);
    }

    /**
     * Проверяет, готово ли API к инициализации
     */
    private static boolean isReadyToInit() {
        return !UIManager.UI.isEmpty();
    }

    /**
     * Уведомляет всех зарегистрированных слушателей о наступлении события в {@code Core}.
     *
     * @param self экземпляр {@code Core}, в котором произошло событие.
     */
    public static void Call(Core self){
        for (IGameCoreListener listener : listeners) {
            listener.onCall(self);
        }

        if (isReadyToInit()) {
            if (isInitialized.compareAndSet(false, true)) {
                EtherMain.getInstance().InitClientGUI();
            }

            EtherMain.getInstance().etherGUI.UpdateGUI();
        }
    }
}
