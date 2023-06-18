package EtherHack.cheat;

public class EtherMain {

    private static EtherMain instance;

    public EtherHackCredits etherHackCredits;
    public EtherGUI etherGUI;

    /**
     * Предотвращение создание экземпляров из вне
     */
    private EtherMain() {
    }

    /**
     * Реализация инициализации EtherHack
     */
    private void InitEther(){
        etherHackCredits = new EtherHackCredits();
    }

    /**
     * Реализация инициализации пользовательского интерфейса EtherHack
     */
    public void InitClientGUI() {
        etherGUI = new EtherGUI();
        etherGUI.InitGUI();
    }

    /**
     * Получение текущего экземпляра синглтона EtherHack
     * @return Экземпляр EtherMain
     */
    public static EtherMain getInstance() {
        if (instance == null) {
            instance = new EtherMain();
            instance.InitEther();
        }
        return instance;
    }
}
