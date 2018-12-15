import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;

public class ViewControllerCNF {

    private static final String MAPPING = " -> ";
    private static final String SEPARATOR = "| ";
    private static final String FIRSTRULE = "1. Regel:\n";
    private static final String FIRSTRULEDESCRIPTION = "Alle Regeln enthalten auf der rechten Seite nur Nichtterminale oder ein Terminalsymbol.\n\n";
    private static final String SECONDRULE = "2. Regel:\n";
    private static final String SECONDRULEDESCRIPTION = "Alle rechten Seiten haben eine maximale Länge von 2.\n\n";
    private static final String THIRDRULE = "3. Regel:\n";
    private static final String THIRDTRULEDESCRIPTION = "Epsilon-Übergänge werden aufgelöst und entfernt.\n\n";
    private static final String FOURTHRULE = "4. Regel:\n";
    private static final String FOURTHRULEDESCRIPTION = "Alle Kettenregeln werden aufgelöst und entfernt.\n\n";
    private static final String TERMINALRULE = "terminalRule";
    private static final String LENGTHRULE = "lengthRule";
    private static final String EPSILONRULE = "epsilonRule";
    private static final String CHAINRULE = "chainRule";


    private TextFlow nextPane;
    private TextFlow previousPane;
    private TextFlow infoBox;
    private int changeIterator = 0;
    private int step = 1;
    private Tree tree;
    private String[] change;
    private boolean firstMark = false;
    private Map<String, Elem> map;
    private Map<String, ArrayList<ArrayList<Text>>> childListMap;
    private List<String> mapKeySet;
    private List<String> changedItems;
    private List<Text> rootTextList;
    private String root;
    private String actualRoot;
    private String ruleName;


    public ViewControllerCNF(TextFlow nextPane, TextFlow previousPane, TextFlow infoBox, Tree tree) {
        this.nextPane = nextPane;
        this.previousPane = previousPane;
        this.infoBox = infoBox;
        this.tree = tree;
        //initMaps();
        //initNextStepPane();
        initAllPanes();
        changedItems = new ArrayList<>();
    }

    private void initMaps() {
        root = tree.getRootElem().getString();
        actualRoot = root;
        map = tree.getMap();
        childListMap = new HashMap<>();
        rootTextList = new ArrayList<>();
        for (Elem element: map.values()) {
            ArrayList<ArrayList<Text>> list = new ArrayList<>();
            fillListWithChildren(element, list);
            childListMap.put(element.getString(), list);
        }
        mapKeySet = new ArrayList<>(map.keySet());
        sortKeySet();
        getStep(step);
    }

    private void fillListWithChildren(Elem element, ArrayList<ArrayList<Text>> list) {
        for (Node node: element.getList()) {
            ArrayList<Text> innerList = new ArrayList<>();
            for (Elem children: node.getNodeList()) {
                innerList.add(new Text(children.getString()));
                innerList.add(new Text(" "));
            }
            list.add(innerList);
        }
    }

    private void sortKeySet() {
        List<String> sortedList = new ArrayList<>();
        sortedList.add(root);
        for (int i = 0; i < sortedList.size(); i++) {
            String child = map.get(sortedList.get(i)).getChildren();
            while(child != null) {
                if (!sortedList.contains(child) && mapKeySet.contains(child)) sortedList.add(child);
                child = map.get(sortedList.get(i)).getChildren();
            }
        }
        mapKeySet = sortedList;
    }

    private void initPane(TextFlow pane) {
        for (String elementName : mapKeySet) {
            Text rootElementText = new Text(elementName);
            pane.getChildren().add(rootElementText);
            rootTextList.add(rootElementText);
            pane.getChildren().add(new Text(MAPPING));
            for (ArrayList<Text> node : childListMap.get(elementName)) {
                iterateOverNode(node, pane);
                pane.getChildren().add(new Text(SEPARATOR));
            }
            pane.getChildren().remove(pane.getChildren().size() - 1);
            pane.getChildren().add(new Text("\n"));
        }
    }

    private void iterateOverNode(ArrayList<Text> Node, TextFlow pane) {
        for (Text text : Node) {
            pane.getChildren().add(text);
        }
    }

    private void initAllPanes() {
        //previousPane = nextPane;
        //copyPane(previousPane, nextPane);
        clearAllPanes();
        tree.setActiveStep(++step);
        initMaps();
        initPane(nextPane);
        tree.setActiveStep(--step);
        initMaps();
        initPane(previousPane);
        initInfoBox();
    }

