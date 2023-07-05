require "ISUI/ISCollapsableWindow"

EHMenu = ISCollapsableWindow:derive("EHMenu");

--*********************************************************
--* Экземпляр окна UI
--*********************************************************
EHMenu.instance = nil;

--*********************************************************
--* Клавиша открытия главного меню - Insert (210)
--*********************************************************
EHMenu.menuKeyID = 210;

--*********************************************************
--* API EtherHack
--*********************************************************
EHMenu.API = {
    -- Находится ли игрок в игровой сессии
    isEtherInGame = function()
        return isEtherInGame();
    end,

    -- Получить заголовок для окна
    getEtherUITitle = function()
        return getEtherUITitle();
    end,

    -- Добавить очки навыков при создании персонажа
    addProfessionPoint = function(point)
        local UIElementsList = UIManager.getUI()
		for i=1, UIElementsList:size() do
			local UIElementInstance = UIElementsList:get(i-1);
            if UIElementInstance:getTable() and UIElementInstance:getTable():getChildren() ~= nil then
                for _, child in pairs(UIElementInstance:getTable():getChildren()) do
                    if child.Type == "CharacterCreationProfession" then
                        child.pointToSpend = child.pointToSpend + point;
                        break;
                    end
                end
            end
		end
    end,
    -- Включение режима разработки
    setDebugBypass = function(changeOptionTarget, joypadIndex, isSelected)
        setEtherBypassDebugMode(isSelected);
    end,

    -- Получение прав администратора
    setAdminAccess = function()
        setEtherAdminAccess();
    end,

    -- Выдача предметов
    addItem = function(id, amount)
        getPlayer():getInventory():AddItems(id, amount);
    end,

    -- Метод для переключения состояния "Invisible"
    toggleInvisible = function(changeOptionTarget, joypadIndex, isSelected)
        if not isEtherInGame() or getPlayer() == nil then return end
        getPlayer():setInvisible(isSelected);
    end,

    -- Метод для переключения состояния "GodMode"
    toggleGodMode = function(changeOptionTarget, joypadIndex, isSelected)
        if not isEtherInGame() or getPlayer() == nil then return end
        getPlayer():setGodMod(isSelected);
    end,

    -- Метод для переключения состояния "GhostMode"
    toggleGhostMode = function(changeOptionTarget, joypadIndex, isSelected)
        if not isEtherInGame() or getPlayer() == nil then return end
        getPlayer():setGhostMode(isSelected);
    end,

    -- Метод для переключения состояния "NoClip"
    toggleNoClip = function(changeOptionTarget, joypadIndex, isSelected)
        if not isEtherInGame() or getPlayer() == nil then return end
        getPlayer():setNoClip(isSelected);
    end,

    -- Установка всех навыков на максимальный уровень
    setMaxDefaultSkill = function()
        setEtherMaxDefaultSkill();
    end,
};

--*********************************************************
--* Закрытие окна по нажатию кнопки UI
--*********************************************************
function EHMenu:close()
	EHMenu.instance:setVisible(false);
    EHMenu.instance:removeFromUIManager();
    EHMenu.instance = nil;
end

--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function EHMenu:new()
    local menuTableData = {};

    local width = 200;
    local height = 345;

    local positionX = getCore():getScreenWidth() / 2 - width /2;
    local positionY = getCore():getScreenHeight() / 2 - height /2;

    menuTableData = ISCollapsableWindow:new(positionX, positionY, width, height);
    setmetatable(menuTableData, self);
    self.__index = self;
    self.checkboxes = {};
    self.buttons = {};

    return menuTableData;
end

--*********************************************************
--* Обновление UI каждый кадр
--*********************************************************
function EHMenu:update()
    if not EHMenu.API.isEtherInGame() or getPlayer() == nil then
        for id, item in pairs(self.checkboxes) do
            if item.isOnlyInGame then
                item.enable = false;
            end
        end
    end
end

--*********************************************************
--* Хук рендера
--*********************************************************
function EHMenu:render()
    ISCollapsableWindow.render(self);
end

--*********************************************************
--* Добавление чекбоксов по вертикали
--*********************************************************
function EHMenu:addVerticalCheckBox(option, id, method, isSelected, isOnlyInGame)
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
function EHMenu:onOptionMouseDown(button, x, y)
    -- Проверка наличия кнопки в таблице self.buttons
    local isButton = false
    for _, btn in ipairs(self.buttons) do
        if btn == button then
            isButton = true;
            break
        end
    end

    if not isButton then return false end

    if button.isOnlyInGame and (not EHMenu.API.isEtherInGame() or getPlayer() == nil) then return end
    button.method();
end

