package EtherHack.Ether;

import EtherHack.gameStates.EtherLogoState;
import EtherHack.utils.Logger;
import zombie.GameWindow;
import zombie.gameStates.GameState;
import zombie.gameStates.TISLogoState;

import java.util.List;

public class EtherLogo {

    private static EtherLogo instance;

    /**
     * Предотвращение создание экземпляров из вне
     */
    private EtherLogo() {
    }

    /**
     * Реализация инициализации состояния logo
     */
    public void initLogoState(){

        List<GameState> states = GameWindow.states.States;
        GameState tisLogoState = states.get(0);

        if (tisLogoState instanceof TISLogoState)
        {
            GameWindow.states.States.add(0, new EtherLogoState());
            GameWindow.states.LoopToState = 1;
        }
        else {
            Logger.printLog("Error when initializing the EtherLogo!");
        }
    }

    /**
     * Получение текущего экземпляра синглтона EtherLogo
     * @return Экземпляр EtherLogo
     */
    public static EtherLogo getInstance() {
        if (instance == null) {
            instance = new EtherLogo();
        }
        return instance;
    }
}
