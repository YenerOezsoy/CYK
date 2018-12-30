import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class CircleFigure extends Figure {


    private int radius = 20;
    private Paint color;
    private Circle circle;

    public CircleFigure(int x, int y) {
        this.x = x;
        this.y = y;
        color = Color.WHITE;
        createObject();
    }

    public CircleFigure(int x, int y, Paint color) {
        this.x = x;
        this.y = y;
        this.color = color;
        createObject();
    }


    @Override
    public void createObject() {
        circle = new Circle();
        circle.setRadius(radius);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.TRANSPARENT);
    }

    @Override
    public Shape getObject() {
        return circle;    }
}
