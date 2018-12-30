import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Graph {

    private Map<String, ArrayList<ArrayList<Text>>> childListMap;
    private List<Text> rootTextList;
    private Pane anchorPane;
    private Pane graphPane;
    private int x = 256;
    private int y = 10;
    private int xOffset = 50;
    private int yOffset = 100;


    public void setAnchorPane(Pane anchorPane) {
        this.anchorPane = anchorPane;
    }

    public void setTextAccess(List<Text> rootTextList, Map<String, ArrayList<ArrayList<Text>>> childListMap) {
        this.rootTextList = rootTextList;
        this.childListMap = childListMap;
        x = 256;
        y = 10;
        xOffset = 50;
        yOffset = 100;
    }

    public void generateGraph() {
        graphPane = new Pane();
        Text element = rootTextList.get(0);
        CircleFigure circleFigure = createCircle(element,x , y);
        childListMap.get(rootTextList.get(0).getText());
        initNextRowStartPositions(childListMap.get(rootTextList.get(0).getText()), circleFigure.getX(), circleFigure.getY());
        doNextRowDepicts(circleFigure, childListMap.get(element.getText()), x, y);
        anchorPane.getChildren().add(graphPane);
    }

    private void doNextRowDepicts(CircleFigure circleFigure, ArrayList<ArrayList<Text>> list, int x, int y) {
        int thisLevelX = x;
        int thisLevelY = y;
        for (ArrayList<Text> innerList : list) {
            ArrayList<String> visited = new ArrayList<>();
            ArrayList<String> newlyAddedToLevel = new ArrayList<>();
            RectangleFigure rectangleFigure = createRectangle(circleFigure, thisLevelX, thisLevelY);
            initNextRowStartPositions(innerList, thisLevelX, thisLevelY);
            thisLevelX += xOffset;
            doNextRowCircles(rectangleFigure, innerList, visited, newlyAddedToLevel, x, y);
        }
    }

    private void doNextRowDepicts(ArrayList<ArrayList<Text>> list, ArrayList<String> visited, int x, int y, CircleFigure circleFigure) {
        if (list != null) {
            int thisLevelX = x;
            int thisLevelY = y;
            for (ArrayList<Text> innerList : list) {
                ArrayList<String> newlyAddedToLevel = new ArrayList<>();
                RectangleFigure rectangleFigure = createRectangle(circleFigure, thisLevelX, thisLevelY);
                initNextRowStartPositions(innerList, thisLevelX, thisLevelY);
                thisLevelX += xOffset;
                doNextRowCircles(rectangleFigure, innerList, visited, newlyAddedToLevel, x, y);
            }
        }
    }

    private RectangleFigure createRectangle(CircleFigure circleFigure, int x, int y) {
        RectangleFigure rectangleFigure = new RectangleFigure(x, y);
        Edge edge = new Edge(circleFigure, rectangleFigure);
        graphPane.getChildren().addAll(rectangleFigure.getRectangleObject(), edge.getLine());
        return rectangleFigure;
    }



    private void doNextRowCircles(RectangleFigure rectangleFigure, ArrayList<Text> list, ArrayList<String> visited, ArrayList<String> newlyAddedToLevel, int x, int y) {
        int thisLevelX = x;
        int thisLevelY = y;
        for (Text text : list) {
            if (!text.getText().equals(" ")) {
                initNextRowStartPositions(childListMap.get(text.getText()), thisLevelX, thisLevelY);
                thisLevelX += xOffset;
                //erzeuge Kindelemente von diesem Objekt
                if (!visited.contains(text.getText()) || newlyAddedToLevel.contains(text.getText())) {
                    CircleFigure circleFigure = doNextRowCircleCreation(text, thisLevelX, thisLevelY, rectangleFigure, visited, newlyAddedToLevel);
                    doNextRowDepicts(childListMap.get(text.getText()), visited, x, y, circleFigure);
                }
                //erzeuge nur Objekt selbst
                else {
                    doNextRowCircleCreation(text, thisLevelX, thisLevelY, rectangleFigure, visited);
                }
            }
        }
    }

    private CircleFigure doNextRowCircleCreation(Text text, int thisLevelX, int thisLevelY, RectangleFigure rectangleFigure, ArrayList<String> visited, ArrayList<String> newlyAddedToLevel) {
        CircleFigure value = doNextRowCircleCreation(text, thisLevelX, thisLevelY, rectangleFigure, visited);
        newlyAddedToLevel.add(text.getText());
        initNextRowStartPositions(childListMap.get(text.getText()),thisLevelX, thisLevelY);
        return value;
    }

    private CircleFigure doNextRowCircleCreation(Text text, int thisLevelX, int thisLevelY, RectangleFigure rectangleFigure, ArrayList<String> visited) {
        CircleFigure circleFigure = createCircle(text, thisLevelX, thisLevelY);
        Edge edge = new Edge(rectangleFigure, circleFigure);
        graphPane.getChildren().add(edge.getLine());
        visited.add(text.getText());
        return circleFigure;
    }

    private CircleFigure createCircle(Text root, int x, int y) {
        CircleFigure circleFigure;
        System.out.println(root.getFill().equals(Color.BLACK));
        System.out.println(root.getFill().equals("0x000000ff"));
        if (root.getFill().equals(Color.BLACK)) circleFigure = new CircleFigure(x,y);
        else circleFigure = new CircleFigure(x, y, root.getFill());
        Text text = new Text(root.getText());
        text.setBoundsType(TextBoundsType.VISUAL);
        StackPane stack = new StackPane();
        stack.setLayoutX(x);
        stack.setLayoutY(y);
        stack.getChildren().addAll(circleFigure.getCircleObject(), text);
        graphPane.getChildren().add(stack);
        return circleFigure;
    }

    private void initNextRowStartPositions(ArrayList root, int x, int y) {
        if(root != null) {
            int size = root.size();
            if (size % 2 == 1) {
                size--;
            }
            this.x = x - (xOffset * size);
            this.y = y + yOffset;
        }
    }
}
