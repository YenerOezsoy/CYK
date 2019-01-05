import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

    private Map<String, ArrayList<ArrayList<Text>>> childListMap;
    private List<Text> rootTextList;
    private Pane anchorPane;
    private Pane graphPane;
    private int x = 256;
    private int y = 10;
    private int xDepictOffset = 0;
    private int xOffset = 50;
    private int yOffset = 100;
    private int xOutOfWindow = 0;
    private HashMap<Figure,ArrayList> figureMap;
    private HashMap<Integer, ArrayList<Integer>> coordinateMap;
    private ArrayList<String> visited;


    public void setAnchorPane(Pane anchorPane) {
        this.anchorPane = anchorPane;
    }

    public void setTextAccess(List<Text> rootTextList, Map<String, ArrayList<ArrayList<Text>>> childListMap) {
        this.rootTextList = rootTextList;
        this.childListMap = childListMap;
        figureMap = new HashMap<>();
        coordinateMap = new HashMap<>();
        x = 256;
        y = 10;
        xOffset = 50;
        yOffset = 100;
        xDepictOffset = 0;
        visited = new ArrayList<>();
    }


    public void generateGraph() {
        graphPane = new Pane();
        CircleFigure circleFigure = createCircle(rootTextList.get(0), x, y);
        figureMap.put(circleFigure, childListMap.get(rootTextList.get(0).getText()));
        visited.add(rootTextList.get(0).getText());
        ArrayList<CircleFigure> toProcess = new ArrayList<>();
        toProcess.add(circleFigure);
        doNextRowDepicts(toProcess);
        //anchorPane.getChildren().add(graphPane);
        if (xOutOfWindow < 0) relocateAllElements();
    }

    private void doNextRowDepicts(ArrayList<CircleFigure> toProcess) {
        ArrayList<RectangleFigure> toProcessDepicts = new ArrayList<>();
        initNextRowStartPositionsDepicts(toProcess);
        for (CircleFigure circleFigure : toProcess) {
            ArrayList<ArrayList<Text>> list = figureMap.get(circleFigure);
            x = circleFigure.getX() - getStartingPosition(list);
            checkIfCoordinatesAreUsed(true, list.size());
            createRectangles(circleFigure, list, toProcessDepicts);
        }
        if (!toProcessDepicts.isEmpty()) doNextRowElements(toProcessDepicts);
    }

    private void createRectangles(CircleFigure circleFigure, ArrayList<ArrayList<Text>> list, ArrayList<RectangleFigure> toProcessDepicts) {
        for (ArrayList<Text> innerList : list) {
            RectangleFigure rectangleFigure = new RectangleFigure(x, y);
            x += xDepictOffset;
            Edge edge = new Edge(circleFigure, rectangleFigure);
            graphPane.getChildren().addAll(rectangleFigure.getObject(), edge.getLine());
            toProcessDepicts.add(rectangleFigure);
            figureMap.put(rectangleFigure, innerList);
        }
    }

    private void doNextRowElements(ArrayList<RectangleFigure> toProcessDepicts) {
        ArrayList<String> tempVisited = new ArrayList<>();
        ArrayList<CircleFigure> toProcess = new ArrayList<>();
        for (RectangleFigure rectangleFigure : toProcessDepicts) {
            ArrayList<Text> list = figureMap.get(rectangleFigure);
            initNextRowStartPositionsElements(list, rectangleFigure.getX(), rectangleFigure.getY());
            checkIfCoordinatesAreUsed(false, getElementSize(list));
            for (Text text : list) {
                if (!text.getText().equals(" ")) {
                    CircleFigure circleFigure = createCircle(text, x, y);
                    x += xOffset;
                    Edge edge = new Edge(rectangleFigure, circleFigure);
                    graphPane.getChildren().add(edge.getLine());
                    tempVisited.add(text.getText());
                    checkProcessAvailability(text, toProcess, circleFigure);
                }
            }
        }
        visited.addAll(tempVisited);
        if (!toProcess.isEmpty()) doNextRowDepicts(toProcess);
    }

    private void checkProcessAvailability(Text text, ArrayList<CircleFigure> toProcess, CircleFigure circleFigure) {
        if (childListMap.get(text.getText()) != null && !visited.contains(text.getText())) {
            toProcess.add(circleFigure);
            figureMap.put(circleFigure, childListMap.get(text.getText()));
        }
    }

    private CircleFigure createCircle(Text rootName, int x, int y) {
        CircleFigure circleFigure;
        Paint paint = getColor(rootName);
        if (paint.equals(Color.DARKGOLDENROD) || paint.equals(Color.RED)) {
            circleFigure = new CircleFigure(rootName.getText(), x, y, paint);
        }
        else {
            circleFigure = new CircleFigure(rootName.getText(), x, y);
        }
        graphPane.getChildren().add(circleFigure.getPane());
        return circleFigure;
    }

    private Paint getColor(Text text) {
        for (Text root : rootTextList) {
            if (root.getText().equals(text.getText())) return root.getFill();
        }
        return text.getFill();
    }


    private void initNextRowStartPositionsElements(ArrayList root, int x, int y) {
        this.y = y + yOffset;
        this.x = x - (xOffset * (getElementSize(root) / 2)) - 10;
        if (x < xOutOfWindow) xOutOfWindow = x;
    }

    private int getElementSize(ArrayList<Text> list) {
        int value = 0;
        for (Text text : list) {
            if (!text.getText().equals(" ")) value++;
        }
        return value;
    }

    private void initNextRowStartPositionsDepicts(ArrayList<CircleFigure> list) {
        y += yOffset;
        if (list != null) {
            int size = getBiggestChildNumber(list);
            //xDepictOffset = xOffset * (size / 2);
            xDepictOffset = xOffset * size;
        }
        if (x < xOutOfWindow) xOutOfWindow = x;
    }

    private int getBiggestChildNumber(ArrayList<CircleFigure> list) {
        int number = 0;
        for (CircleFigure circleFigure : list) {
            for (ArrayList<Text> depicts : (ArrayList<ArrayList<Text>>) figureMap.get(circleFigure)) {
                int size = getElementSize(depicts);
                if (size > number) number = size;
            }
        }
        return number;
    }

    private void relocateAllElements() {
        int relocateValue = Math.abs(xOutOfWindow) + 10;
        for (javafx.scene.Node node : graphPane.getChildren()) {
            node.setLayoutX(node.getLayoutX() + relocateValue);
        }
    }

    private int getStartingPosition(ArrayList<ArrayList<Text>> list) {
        int size;
        if (list.size() % 2 == 0) {
            size = xDepictOffset * (list.size() / 2) - (xDepictOffset / 2);
        }
        else {
            size = xDepictOffset * ((list.size() - 1) / 2);
        }
        return size;
    }

    private void checkIfCoordinatesAreUsed(boolean isDepict, int size) {
        int value = 0;
        int offset = xOffset;
        int coordinateX = x;
        int coordinateY = y;
        int toAdd = 0;
        if (isDepict) offset = xDepictOffset;
        ArrayList<Integer> xCoordinates = new ArrayList<>();
        boolean foundTouchingElements;
        while (value != size) {
            if (isDepict) foundTouchingElements = checkRectanglesTouching(coordinateX, coordinateY,toAdd, xCoordinates);
            else foundTouchingElements = checkCirclesTouching(coordinateX, coordinateY,toAdd, xCoordinates);
            toAdd += offset;
            value++;
            if (foundTouchingElements) {
                toAdd = 0;
                value = 0;
                if (isDepict) x -= 40;
                y += 55;
                coordinateX = x;
                coordinateY = y;
            }
        }
        if (coordinateMap.containsKey(y)) coordinateMap.get(y).addAll(xCoordinates);
        else coordinateMap.put(y, xCoordinates);
    }

    private boolean checkRectanglesTouching(int coordinateX, int coordinateY, int toAdd, ArrayList<Integer> xCoordinates) {
        if (coordinateMap.containsKey(coordinateY)) {
            if (coordinateMap.get(coordinateY).contains(coordinateX + toAdd)) {
                return true;
            }
            else {
                xCoordinates.add(coordinateX + toAdd);
                return false;
            }
        }
        else xCoordinates.add(coordinateX + toAdd);
        return false;
    }

    private boolean checkCirclesTouching(int coordinateX, int coordinateY, int toAdd, ArrayList<Integer> xCoordinates) {
        if (!coordinateMap.containsKey(coordinateY)) {
            xCoordinates.add(coordinateX);
            return false;
        }
        for (Integer xcoordinate : coordinateMap.get(coordinateY)) {
           if (xcoordinate <= coordinateX && xcoordinate + 40 > coordinateX) return true;
           else if (xcoordinate >= coordinateX && xcoordinate - 40 < coordinateX) return true;
        }
        xCoordinates.add(coordinateX + toAdd);
        return false;
    }

    public Pane getPane() {
        return graphPane;
    }
}