    private void initInfoBox() {
        infoBox.getChildren().clear();
        ruleName = tree.getName();
        switch(ruleName) {
            case TERMINALRULE: infoBox.getChildren().add(new Text(FIRSTRULE));
                infoBox.getChildren().add(new Text(FIRSTRULEDESCRIPTION));
                break;
            case LENGTHRULE:  infoBox.getChildren().add(new Text(SECONDRULE));
                infoBox.getChildren().add(new Text(SECONDRULEDESCRIPTION));
                break;
            case EPSILONRULE: infoBox.getChildren().add(new Text(THIRDRULE));
                infoBox.getChildren().add(new Text(THIRDTRULEDESCRIPTION));
                break;
            case CHAINRULE:   infoBox.getChildren().add(new Text(FOURTHRULE));
                infoBox.getChildren().add(new Text(FOURTHRULEDESCRIPTION));
                break;
        }
    }


    private void clearAllPanes() {
        previousPane.getChildren().clear();
        nextPane.getChildren().clear();
        infoBox.getChildren().clear();
    }

   /* public void setNextStepPane(TextFlow nextPane) {
        this.nextPane = nextPane;
    }

    public void setPreviousTextPane(TextFlow previousPane) {
        this.previousPane = previousPane;
    }

    public void setinfoBox(TextFlow infoBox) {
        this.infoBox = infoBox;
    }*/

    /*private void initPane(Tree tree) {
        this.tree = tree;
    }*/

    public boolean next() {
        if (hasNextStep()) {
            initInfoBox();
            doNextSteps();
            return true;
        }
        else {
            if (hasNextRule()) {
                return true;
            }
        }
        return false;
    }

    public void previous() {

    }

    private void mark(String change) {
        actualRoot = change;
        changedItems.clear();
        unmark();
         //doChangeInPane();
        if (change.length() == 1) {
            getItem(change, rootTextList).setFill(Color.RED);
            actualRoot = change;
        }
        /*else {
            String[] splitted = change.split(",");
            getItem(splitted[0], rootTextList).setFill(Color.RED);
            getItem(splitted[1], childListMap.get(splitted[0]));
            actualRoot = splitted[0];
        }*/
    }

    private void doChangeInPane() {
        /*String[] splitted;
        for (String change : changedItems) {
            splitted = change.split(",");
            List<ArrayList<Text>> list = childListMap.get(splitted[0]);
            if (splitted.length == 3) {
                getItem(splitted[1], list).setText(splitted[2]);
            }
            else if (splitted.length == 2) {
                if (splitted[1].matches("0-9")) {
                    map.get(splitted[0]).getList().remove(Integer.parseInt(splitted[1]));
                    list.remove(splitted[1]);
                }
                else {
                    list.remove(getItem(splitted[1], list));
                }
            }
            else {
                map.remove(splitted[0]);
            }
        }*/
    }

    private Text getItem(String item, List<Text> list) {
        for (Text name : list) {
            if (name.getText().equals(item)) return name;
        }
        return null;
    }

    private void unmark() {
        Text text;
        for (int i = 0; i < previousPane.getChildren().size(); i++) {
            text = (Text) previousPane.getChildren().get(i);
            if (text.getFill() != Color.BLACK) text.setFill(Color.BLACK);
        }
    }

    private boolean hasNextStep() {
        return changeIterator != -1;
    }

    private boolean hasNextRule() {
        if (step < 6) {
            step++;
            initAllPanes();
            return true;
        }
        return false;
    }

    private void getStep(int step) {
        tree.setActiveStep(step);
        changeIterator = 0;
        tree.printTree();
        change = tree.getChange().split(";");
    }

    private void doNextSteps() {
        boolean retrieveChanges = true;
        while (retrieveChanges) {
            if (changeIterator < change.length) {
                retrieveChanges = doChange(change[changeIterator]);
            }
            else {
                changeIterator = -1;
                retrieveChanges = false;
            }
        }
    }

    private boolean doChange(String change) {
        if (change.contains("mark") && !firstMark) {
            firstMark = true;

            //return true;
        }
        else if (change.contains("mark") && firstMark) {
            firstMark = false;
            return firstMark;
        }
        changeIterator++;
        setChange(change);
        return firstMark;
    }

