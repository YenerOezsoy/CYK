import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class CircleFigure {


    private int radius = 20;
    private int x;
    private int y;
    private Paint color;
    private Circle circle;

    public CircleFigure(int x, int y) {
        this.x = x;
        this.y = y;
        color = Color.TRANSPARENT;
        createCircle();
    }

    public CircleFigure(int x, int y, Paint color) {
        this.x = x;
        this.y = y;
        this.color = color;
        createCircle();
    }

    private void createCircle() {
        circle = new Circle();
        //circle.setCenterX(x);
        //circle.setCenterY(y);
        circle.setRadius(radius);
        circle.setStroke(color);
    }

    public Circle getCircleObject() {
        return circle;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
