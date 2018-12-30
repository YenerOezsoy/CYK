import javafx.scene.shape.Line;

public class Edge {

    private Line line;

    public Edge(RectangleFigure source, CircleFigure target) {
        line = new Line();
        setEdgeEnds(source.getX() + 10, source.getY() + 20, target.getX() + 20 , target.getY());
    }

    public Edge(CircleFigure source, RectangleFigure target) {
        line = new Line();
        setEdgeEnds(source.getX() + 20 , source.getY() + 40, target.getX() + 10, target.getY());
    }

    private void setEdgeEnds(int xStart, int yStart, int xEnd, int yEnd) {
        line.setStartX(xStart);
        line.setStartY(yStart);
        line.setEndX(xEnd);
        line.setEndY(yEnd);
    }

    public Line getLine() {
        return line;
    }
}
