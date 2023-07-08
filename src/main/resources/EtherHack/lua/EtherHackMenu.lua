require "ISUI/ISCollapsableWindow"
EtherRequire "EtherHack/lua/EtherHackAPI.lua"
EtherRequire "EtherHack/lua/ItemCreator/EtherItemCreator.lua"
EtherRequire "EtherHack/lua/PlayerEditor/EtherPlayerEditor.lua"
EtherRequire "EtherHack/lua/AdminPanel/EtherAdminPanel.lua"

EtherMain = ISCollapsableWindow:derive("EtherMain");

--*********************************************************
--* Экземпляр окна UI
--*********************************************************
EtherMain.instance = nil;

--*********************************************************
--* Клавиша открытия главного меню - Insert (210)
--*********************************************************
EtherMain.menuKeyID = 210;

--*********************************************************
--* Закрытие окна по нажатию кнопки UI
--*********************************************************
function EtherMain:close()
	EtherMain.instance:setVisible(false);
    EtherMain.instance:removeFromUIManager();
    EtherMain.instance = nil;
end

--*********************************************************
--* Обновление UI каждый кадр
--*********************************************************
function EtherMain:update()
    if not EtherHack.API.isEtherInGame() or getPlayer() == nil then
        for id, item in pairs(self.checkboxes) do
            if item.isOnlyInGame then
                item.enable = false;
            end
        end
    end
end

