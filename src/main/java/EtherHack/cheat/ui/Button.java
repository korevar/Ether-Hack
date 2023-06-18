package EtherHack.cheat.ui;

import zombie.core.Color;
import zombie.core.textures.Texture;
import zombie.ui.TextManager;
import zombie.ui.UIElement;
import zombie.ui.UIEventHandler;
import zombie.ui.UIFont;

public class Button extends UIElement{
    /**
     * Флаг, отслеживающий была ли кнопка нажата.
     */
    public boolean clicked = false;

    /**
     * Объект UIElement, которому отправляется сообщение при нажатии на кнопку.
     */
    public UIElement MessageTarget;

    /**
     * Флаг, отслеживающий наведение мыши на кнопку.
     */
    public boolean mouseOver = false;

    /**
     * Название кнопки.
     */
    public String name;

    /**
     * Текст на кнопке.
     */
    public String text;

    /**
     * Текстуры кнопки для разных состояний.
     */
    Texture downLeft, downMid, downRight, upLeft, upMid, upRight;

    /**
     * Оригинальное положение X кнопки.
     */
    float origX;

    /**
     * Обработчик событий для действий с пользовательским интерфейсом.
     */
    private UIEventHandler MessageTarget2 = null;


    /**
     * Конструктор для создания кнопки с заданными параметрами.
     *
     * @param target объект UIElement, который получает уведомление при нажатии на кнопку
     * @param x координата x кнопки
     * @param y координата y кнопки
     * @param text текст кнопки
     * @param name имя кнопки
     */
    public Button(UIEventHandler target, int x, int y, String text, String name) {
        this.x = x;
        this.y = y;
        this.origX = (float) x;
        this.MessageTarget2 = target;
        this.upLeft = Texture.getSharedTexture("ButtonL_Up");
        this.upMid = Texture.getSharedTexture("ButtonM_Up");
        this.upRight = Texture.getSharedTexture("ButtonR_Up");
        this.downLeft = Texture.getSharedTexture("ButtonL_Down");
        this.downMid = Texture.getSharedTexture("ButtonM_Down");
        this.downRight = Texture.getSharedTexture("ButtonR_Down");
        this.name = name;
        this.text = text;
        this.width = (float) TextManager.instance.MeasureStringX(UIFont.Small, text);
        this.width += 8.0F;
        if (this.width < 40.0F) {
            this.width = 40.0F;
        }

        this.height = (float) this.downMid.getHeight();
    }

    /**
     * Обрабатывает событие нажатия кнопки мыши на кнопку.
     *
     * @param x координата x нажатия
     * @param y координата y нажатия
     * @return возвращает истину, если кнопка видима и была нажата
     */
    public Boolean onMouseDown(double x, double y) {
        if (!this.isVisible()) {
            return false;
        } else {
            this.clicked = true;
            return Boolean.TRUE;
        }
    }

    /**
     * Обрабатывает событие перемещения курсора мыши над кнопкой.
     *
     * @param x координата x курсора
     * @param y координата y курсора
     * @return возвращает истину, если курсор перемещается над кнопкой
     */
    public Boolean onMouseMove(double x, double y) {
        this.mouseOver = true;
        return Boolean.TRUE;
    }

    /**
     * Обрабатывает событие, когда курсор мыши перемещается за пределы кнопки.
     *
     * @param x координата x курсора
     * @param y координата y курсора
     */
    public void onMouseMoveOutside(double x, double y) {
        this.clicked = false;
        this.mouseOver = false;
    }

    /**
     * Метод обрабатывает событие отпускания кнопки мыши после нажатия на кнопку.
     *
     * @param x координата x позиции мыши при отпускании.
     * @param y координата y позиции мыши при отпускании.
     * @return возвращает истину, если кнопка была нажата и затем отпущена.
     */
    public Boolean onMouseUp(double x, double y) {
        if (this.clicked) {
            if (this.MessageTarget2 != null) {
                this.MessageTarget2.Selected(this.name, 0, 0);
            } else if (this.MessageTarget != null) {
                this.MessageTarget.ButtonClicked(this.name);
            }
        }

        this.clicked = false;
        return Boolean.TRUE;
    }

    /**
     * Метод рендерит кнопку на экране, используя заданные текстуры и цвета.
     */
    public void render() {
        if (this.isVisible()) {
            if (this.clicked) {
                this.DrawTexture(this.downLeft, 0.0D, 0.0D, 1.0D);
                this.DrawTextureScaledCol(this.downMid, this.downLeft.getWidth(), 0.0D, (int) (this.getWidth() - (double) (this.downLeft.getWidth() * 2)), this.downLeft.getHeight(), new Color(255, 255, 255, 255));
                this.DrawTexture(this.downRight, (int) (this.getWidth() - (double) this.downRight.getWidth()), 0.0D, 1.0D);
                this.DrawTextCentre(this.text, this.getWidth() / 2.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D);
            } else {
                this.DrawTexture(this.upLeft, 0.0D, 0.0D, 1.0D);
                this.DrawTextureScaledCol(this.upMid, this.downLeft.getWidth(), 0.0D, (int) (this.getWidth() - (double) (this.downLeft.getWidth() * 2)), this.downLeft.getHeight(), new Color(255, 255, 255, 255));
                this.DrawTexture(this.upRight, (int) (this.getWidth() - (double) this.downRight.getWidth()), 0.0D, 1.0D);
                this.DrawTextCentre(this.text, this.getWidth() / 2.0D, 0.0D, 1.0D, 1.0D, 1.0D, 1.0D);
            }

            super.render();
        }
    }
}
