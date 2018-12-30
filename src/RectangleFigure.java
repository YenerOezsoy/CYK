import javafx.scene.shape.Rectangle;

public class RectangleFigure {

    private int height = 20;
    private int width = 20;
    private int x;
    private int y;
    private Rectangle rectangle;

    public RectangleFigure(int x, int y) {
        this.x = x;
        this.y = y;
        createRectangle();
    }


    private void createRectangle() {
        rectangle = new Rectangle();
        rectangle.setX(x);
        rectangle.setY(y);
        rectangle.setHeight(height);
        rectangle.setWidth(width);
    }

    public Rectangle getRectangleObject() {
        return rectangle;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
