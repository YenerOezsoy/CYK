import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ViewControllerCYK {

    private static final String UNMARKALLROWS = "-fx-border-color: black; -fx-alignment: CENTER; -fx-font-size: 20";
    private static final String UNMARKFIRSTROW = "-fx-alignment: CENTER; -fx-font-size: 20";

    private Tree tree;
    private CYK cyk;
    private String word;
    private Pane pane;
    private HashMap<Integer, ArrayList<String>[]> pyramid;
    private int size;
    private int[] marker;
    private Node node;


    public ViewControllerCYK(Tree tree, String word, Pane pane) {
        this.tree = tree;
        this.pane = pane;
        cyk = new CYK();
        cyk.setMap(tree.getMap());
        this.word = word;
        cyk.initialize(word);
        pyramid = cyk.getPyramid();
        size = cyk.getSize();
    }

    public boolean next() {
        marker = cyk.nextStep();
        if (marker != null) {
            unmark();
            doMarking(marker);
            return true;
        }
        checkFinal();
        return false;
    }

    public void previous() {
        deleteContent();
        marker = cyk.previousStep();
        if (marker != null) {
            unmark();
            doMarking(marker);
        }
    }

    private void deleteContent() {
        if (marker != null) {
            int childNumber = transform(marker[1], marker[0]);
            Label label = (Label) pane.getChildren().get(childNumber);
            deleteLastEdits(label, childNumber);
        }
    }

    private void deleteLastEdits(Label label, int childNumber) {
        String text = label.getText();
        String[] split = text.split(",");
        if (split.length > 2 && childNumber > (size * 3) - 1) {
            int length = split.length - 2;
            text = shrinkArray(split, length);
            //text = "" + Arrays.toString(split);
        }
        else text = "";
        label.setText(text);
    }

    private String shrinkArray(String[] split, int length) {
        String value = "";
        for (int i = 0; i < length; i++) {
            if (value.length() > 1) value += ",";
            value += split[i];
        }
        return value;
    }

    private void doMarking(int[] marker) {
        highlight(marker[1],marker[0]);
        if(marker.length == 2) {
            mark(marker[1],marker[0] + 1);
        }
        else if (marker.length == 5) {
            mark(marker[3],marker[2]);
            mark(marker[4],marker[2]);
        }
        else if (marker.length == 6){
            mark(marker[3],marker[2]);
            mark(marker[5],marker[4]);
        }
    }

    private void unmark() {
        for (javafx.scene.Node node : pane.getChildren()) {
            if (node.getStyle().contains("background")) {
                if (pane.getChildren().indexOf(node) < size) {
                    node.setStyle(UNMARKFIRSTROW);
                }
                else {
                    node.setStyle(UNMARKALLROWS);
                }
            }
        }
    }

    private void highlight(int x, int y) {
        int childNumber = transform(x,y);
        doColoring((Label) pane.getChildren().get(childNumber), "#F0E68C");
        String value = cyk.getRoot();
        if(value != null) {
            String textInLabel = ((Label) pane.getChildren().get(childNumber)).getText();
            if (textInLabel.length() != 0 && childNumber > (size * 3) - 1) {
                if (!labelContains(textInLabel, value)) value = textInLabel + "," + value;
                else value = textInLabel;
            }
            ((Label) pane.getChildren().get(childNumber)).setText(value);
        }
        node = pane.getChildren().get(childNumber);
    }

    private boolean labelContains(String textInLabel, String value) {
        String[] split = textInLabel.split(",");
        for (String text : split) {
            if (text.equals(value)) return true;
        }
        return false;
    }

    private void mark(int x, int y) {
        int childNumber = transform(x,y);
        doColoring((Label) pane.getChildren().get(childNumber), "#DC143C");

    }

    private void checkFinal() {
        String text = ((Label) node).getText();
        String[] splitted = text.split(",");
        for (String root : splitted) {
            if (root.equals(tree.getRootElem().getString())) doColoring((Label) node, "#00FF00");
        }
    }

    private int transform(int x, int y) {
        return (((size - (y - 1)) * size) + x);
    }

    private void doColoring(Label label, String color) {
        label.setStyle("-fx-background-color:" + color +"; -fx-opacity: 70; -fx-alignment: CENTER; -fx-font-size: 20;");
    }


}