    private void setChange(String change) {
        int begin = change.indexOf('(') + 1;
        int end = change.indexOf(')');
        if(change.contains("mark")) {
            change = change.substring(begin,end);
            mark(change);
        }
        else if (change.contains("highlightAll")) {
            change = change.substring(begin, end);
            highlightAll(change);
        }
        else if(change.contains("highlight")) {
            change = change.substring(begin,end);
            //highlightChainRule.add(change);
            highlight(change);
        }
        else if(change.contains("addNode")) {
            change = change.substring(begin,end);
            addNode(change, infoBox);
        }
        else if(change.contains("addTo")) {
            change = change.substring(begin,end);
            addTo(change, infoBox);
        }
        else if(change.contains("add")) {
            change = change.substring(begin,end);
            add(change, infoBox);
        }
        else if(change.contains("deleteNode")) {
            change = change.substring(begin,end);
            deleteNode(change, infoBox);
            changedItems.add(change);
        }
        else if(change.contains("delete")) {
            change = change.substring(begin,end);
            delete(change, infoBox);
            changedItems.add(change);
        }
        else if(change.contains("replace")) {
            change = change.substring(begin,end);
            replace(change, infoBox);
            changedItems.add(change);
        }
    }

    private void highlight(String element) {
        String[] split = element.split(",");
        if (split.length == 0) {
            highlightFromRootList(element);
        }
        else {
            highlightChildren(split[0], Integer.parseInt(split[1]));
        }
    }

    private void highlightFromRootList(String element) {
        for (Text rootElement : rootTextList) {
            if (rootElement.getText().equals(element)) rootElement.setFill(Color.DARKGOLDENROD);
        }
    }

    private void highlightChildren(String element, int counter) {
        for (ArrayList<Text> node : childListMap.get(actualRoot)) {
            int count = 0;
            for (Text children : node) {
                if (children.getText().equals(element) && count == counter) {
                    children.setFill(Color.DARKGOLDENROD);
                    break;
                }
                else if (children.getText().equals(actualRoot) && ruleName.equals(CHAINRULE)) {
                    children.setFill(Color.RED);
                }
                if  (children.getText().equalsIgnoreCase(" ")) count++;
            }
        }
    }

    private void highlightChildren(String root, String element) {
        for (ArrayList<Text> node : childListMap.get(root)) {
            for (Text children : node) {
                if (children.getText().equals(element)) {
                    children.setFill(Color.DARKGOLDENROD);
                }
            }

        }
    }

    private void highlightAll(String element) {
        highlightFromRootList(element);
        for (Text rootElement : rootTextList) {
            highlightChildren(rootElement.getText(), element);
        }
    }


    private void addNode(String change, TextFlow infoBox) {
        String[] split = change.split(",");
        int value = Integer.parseInt(split[2]) + 1;
        infoBox.getChildren().add(new Text("Füge der Wurzel " + split[0] + " in Abbildung " + value + " das Element " + split[1] + " hinzu.\n"));
    }

    private void addTo(String change, TextFlow infoBox) {
        String[] split = change.split(",");
        infoBox.getChildren().add(new Text("Füge der Wurzel " + split[0] + " das Element " + split[1] + " hinzu.\n"));
    }

    private void add(String change, TextFlow infoBox) {
        infoBox.getChildren().add(new Text("Erstelle die Wurzel " + change + ".\n"));
    }

    private void deleteNode(String change, TextFlow infoBox) {
        String[] split = change.split(",");
        int value = Integer.parseInt(split[1]) + 1;
        infoBox.getChildren().add(new Text("Lösche die Abbildung " + value  + " von der Wurzel " + split[0] + ".\n"));
    }

    private void delete(String change, TextFlow infoBox) {
        String[] split = change.split(",");
        if (split.length == 1) infoBox.getChildren().add(new Text("Lösche die Wurzel " + split[0] + ".\n"));
        else infoBox.getChildren().add(new Text("Lösche das Element " + split[1] + " von der Wurzel " + split[0] + ".\n"));

    }

    private void replace(String change, TextFlow infoBox) {
        String[] split = change.split(",");
        if (split.length == 2) infoBox.getChildren().add(new Text("Ersetze das Element " + split[0] + " mit dem Element " + split[1] + ".\n"));
        else infoBox.getChildren().add(new Text("Ersetze in Wurzel " + split[0] + " das Element " + split[1] + " mit dem neuen Element " + split[2] + ".\n"));
    }


}
