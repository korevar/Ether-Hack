package EtherHack.Ether;

import EtherHack.utils.Info;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Platform;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.network.GameClient;

/**
 * Этот класс предоставляет API Ether для приложения.
 */
public class EtherAPI  {
    public static EtherExposer exposer;

    /**
     * Включение режима разработки
     */
    public boolean isBypassDebugMode = false;

    /**
     * Конструктор класса EtherAPI.
     */
    public EtherAPI(){
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
         * Получение данных о работе режима разработки
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

        /**
         * Выдача предметов
         */
        @LuaMethod(
                name = "addEtherItem",
                global = true
        )
        public static void addEtherItem(String id, int amount) {
            for (IsoPlayer p : GameClient.instance.getPlayers()) {
                if (p.isLocalPlayer()) {
                    p.getInventory().AddItems(id, amount);
                }
            }
        }

        /**
         * Установка стандартных навыков на максимальный уровень
         */
        @LuaMethod(
                name = "setEtherMaxDefaultSkill",
                global = true
        )
        public static void setEtherMaxDefaultSkill() {
            for (IsoPlayer p : GameClient.instance.getPlayers()) {
                if (p.isLocalPlayer()) {
                    for (int i = 0; i < 400; i++) {
                        p.getXp().AddXP(PerkFactory.Perks.Fitness, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Strength, 1000);
                    }
                    for (int i = 0; i < 200; i++) {
                        p.getXp().AddXP(PerkFactory.Perks.Axe, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Blunt, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.SmallBlunt, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.LongBlade, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.SmallBlade, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Spear, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Maintenance, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Aiming, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Reloading, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Cooking, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Farming, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Doctor, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Electricity, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.MetalWelding, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Mechanics, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Tailoring, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Fishing, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Trapping, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Sprinting, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Nimble, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Sneak, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Lightfoot, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.PlantScavenging, 1000);
                        p.getXp().AddXP(PerkFactory.Perks.Woodwork, 1000);
                    }
                }
            }
        }
    }
}