--*********************************************************
--* Добавление кнопок
--*********************************************************
function EHMenu:addVerticalButton(title, id, width, height, method, isOnlyInGame)
    local buttonY = #self.checkboxes * 20 + 30 + (#self.buttons * 35);
    local button = ISButton:new(10, buttonY, width, height, title, self, EHMenu.onOptionMouseDown);
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
function EHMenu:createChildren()
    ISCollapsableWindow.createChildren(self);

    self:addVerticalCheckBox("Debug Mode Bypass","DebugModeBypass", EHMenu.API.setDebugBypass, isEtherBypassDebugMode() or false, false);
    self:addVerticalCheckBox("Invisible","Invisible", EHMenu.API.toggleInvisible, getPlayer() and getPlayer():isInvisible() or false, true);
    self:addVerticalCheckBox("God Mode", "GodMode", EHMenu.API.toggleGodMode, getPlayer() and getPlayer():isGodMod() or false, true);
    self:addVerticalCheckBox("Ghost Mode", "GhostMode", EHMenu.API.toggleGhostMode, getPlayer() and getPlayer():isGhostMode() or false, true);
    self:addVerticalCheckBox("No Clip", "NoClip", EHMenu.API.toggleNoClip, getPlayer() and getPlayer():isNoClip() or false, true);

    self:addVerticalButton("Give Profession Points (beta)", "GiveProfessionPoints", self.width - 20, 30, function() EHMenu.API.addProfessionPoint(100) end, false)
    self:addVerticalButton("Set Max Default Skill", "SetMaxDefaultSkill", self.width - 20, 30, function() EHMenu.API.setMaxDefaultSkill() end, true)
    self:addVerticalButton("Get Admin Access", "SetAdminAccess", self.width - 20, 30, function() EHMenu.API.setAdminAccess() end, true)
    self:addVerticalButton("Items Creator", "ItemCreator", self.width - 20, 30, function() ISItemsListViewer.OnOpenPanel() end, true)
    self:addVerticalButton("Player Editor", "PlayerEditor", self.width - 20, 30, function() EHPlayerStatMenu.OnOpenPanel() end, true)
    self:addVerticalButton("Game Debug Menu", "GameDebugMenu", self.width - 20, 30, function() ISGeneralDebug.OnOpenPanel() end, true)
end

--*********************************************************
--* Логика открытия и закрытия меню по нажатию клавиши
--*********************************************************
function EHMenu.onOpenAndCloseMenu(key)
    if key == EHMenu.menuKeyID then
        -- Если панель уже существует, закрываем окно
        if EHMenu.instance ~= nil then
            EHMenu.instance:setVisible(false);
            EHMenu.instance:removeFromUIManager();
            EHMenu.instance = nil;
            return
        end

        -- Создаем новую панель
        EHMenu.instance  = EHMenu:new()
        EHMenu.instance:initialise();
        EHMenu.instance:instantiate();
        EHMenu.instance:addToUIManager();
        EHMenu.instance:setVisible(true);
        EHMenu.instance:setResizable(false);
        EHMenu.instance:setTitle(EHMenu.API.getEtherUITitle());
        EHMenu.instance:setAlwaysOnTop(true);

    end
end

Events.OnKeyPressed.Add(EHMenu.onOpenAndCloseMenu)

--*********************************************************
--* Override
--*********************************************************
EHPlayerStatMenu = ISPlayerStatsUI:derive("EHPlayerStatMenu");

function EHPlayerStatMenu:updateButtons()
    local buttonEnable = true;
    self.addTraitBtn.enable = buttonEnable;
    self.changeProfession.enable = buttonEnable;
    self.changeForename.enable = buttonEnable;
    self.changeSurname.enable = buttonEnable;
    --    self.addGlobalXP.enable = buttonEnable;
    self.muteAllBtn.enable = buttonEnable;
    self.addXpBtn.enable = buttonEnable;
    self.addLvlBtn.enable = buttonEnable and (self.selectedPerk ~= nil)
    self.loseLvlBtn.enable = buttonEnable and (self.selectedPerk ~= nil)
    self.userlogBtn.enable = buttonEnable;
    self.manageInvBtn.enable = buttonEnable;
    self.warningPointsBtn.enable = buttonEnable;
    self.changeAccessLvlBtn.enable = (self.admin:getAccessLevel() == "Admin" or self.admin:getAccessLevel() == "Moderator") and self:canModifyThis();
    self.changeUsernameBtn.enable = buttonEnable;
    for _,image in ipairs(self.traits) do
        self.traitsRemoveButtons[image.label].enable = buttonEnable;
    end
end

function EHPlayerStatMenu.OnOpenPanel()
    if EHPlayerStatMenu.instance then
        EHPlayerStatMenu.instance:close()
    end
    local x = getCore():getScreenWidth() / 2 - (800 / 2);
    local y = getCore():getScreenHeight() / 2 - (800 / 2);
    local ui = EHPlayerStatMenu:new(x,y - 100,800,800, getPlayer(), getPlayer())
    ui:initialise();
    ui:addToUIManager();
    ui:setVisible(true);
end
