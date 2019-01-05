import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public abstract class Figure {

    protected double x;
    protected double y;
    protected double m_nMouseX;
    protected double m_nMouseY;
    protected double m_nX;
    protected double m_nY;

    public  double getX() {
        return x;
    }

    public  double getY() {
        return y;
    }

    /**
     * Creates an event handler that handles a mouse press on the node.
     *
     * @return the event handler.
     */
    protected EventHandler<MouseEvent> pressMouse(Object object) {
        EventHandler<MouseEvent> mousePressHandler = event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                // get the current mouse coordinates according to the scene.
                m_nMouseX = event.getSceneX();
                m_nMouseY = event.getSceneY();

                // get the current coordinates of the draggable node.
                getLayout(object);
            }
        };
        return mousePressHandler;
    }

    private void getLayout(Object object) {
        if (object instanceof Shape) {
            m_nX = ((Shape) object).getLayoutX();
            m_nY = ((Shape) object).getLayoutY();
        }
        else if (object instanceof StackPane) {
            m_nX = ((StackPane) object).getLayoutX();
            m_nY = ((StackPane) object).getLayoutY();
        }
    }

    /**
     * Creates an event handler that handles a mouse drag on the node.
     *
     * @return the event handler.
     */
    protected EventHandler<MouseEvent> dragMouse(Object object) {
        EventHandler<MouseEvent> dragHandler = event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                // find the delta coordinates by subtracting the new mouse
                // coordinates with the old.
                double deltaX = event.getSceneX() - m_nMouseX;
                double deltaY = event.getSceneY() - m_nMouseY;

                // add the delta coordinates to the node coordinates.
                m_nX += deltaX;
                m_nY += deltaY;

                // set the layout for the draggable node.
                setLayout(object);

                // get the latest mouse coordinate.
                m_nMouseX = event.getSceneX();
                m_nMouseY = event.getSceneY();

                getGraph().setHasObjectMoved(true);
            }
        };
        return dragHandler;
    }

    protected abstract Graph getGraph();

    private void setLayout(Object object) {
        if (object instanceof Rectangle) {
            ((Rectangle) object).setLayoutY(m_nY);
            ((Rectangle) object).setLayoutX(m_nX);

        }
        else if (object instanceof StackPane) {
            ((StackPane) object).setLayoutY(m_nY);
            ((StackPane) object).setLayoutX(m_nX);
        }
    }

    protected void initDragAndDrop(Shape shape) {
        shape.setOnMousePressed(pressMouse(shape));
        shape.setOnMouseDragged(dragMouse(shape));
    }

    protected void initDragAndDrop(StackPane pane) {
        pane.setOnMousePressed(pressMouse(pane));
        pane.setOnMouseDragged(dragMouse(pane));
    }

    public abstract void createObject();

    public abstract Shape getObject();
}
