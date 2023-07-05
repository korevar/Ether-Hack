package EtherHack.cheat;

import java.util.concurrent.atomic.AtomicBoolean;

public class EtherMain {

    private static EtherMain instance;
    public EtherHackCredits etherHackCredits;
    public EtherGUI etherGUI;
    private static final AtomicBoolean isInitialized = new AtomicBoolean(false);

    /**
     * Предотвращение создание экземпляров из вне
     */
    private EtherMain() {
    }

    /**
     * Реализация инициализации EtherHack
     */
    public void initEther(){
        etherHackCredits = new EtherHackCredits();
        etherGUI = new EtherGUI();
    }

    /**
     * Получение текущего экземпляра синглтона EtherHack
     * @return Экземпляр EtherMain
     */
    public static EtherMain getInstance() {
        if (instance == null) {
            instance = new EtherMain();
        }
        return instance;
    }
}
