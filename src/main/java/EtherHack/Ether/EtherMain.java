package EtherHack.Ether;

public class EtherMain {

    private static EtherMain instance;
    public EtherHackCredits etherHackCredits;
    public EtherGUI etherGUI;
    public EtherAPI etherAPI;
    public EtherInitializer etherInitializer;

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
        etherInitializer = new EtherInitializer();
        etherAPI = new EtherAPI();
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
