import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RectangleFigure extends Figure {

    private int height = 20;
    private int width = 20;
    private Graph graph;

    private Rectangle rectangle;

    public RectangleFigure(double x, double y, Graph graph) {
        this.x = x;
        this.y = y;
        this.graph = graph;
        createObject();
    }

    @Override
    protected Graph getGraph() {
        return graph;
    }

    @Override
    public void createObject() {
        rectangle = new Rectangle();
        rectangle.setHeight(height);
        rectangle.setWidth(width);
        rectangle.setFill(Color.BLACK);
        initDragAndDrop(rectangle);
        rectangle.setLayoutX(x);
        rectangle.setLayoutY(y);
    }

    @Override
    public Shape getObject() {
        return rectangle;
    }
}
