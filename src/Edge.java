import javafx.scene.shape.Line;

public class Edge {

    private Line line;

    public Edge(RectangleFigure source, CircleFigure target) {
        line = new Line();
        line.startXProperty().bind(source.getObject().layoutXProperty().add(10));
        line.startYProperty().bind(source.getObject().layoutYProperty().add(20));

        line.endXProperty().bind(target.getPane().layoutXProperty().add(20));
        line.endYProperty().bind(target.getPane().layoutYProperty());
    }

    public Edge(CircleFigure source, RectangleFigure target) {
        line = new Line();

        line.startXProperty().bind(source.getPane().layoutXProperty().add(20));
        line.startYProperty().bind(source.getPane().layoutYProperty().add(40));

        line.endXProperty().bind(target.getObject().layoutXProperty().add(10));
        line.endYProperty().bind(target.getObject().layoutYProperty());
    }

    public Line getLine() {
        return line;
    }
}
