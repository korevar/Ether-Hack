package EtherHack.Ether;

import EtherHack.hooks.OnUIElementPostRenderHook;
import EtherHack.hooks.interfaces.IOnUIElementPostRenderListener;
import EtherHack.utils.Logger;
import org.lwjglx.input.Keyboard;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.input.GameKeyboard;
import zombie.network.GameClient;

import java.util.concurrent.atomic.AtomicBoolean;

public class EtherGUI implements IOnUIElementPostRenderListener {
    public EtherAPI etherAPI;

    /**
     * Путь до главного файла UI
     */
    private final String PATH_LUA_UI_MAIN = "EtherHack/lua/EtherHackMenu.lua";

    /**
     * Флаг, был ли загружен API EtherHack
     */
    private AtomicBoolean isAPIInitialized = new AtomicBoolean(false);


    /**
     * Конструктор, реализующий подписку на события OnUIElementPostRender
     */
    public EtherGUI() {
        OnUIElementPostRenderHook.addListener(this);
        etherAPI = new EtherAPI();
    }

    /**
     * Компиляция UI Lua
     */
    public void loadLuaUI(){
        if(!LuaManager.loaded.contains(PATH_LUA_UI_MAIN)){
            Logger.printLog("Loading EtherHackUI...");
            LuaManager.RunLua(PATH_LUA_UI_MAIN, false);
        }
    }
    /**
     * Реализация функционала при вызове хука OnUIElementPostRender
     */
    @Override
    public void onCall() {
        if (isAPIInitialized.compareAndSet(false, true) || (isAPIInitialized.get() && !LuaManager.loaded.contains(PATH_LUA_UI_MAIN))){
            Logger.printLog("Initializing EtherAPI...");
            etherAPI.init();
        }

        /**
         * Дебаг режима в мультиплеере
         */
        Core.bDebug = GameClient.bIngame && etherAPI.isBypassDebugMode;

        loadLuaUI();

    }


}