--*********************************************************
--* Добавление чекбоксов по вертикали
--*********************************************************
function EtherMain:addVerticalCheckBox(option, id, method, isSelected, isOnlyInGame)
    local checkboxY = 20 + (#self.checkboxes * 20);

    local tick = ISTickBox:new(10, checkboxY, 100, 50, id, self, method);
    tick:initialise();
    tick:instantiate();
    tick:setAnchorLeft(true);
    tick:setAnchorRight(false);
    tick:setAnchorTop(false);
    tick:setAnchorBottom(true);
    tick.selected[1] = isSelected;
    tick.isOnlyInGame = isOnlyInGame;
    self:addChild(tick);
    tick:addOption(option);

    table.insert(self.checkboxes, tick);
end

--*********************************************************
--* Хук нажатия на кнопку
--*********************************************************
function EtherMain:onOptionMouseDown(button, x, y)
    -- Проверка наличия кнопки в таблице self.buttons
    local isButton = false
    for _, btn in ipairs(self.buttons) do
        if btn == button then
            isButton = true;
            break
        end
    end

    if not isButton then return false end

    if button.isOnlyInGame and (not EtherHack.API.isEtherInGame() or getPlayer() == nil) then return end
    button.method();
end

--*********************************************************
--* Добавление кнопок
--*********************************************************
function EtherMain:addVerticalButton(title, id, width, height, method, isOnlyInGame)
    local buttonY = #self.checkboxes * 20 + 30 + (#self.buttons * 35);
    local button = ISButton:new(10, buttonY, width, height, title, self, EtherMain.onOptionMouseDown);
    button.internal = id;
    button.method = method;
    button.isOnlyInGame = isOnlyInGame;
    button:initialise();
    button:instantiate();
    button.borderColor = {r=0.7, g=0.7, b=0.7, a=0.5};
    self:addChild(button);

    table.insert(self.buttons, button);
end

--*********************************************************
--* Создание дочерних элементов
--*********************************************************
function EtherMain:createChildren()
    ISCollapsableWindow.createChildren(self);

    self:addVerticalCheckBox("Debug Mode Bypass","DebugModeBypass", EtherHack.API.setDebugBypass, EtherHack.API.isEtherBypassDebugMode() or false, false);
    self:addVerticalCheckBox("MultiHit Zombies","MultiHitZombies", EtherHack.API.toggleMultiHitZombies, getPlayer() and EtherHack.API.isMultiHitZombies() or false, true);
    self:addVerticalCheckBox("Invisible","Invisible", EtherHack.API.toggleInvisible, getPlayer() and getPlayer():isInvisible() or false, true);
    self:addVerticalCheckBox("God Mode", "GodMode", EtherHack.API.toggleGodMode, getPlayer() and getPlayer():isGodMod() or false, true);
    self:addVerticalCheckBox("No Clip", "NoClip", EtherHack.API.toggleNoClip, getPlayer() and getPlayer():isNoClip() or false, true);
    self:addVerticalCheckBox("Unlimited Carry", "UnlimitedCarry", EtherHack.API.toggleUnlimitedCarry, getPlayer() and EtherHack.API.isEtherUnlimitedCarry() or false, true);
    self:addVerticalCheckBox("Unlimited Endurance", "UnlimitedEndurance", EtherHack.API.toggleUnlimitedEndurance, getPlayer() and EtherHack.API.isUnlimitedEndurance() or false, true);
    self:addVerticalCheckBox("Disable Fatigue", "DisableFatigue", EtherHack.API.toggleDisableFatigue, getPlayer() and EtherHack.API.isDisableFatigue() or false, true);
    self:addVerticalCheckBox("Disable Hunger", "DisableHunger", EtherHack.API.toggleDisableHunger, getPlayer() and EtherHack.API.isDisableHunger() or false, true);
    self:addVerticalCheckBox("Disable Thirst", "DisableThirst", EtherHack.API.toggleDisableThirst, getPlayer() and EtherHack.API.isDisableThirst() or false, true);
    self:addVerticalCheckBox("Disable Character Needs", "CharacterNeeds", EtherHack.API.toggleCharacterNeeds, getPlayer() and EtherHack.API.isCharacterNeeds() or false, true);

    self:addVerticalButton("Add x100 Trait Points (beta)", "GiveProfessionPoints", self.width - 20, 30, function() EtherHack.API.addProfessionPoint(100) end, false)
    self:addVerticalButton("Game Debugger", "GameDebugMenu", self.width - 20, 30, function() ISGeneralDebug.OnOpenPanel() end, true)
    self:addVerticalButton("Items Creator", "ItemCreator", self.width - 20, 30, function() EtherItemCreator.OnOpenPanel() end, true)
    self:addVerticalButton("Player Editor", "PlayerEditor", self.width - 20, 30, function() EtherPlayerEditor.OnOpenPanel() end, true)
    self:addVerticalButton("Get Admin Access", "SetAdminAccess", self.width - 20, 30, function() EtherHack.API.setAdminAccess() end, true)
    self:addVerticalButton("Open Admin Menu", "AdminMenu", self.width - 20, 30, function() EtherHack.API.openAdminMenu() end, true)
end

--*********************************************************
--* Логика открытия и закрытия меню по нажатию клавиши
--*********************************************************
function EtherMain.OnOpenPanel(key)
    if key == EtherMain.menuKeyID then
        -- Если панель уже существует, закрываем окно
        if EtherMain.instance ~= nil then
            EtherMain.instance:setVisible(false);
            EtherMain.instance:removeFromUIManager();
            EtherMain.instance = nil;
            return
        end

        -- Создаем новую панель
        EtherMain.instance  = EtherMain:new()
        EtherMain.instance:initialise();
        EtherMain.instance:instantiate();
        EtherMain.instance:addToUIManager();
        EtherMain.instance:setVisible(true);
        EtherMain.instance:setResizable(false);
        EtherMain.instance:setTitle(EtherHack.API.getEtherUITitle());
        EtherMain.instance:setAlwaysOnTop(true);

    end
end

--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function EtherMain:new()
    local menuTableData = {};

    local width = 200;
    local height = 465;

    local positionX = getCore():getScreenWidth() / 2 - width /2;
    local positionY = getCore():getScreenHeight() / 2 - height /2;

    menuTableData = ISCollapsableWindow:new(positionX, positionY, width, height);
    setmetatable(menuTableData, self);
    self.__index = self;
    self.checkboxes = {};
    self.buttons = {};

    return menuTableData;
end

Events.OnKeyPressed.Add(EtherMain.OnOpenPanel)