package EtherHack.Ether;

import EtherHack.hooks.OnUIElementPostRenderHook;
import EtherHack.hooks.interfaces.IOnUIElementPostRenderListener;
import EtherHack.utils.Info;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Platform;
import zombie.Lua.LuaManager;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.network.GameClient;

/**
 * Этот класс предоставляет API Ether для приложения.
 */
public class EtherAPI implements IOnUIElementPostRenderListener {
    public static EtherExposer exposer;

    /**
     * Включение режима разработки
     */
    public boolean isBypassDebugMode = false;

    /**
     * Включен ли режим бесконечной грузоподъемности
     */
    public boolean isUnlimitedCarry = false;

    /**
     * Конструктор класса EtherAPI.
     */
    public EtherAPI(){
        OnUIElementPostRenderHook.addListener(this);
    }

    /**
     * Инициализирует API Ether.
     */
    public void init(){
        if (exposer != null) {
            exposer.destroy();
        }
        exposer = new EtherExposer(LuaManager.converterManager, LuaManager.platform, LuaManager.env);
        exposer.exposeAPI();
    }

    /**
     * Реализация функционала при вызове хука OnUIElementPostRender
     */
    @Override
    public void onCall() {
        /**
         * Дебаг режим в мультиплеере
         */
        Core.bDebug = GameClient.bIngame && isBypassDebugMode;
    }

    /**
     * Внутренний класс, отвечающий за экспозицию API Ether.
     */
    public static final class EtherExposer extends LuaJavaClassExposer {
        /**
         * Конструктор класса EtherExposer.
         */
        public EtherExposer(KahluaConverterManager var1, Platform var2, KahluaTable var3) {
            super(var1, var2, var3);
        }

        /**
         * Экспонирует API Ether.
         */
        public void exposeAPI() {
            this.exposeGlobalFunctions(new GlobalEtherAPI());
        }
    }

    /**
     * Внутренний класс, предоставляющий глобальные функции API Ether.
     */
    public static class GlobalEtherAPI {
        /**
         * Конструктор класса GlobalEtherAPI.
         */
        public GlobalEtherAPI() {
        }

        @LuaMethod(
                name = "EtherRequire",
                global = true
        )
        public static Object EtherRequire(String path) {
            String fixedPath = path.endsWith(".lua") ? path : path + ".lua";

            if (!EtherMain.getInstance().etherGUI.luaDependenciesList.contains(fixedPath)) {
                EtherMain.getInstance().etherGUI.luaDependenciesList.add(fixedPath);
            }
            return LuaManager.RunLua(ZomboidFileSystem.instance.getString(fixedPath));
        }

        /**
         * Изменение режима бесконечного объема инвентаря
         */
        @LuaMethod(
                name = "setEtherUnlimitedCarry",
                global = true
        )
        public static void setEtherUnlimitedCarry(boolean isEnable) {
            EtherMain.getInstance().etherAPI.isUnlimitedCarry = isEnable;
        }

        /**
         * Получение состояния режима бесконечного объема инвентаря
         */
        @LuaMethod(
                name = "isEtherUnlimitedCarry",
                global = true
        )
        public static boolean isEtherUnlimitedCarry() {
            return EtherMain.getInstance().etherAPI.isUnlimitedCarry;
        }

        /**
         * Изменение режима разработки
         */
        @LuaMethod(
                name = "setEtherBypassDebugMode",
                global = true
        )
        public static void setEtherBypassDebugMode(boolean isEnable) {
            EtherMain.getInstance().etherAPI.isBypassDebugMode = isEnable;
        }

        /**
         * Получение состояния режима разработки
         */
        @LuaMethod(
                name = "isEtherBypassDebugMode",
                global = true
        )
        public static boolean isEtherBypassDebugMode() {
            return EtherMain.getInstance().etherAPI.isBypassDebugMode;
        }

        /**
         * Получение заголовка для окна EtherHack
         */
        @LuaMethod(
                name = "getEtherUITitle",
                global = true
        )
        public static String getEtherUITitle() {
            return Info.CHEAT_GUI_TITLE;
        }

        /**
         * Получение заголовка для окна EtherHack
         */
        @LuaMethod(
                name = "isEtherInGame",
                global = true
        )
        public static Boolean isEtherInGame() {
            return GameClient.bIngame;
        }

        /**
         * Получение прав доступа администратора
         */
        @LuaMethod(
                name = "setEtherAdminAccess",
                global = true
        )
        public static void setEtherAdminAccess() {
            for (IsoPlayer p : GameClient.instance.getPlayers()) {
                if (p.isLocalPlayer()) {
                    p.accessLevel = "admin";
                    p.accessLevel.equals("admin");
                }
            }
        }
    }
}
