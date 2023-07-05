package EtherHack.hooks;

import EtherHack.hooks.interfaces.IStartFrameUIHookListener;
import lombok.experimental.UtilityClass;
import zombie.core.Core;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class OnStartFrameUIHook {

    /**
     * Список слушателей, ожидающих события от объекта {@code Core}.
     */
    private static List<IStartFrameUIHookListener> listeners = new ArrayList<>();

    /**
     * Регистрирует новый объект {@code IGameCoreListener} для уведомления о наступлении события в {@code Core}.
     *
     * @param listener объект слушателя для добавления. Не может быть {@code null}.
     * @throws NullPointerException если предоставленный слушатель равен {@code null}.
     */
    public static void addListener(IStartFrameUIHookListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null");
        }
        listeners.add(listener);
    }

    /**
     * Уведомляет всех зарегистрированных слушателей о наступлении события в {@code Core}.
     *
     * @param self экземпляр {@code Core}, в котором произошло событие.
     */
    public static void call(Core self){
        for (IStartFrameUIHookListener listener : listeners) {
            listener.onCall(self);
        }
    }
}
