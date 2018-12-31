import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

public class CircleFigure extends Figure {


    private int radius = 20;
    private Paint color;
    private StackPane pane;
    private Circle circle;
    private String name;

    public CircleFigure(String name, int x, int y) {
        this.x = x;
        this.y = y;
        this.name = name;
        color = Color.BLACK;
        createObject();
    }

    public CircleFigure(String name, int x, int y, Paint color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.name = name;
        createObject();
    }


    @Override
    public void createObject() {
        circle = new Circle();
        circle.setRadius(radius);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.TRANSPARENT);
        Text text = new Text(name);
        text.setFill(color);
        pane = new StackPane();
        pane.getChildren().addAll(circle, text);
        pane.setLayoutX(x);
        pane.setLayoutY(y);
    }

    @Override
    public Shape getObject() {
        return circle;
    }

    public StackPane getPane() {
        return pane;
    }
}
