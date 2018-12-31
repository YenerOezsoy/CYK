import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
    private HashMap<ArrayList,Figure> figureMap;
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
        figureMap.put(childListMap.get(rootTextList.get(0).getText()), circleFigure);
        visited.add(rootTextList.get(0).getText());
        ArrayList<ArrayList<ArrayList<Text>>> toProcess = new ArrayList<>();
        toProcess.add(childListMap.get(rootTextList.get(0).getText()));
        doNextRowDepicts(toProcess);
        anchorPane.getChildren().add(graphPane);
        if (xOutOfWindow < 0) relocateAllElements();
    }

    private void doNextRowDepicts(ArrayList<ArrayList<ArrayList<Text>>> toProcess) {
        ArrayList<ArrayList<Text>> toProcessDepicts = new ArrayList<>();
        initNextRowStartPositionsDepicts(toProcess);
        for (ArrayList<ArrayList<Text>> list : toProcess) {
            CircleFigure circleFigure = (CircleFigure) figureMap.get(list);
            x = circleFigure.getX() - getStartingPosition(list);
            checkIfCoordinatesAreUsed(true, list.size());
            createRectangles(circleFigure, list, toProcessDepicts);
        }
        if (!toProcessDepicts.isEmpty()) doNextRowElements(toProcessDepicts);
    }

    private void createRectangles(CircleFigure circleFigure, ArrayList<ArrayList<Text>> list, ArrayList<ArrayList<Text>> toProcessDepicts) {
        for (ArrayList<Text> innerList : list) {
            RectangleFigure rectangleFigure = new RectangleFigure(x, y);
            x += xDepictOffset;
            Edge edge = new Edge(circleFigure, rectangleFigure);
            graphPane.getChildren().addAll(rectangleFigure.getObject(), edge.getLine());
            toProcessDepicts.add(innerList);
            figureMap.put(innerList, rectangleFigure);
        }
    }

    private void doNextRowElements(ArrayList<ArrayList<Text>> toProcessDepicts) {
        ArrayList<String> tempVisited = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Text>>> toProcess = new ArrayList<>();
        for (ArrayList<Text> list : toProcessDepicts) {
            RectangleFigure rectangleFigure = (RectangleFigure) figureMap.get(list);
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

    private void checkProcessAvailability(Text text, ArrayList<ArrayList<ArrayList<Text>>> toProcess, CircleFigure circleFigure) {
        if (childListMap.get(text.getText()) != null && !visited.contains(text.getText())) {
            toProcess.add(childListMap.get(text.getText()));
            figureMap.put(childListMap.get(text.getText()), circleFigure);
        }
    }

    private CircleFigure createCircle(Text rootName, int x, int y) {
        CircleFigure circleFigure;
        if (rootName.getFill().equals(Color.BLACK)) {
            circleFigure = new CircleFigure(rootName.getText(), x, y);
        }
        else circleFigure = new CircleFigure(rootName.getText(), x, y, rootName.getFill());
        graphPane.getChildren().add(circleFigure.getPane());
        return circleFigure;
    }


    private void initNextRowStartPositionsElements(ArrayList root, int x, int y) {
        this.y = y + yOffset;
        this.x = x - xOffset * (getElementSize(root) / 2);
        if (x < xOutOfWindow) xOutOfWindow = x;
    }

    private int getElementSize(ArrayList<Text> list) {
        int value = 0;
        for (Text text : list) {
            if (!text.getText().equals(" ")) value++;
        }
        return value;
    }

    private void initNextRowStartPositionsDepicts(ArrayList<ArrayList<ArrayList<Text>>> list) {
        y += yOffset;
        if (list != null) {
            int size = getBiggestChildNumber(list);
            //xDepictOffset = xOffset * (size / 2);
            xDepictOffset = xOffset * size;
        }
        if (x < xOutOfWindow) xOutOfWindow = x;
    }

    private int getBiggestChildNumber(ArrayList<ArrayList<ArrayList<Text>>> list) {
        int number = 0;
        for (ArrayList<ArrayList<Text>> innerList : list) {
            for (ArrayList<Text> depicts : innerList) {
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
        while (value != size) {
            if (coordinateMap.containsKey(coordinateY)) {
                if (coordinateMap.get(coordinateY).contains(coordinateX + toAdd)) {
                    x -= 20;
                    y += 20;
                    coordinateX = x;
                    coordinateY = y;
                    value = -1;
                    toAdd = 0 - offset;
                }
                else xCoordinates.add(coordinateX+toAdd);
            }
            else xCoordinates.add(coordinateX+toAdd);
            toAdd += offset;
            value++;
        }
        if (coordinateMap.containsKey(y)) coordinateMap.get(y).addAll(xCoordinates);
        else coordinateMap.put(y, xCoordinates);
    }
}
