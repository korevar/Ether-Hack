package EtherHack.cheat;

import EtherHack.hooks.GameCoreHook;
import EtherHack.hooks.IGameCoreListener;
import EtherHack.utils.Info;
import EtherHack.utils.Rendering;
import zombie.core.Core;

public class EtherHackCredits implements IGameCoreListener {

    /**
     * Конструктор, реализующий подписку на события GameCoreHook
     */
    public EtherHackCredits() {
        GameCoreHook.addListener(this);
    }

    /**
     * Реализация функционала при вызове хука GameCoreHook
     */
    @Override
    public void onCall(Core self) {
        float screenHeight = (float)self.getScreenHeight();

        Rendering.DrawText(Info.CHEAT_NAME_DISPLAY, 15.0f, screenHeight - 30.0f, 255.0f, 255.0f, 255.0f, 255.0f);
        Rendering.DrawText(Info.CHEAT_AUTHOR_DISPLAY, 15.0f, screenHeight - 15.0f, 255.0f, 255.0f, 255.0f, 255.0f);
    }
}
