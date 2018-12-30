import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RectangleFigure extends Figure {

    private int height = 20;
    private int width = 20;

    private Rectangle rectangle;

    public RectangleFigure(int x, int y) {
        this.x = x;
        this.y = y;
        createObject();
    }

    @Override
    public void createObject() {
        rectangle = new Rectangle();
        rectangle.setX(x);
        rectangle.setY(y);
        rectangle.setHeight(height);
        rectangle.setWidth(width);
        rectangle.setFill(Color.BLACK);
    }

    @Override
    public Shape getObject() {
        return rectangle;
    }
}
