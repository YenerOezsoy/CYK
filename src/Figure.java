import javafx.scene.shape.Shape;

public abstract class Figure {

    protected int x;
    protected int y;


    public  int getX() {
        return x;
    }

    public  int getY() {
        return y;
    }

    public abstract void createObject();

    public abstract Shape getObject();
}
