package EtherHack.cheat.ui;

import EtherHack.handlers.AbstractEventHandler;
import EtherHack.utils.Info;
import org.lwjglx.input.Keyboard;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.input.GameKeyboard;
import zombie.network.GameClient;
import zombie.ui.NewWindow;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.ui.UITextBox2;


public class Window extends NewWindow {

    private final UITextBox2 titleGUIBox;
    private final UITextBox2 giveGUIInputName;
    private final UITextBox2 giveGUIInputAmount;

    /**
     * Создание кнопок в GUI
     */
    private void InitGUIButtons(){
        /**
         * Кнопка добавления опыта
         */
        AddChild(new Button(new AbstractEventHandler() {
            @Override
            public void Selected(String s, int toggled, int i1) {
                for (IsoPlayer p : GameClient.instance.getPlayers()) {
                    if (p.isLocalPlayer()) {
                        for (int i = 0; i < 300; i++) {
                            p.getXp().AddXP(PerkFactory.Perks.Fitness, 1000);
                            p.getXp().AddXP(PerkFactory.Perks.Strength, 1000);
                        }

                        for (int i = 0; i < 110; i++) {
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
        }, 5, 30, "Skill", "skill_button"));

        /**
         * Кнопка получения Noclip
         */
        AddChild(new Button(new AbstractEventHandler() {
            @Override
            public void Selected(String s, int toggled, int i1) {
                for (IsoPlayer p : GameClient.instance.getPlayers()) {
                    if (p.isLocalPlayer()) {
                        p.setNoClip(toggled == 1);
                        GameClient.sendPlayerExtraInfo(p);
                    }
                }
            }
        }, 55, 30, "+Noclip", "noclip_button"));

        /**
         * Кнопка получения UnNoclip
         */
        AddChild(new Button(new AbstractEventHandler() {
            @Override
            public void Selected(String s, int toggled, int i1) {
                for (IsoPlayer p : GameClient.instance.getPlayers()) {
                    if (p.isLocalPlayer()) {
                        p.setNoClip(toggled == 0);
                        GameClient.sendPlayerExtraInfo(p);
                    }
                }
            }
        }, 105, 30, "-Noclip", "unnoclip_button"));

        /**
         * Кнопка получения Ghost
         */
        AddChild(new Button(new AbstractEventHandler() {
            @Override
            public void Selected(String s, int toggled, int i1) {
                for (IsoPlayer p : GameClient.instance.getPlayers()) {
                    if (p.isLocalPlayer()) {
                        p.setInvisible(toggled == 1);
                        p.setGhostMode(toggled == 1);
                    }
                }
            }
        }, 155, 30, "+Ghost", "ghost_button"));
        /**
         * Кнопка получения UnGhost
         */
        AddChild(new Button(new AbstractEventHandler() {
            @Override
            public void Selected(String s, int toggled, int i1) {
                for (IsoPlayer p : GameClient.instance.getPlayers()) {
                    if (p.isLocalPlayer()) {
                        p.setInvisible(toggled == 0);
                        p.setGhostMode(toggled == 0);
                        GameClient.sendPlayerExtraInfo(p);
                    }
                }
            }
        }, 205, 30, "-Ghost", "unghost_button"));


        /**
         * Кнопка получения God
         */
        AddChild(new Button(new AbstractEventHandler() {
            @Override
            public void Selected(String s, int toggled, int i1) {
                for (IsoPlayer p : GameClient.instance.getPlayers()) {
                    if (p.isLocalPlayer()) {
                        p.setGodMod(toggled == 1);
                        p.setCanHearAll(toggled == 1);
                        GameClient.sendPlayerExtraInfo(p);
                    }
                }
            }
        }, 5, 50, "+God", "god_button"));

        /**
         * Кнопка получения UnGod
         */
        AddChild(new Button(new AbstractEventHandler() {
            @Override
            public void Selected(String s, int toggled, int i1) {
                for (IsoPlayer p : GameClient.instance.getPlayers()) {
                    if (p.isLocalPlayer()) {
                        p.setGodMod(toggled == 0);
                        p.setCanHearAll(toggled == 0);
                        GameClient.sendPlayerExtraInfo(p);
                    }
                }
            }
        }, 55, 50, "-God", "ungod_button"));

        /**
         * Кнопка получения Weight
         */
        AddChild(new Button(new AbstractEventHandler() {
            @Override
            public void Selected(String s, int toggled, int i1) {
                for (IsoPlayer p : GameClient.instance.getPlayers()) {
                    if (p.isLocalPlayer()) {
                        p.setUnlimitedCarry(toggled == 1);
                        p.setMaxWeightDelta(1000f);
                        p.setMaxWeight(1000);
                        GameClient.sendPlayerExtraInfo(p);
                    }
                }
            }
        }, 105, 50, "Weight", "weight_button"));

        /**
         * Кнопка получения StarterPack
         */
        AddChild(new Button(new AbstractEventHandler() {
            @Override
            public void Selected(String s, int toggled, int i1) {
                for (IsoPlayer p : GameClient.instance.getPlayers()) {
                    if (p.isLocalPlayer()) {

                        // Оружие
                        p.getInventory().AddItems("Base.Katana", 1);
                        p.getInventory().AddItems("Base.Pistol", 1);
                        p.getInventory().AddItems("Base.Bullets9mmBox", 5);
                        p.getInventory().AddItems("Base.9mmClip", 10);

                        // Одежда
                        p.getInventory().AddItems("Base.Tshirt_CamoGreen", 1);
                        p.getInventory().AddItems("Base.Shirt_CamoGreen", 1);
                        p.getInventory().AddItems("Base.Base.WeldingMask", 1);
                        p.getInventory().AddItems("Base.Vest_Hunting_Camo", 1);
                        p.getInventory().AddItems("Base.Trousers_CamoGreen", 1);
                        p.getInventory().AddItems("Base.Socks_Long", 1);
                        p.getInventory().AddItems("Base.Shoes_ArmyBoots", 1);
                        p.getInventory().AddItems("Base.Briefs_SmallTrunks_Black", 1);
                        p.getInventory().AddItems("Base.Hat_CrashHelmet", 1);
                        p.getInventory().AddItems("Base.HolsterDouble", 1);
                        p.getInventory().AddItems("Base.Belt2", 1);
                        p.getInventory().AddItems("Base.Gloves_LeatherGloves", 1);
                        p.getInventory().AddItems("Base.WristWatch_Right_DigitalBlack", 1);
                        p.getInventory().AddItems("Base.Bag_ALICEpack_Army", 1);

                        // Медицина
                        p.getInventory().AddItems("Base.AlcoholBandage", 15);
                        p.getInventory().AddItems("Base.AlcoholWipes", 5);

                        // Еда
                        p.getInventory().AddItems("Base.WaterBottleFull", 2);
                        p.getInventory().AddItems("Base.OatsRaw", 5);
                    }
                }
            }
        }, 155, 50, "StarterPack", "starterpack_button"));



        /**
         * Кнопка получения прав доступа админа
         */
        AddChild(new Button(new AbstractEventHandler() {
            @Override
            public void Selected(String s, int toggled, int i1) {
                for (IsoPlayer p : GameClient.instance.getPlayers()) {
                    if (p.isLocalPlayer()) {
                        p.accessLevel = "admin";
                        p.accessLevel.equals("admin");
                    }
                }
            }
        }, 5, 70, "Get Admin Access", "admin_access_button"));

        /**
         * Выдача предметов
         */

        AddChild(new Button(new AbstractEventHandler() {
            @Override
            public void Selected(String s, int toggled, int i1) {
                for (IsoPlayer p : GameClient.instance.getPlayers()) {
                    if (p.isLocalPlayer()) {
                        p.getInventory().AddItems(giveGUIInputName.Text, Integer.parseInt(giveGUIInputAmount.Text));
                    }
                }
            }
        }, 200, 95, "Give", "give_button"));

    }

    /**
     * Создание польовательского окна
     */
    public Window() {
        super(50, 50, 300, 130, false);
        setRenderClippedChildren(true);

        AddChild(titleGUIBox = new UITextBox2(UIFont.Small, 0, 0, 300, 20, Info.CHEAT_GUI_TITLE, true));
        titleGUIBox.setEditable(false);

        InitGUIButtons();

        AddChild(giveGUIInputName = new UITextBox2(UIFont.Small, 5, 90, 150, 20, "Base.", true));
        giveGUIInputName.setEditable(true);

        AddChild(giveGUIInputAmount = new UITextBox2(UIFont.Small, 160, 90, 30, 20, "1", true));
        giveGUIInputAmount.setEditable(true);

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void update() {
        super.update();
    }
}
