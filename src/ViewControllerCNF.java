import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.lang.reflect.Array;
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
    private static final String CLEANUP = "Ersetze alle Abbildung auf nur ein Nichtterminal mit dessen Abbildungen und lösche doppelte, sowie Abbildungen auf die Wurzel selbst.\n\n";
    private static final String TERMINALRULE = "terminalRule";
    private static final String LENGTHRULE = "lengthRule";
    private static final String EPSILONRULE = "epsilonRule";
    private static final String CHAINRULE = "chainRule";
    private static final String ADDTOROOT = "Füge der Wurzel ";
    private static final String DEPICT = " die Abbildung ";
    private static final String ELEMENT = " das Element ";
    private static final String ADDSTRING = " hinzu.\n";
    private static final String CREATEROOT = "Erstelle die Wurzel ";
    private static final String LINEENDSTRING = ".\n";
    private static final String LINEEND = "\n";
    private static final String DELETEDEPICT = "Lösche die Abbildung ";
    private static final String FROMROOT = " von der Wurzel ";
    private static final String DELETEROOT = "Lösche die Wurzel ";
    private static final String DELETEELEMENT = "Lösche das Element ";
    private static final String REPLACEELEMET = "Ersetze das Element ";
    private static final String REPLACEINROOT = "Ersetze in Wurzel ";
    private static final String WITHELEMENT = " mit dem Element ";
    private static final String WITHNEWELEMENT = " mit dem neuen Element ";
    private static final String MARK = "mark";
    private static final String HIGHLIGHT = "highlight";
    private static final String HIGHLIGHTALL = "highlightAll";
    private static final String ADDTO = "addTo";
    private static final String ADD = "add";
    private static final String ADDNODE = "addNode";
    private static final String DELETE = "delete";
    private static final String DELETENODE = "deleteNode";
    private static final String REPLACE = "replace";
    private static final String COMMA = ",";
    private static final String SPACE = " ";
    private static final String SEMICOLON = ";";
    private static final String OPENBRACKET = "(";
    private static final String CLOSEBRACKET = ")";



    private TextFlow nextPane;
    private TextFlow previousPane;
    private TextFlow infoBox;
    private int changeIterator = 0;
    private int step = 1;
    private int previousChangeMark = 0;
    private Tree tree;
    private String[] change;
    private boolean firstMark = false;
    private Map<String, Elem> map;
    private Map<String, ArrayList<ArrayList<Text>>> childListMap;
    private Map<String, ArrayList<ArrayList<Text>>> tempMap;
    private List<String> mapKeySet;
    private List<String> changedItems;
    private List<Text> rootTextList;
    private ArrayList<Text> addtoList;
    private String root;
    private String actualRoot;
    private String ruleName;
    private int deleted;
    private int cleanUpRoutine = 0;


    public ViewControllerCNF(TextFlow nextPane, TextFlow previousPane, TextFlow infoBox, Tree tree) {
        this.nextPane = nextPane;
        this.previousPane = previousPane;
        this.infoBox = infoBox;
        this.tree = tree;
        initAllPanes();
    }

    private void initMaps() {
        root = tree.getRootElem().getString();
        actualRoot = root;
        map = tree.getMap();
        childListMap = new HashMap<>();
        rootTextList = new ArrayList<>();
        changedItems = new ArrayList<>();
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
                innerList.add(new Text(SPACE));
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
        rootTextList.clear();
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
            pane.getChildren().add(new Text(LINEEND));
        }
    }

    private void iterateOverNode(ArrayList<Text> Node, TextFlow pane) {
        for (Text text : Node) {
            pane.getChildren().add(text);
        }
    }

    private void initAllPanes() {
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

    public boolean next() {
        if (hasNextStep()) {
            initInfoBox();
            doNextSteps();
            return true;
        }
        else if (!hasNextStep() && cleanUpRoutine != 0) {
            if (cleanUpRoutine == 1) initCleanUp();
            else doCleanUp();
        }
        else {
            if (hasNextRule()) {
                return true;
            }
        }
        return false;
    }

    public void previous() {
        if (hasPreviousStep()) {
            initAllPanes();
            doUndoSteps();
        }
        else if (hasPreviousRule()) {
            doUndoSteps();
        }
    }

    private void mark(String change) {
        actualRoot = change;
        unmark();
        doChangeInPane();
        previousPane.getChildren().clear();
        initPane(previousPane);
        getItem(change, rootTextList).setFill(Color.RED);
        actualRoot = change;

    }

    private void doChangeInPane() {
        String[] splitted;
        tempMap = new HashMap<>();
        copyList();
        for (String change : changedItems) {
            splitted = change.split(COMMA);
            switch(splitted[0]) {
                case ("0") : doAddNode(splitted[1], splitted[2], splitted[3]);
                             break;
                case ("1") : doDeleteNode(splitted[1], splitted[2]);
                             break;
                case ("2") : if (splitted.length == 2) doDelete(splitted[1]);
                             else doDelete(splitted[1], splitted[2]);
                             break;
                case ("3") : if (splitted.length == 3) doReplace(splitted[1], splitted[2]);
                             else doReplace(splitted[1], splitted[2], splitted[3]);
                             break;
                case ("4") : doAddTo(splitted[1], splitted[2]);
                             break;
                case ("5") : doAdd(splitted[1]);
                             break;
            }
        }
        changedItems.clear();
        childListMap.putAll(tempMap);
    }

    private void copyList() {
        for (String root : childListMap.keySet()) {
            ArrayList<ArrayList<Text>> tempList = new ArrayList<>();
            for (ArrayList<Text> list : childListMap.get(root)) {
                tempList.add(list);
                tempMap.put(root,tempList);
            }
        }

    }

    private void doAddNode(String root, String rootOfNode, String node) {
        int nodeIndex = Integer.parseInt(node);
        if (tempMap.containsKey(root) && tempMap.containsKey(rootOfNode)) {
            tempMap.get(root).add(tempMap.get(rootOfNode).get(nodeIndex));
        }
    }

    private void doDeleteNode(String root, String node) {
        int nodeIndex = Integer.parseInt(node);
        deleted = nodeIndex;
        ArrayList<Text> list = childListMap.get(root).get(nodeIndex);
        tempMap.get(root).remove(list);
        if (tempMap.get(root).isEmpty()) mapKeySet.remove(root);
    }

    private void doDelete(String root) {
        if (mapKeySet.contains(root)) {
            mapKeySet.remove(root);
            childListMap.remove(root);
        }
    }

    private void doDelete(String root, String element) {
        for (ArrayList<Text> list : tempMap.get(root)) {
            for (Text children : list) {
                if (children.getText().equals(element)) {
                    list.remove(children);
                    //break??
                }
            }
        }
    }

    private void doReplace(String replaceValue, String elementToReplace) {
        for (String root : mapKeySet) {
            if (tempMap.containsKey(root)) {
                for (ArrayList<Text> list: tempMap.get(root)) {
                    replaceInList(list, replaceValue, elementToReplace, false);
                }
            }
        }
        replaceInList(rootTextList, replaceValue, elementToReplace, false);
    }

    private boolean replaceInList(List<Text> list, String newValue, String oldValue, boolean stopAfterFirstChange) {
        for (Text text : list) {
            if (text.getText().equals(oldValue)) {
                text.setText(newValue);
                if (stopAfterFirstChange) return true;
            }
        }
        return false;
    }

    private void doReplace(String root, String oldElement, String newElement) {
        for (ArrayList<Text> list : tempMap.get(root)) {
            if (replaceInList(list,newElement, oldElement, true)) break;
        }
    }

    private void doAddTo(String root, String element) {
        if (ruleName.equals(LENGTHRULE) && addtoList.size() == 1) {
            addtoList.add(new Text(SPACE));
            addtoList.add(new Text(element));
            addToRoot(new ArrayList<>(addtoList), root);
            addtoList.clear();
        }
        else if (!ruleName.equalsIgnoreCase(LENGTHRULE)) {
            addtoList.add(new Text(element));
            addToRoot(new ArrayList<>(addtoList), root);
            addtoList.clear();
        }
        else {
            addtoList.add(new Text(element));
        }
    }

    private void addToRoot(ArrayList<Text> list, String root) {
        if(ruleName.equals(LENGTHRULE) && deleted != -1) {
            tempMap.get(root).add(deleted, list);
            deleted = -1;
        }
        else tempMap.get(root).add(list);
    }

    private void doAdd(String newRoot) {
        mapKeySet.add(newRoot);
        if (!tempMap.containsKey(newRoot)) {
            ArrayList<ArrayList<Text>> list = new ArrayList<>();
            tempMap.put(newRoot, list);
        }
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
        if (step < 4) {
            step++;
            initAllPanes();
            return true;
        }
        return false;
    }

    private boolean hasPreviousStep() {
        return changeIterator != 0;
    }

    private boolean hasPreviousRule() {
        if (step > 1) {
            step--;
            initAllPanes();
            changeIterator = 0;
            previousChangeMark = change.length - 1;
            return true;
        }
        return false;
    }

    private void doUndoSteps() {
        int lastMark = previousChangeMark;
        if (cleanUpRoutine != 0) {
            cleanUpRoutine--;
            doUndoStepsForCleaner();
        }
        else {
            while (changeIterator != lastMark && changeIterator != -1) {
                next();
            }
        }
    }

    private void doUndoStepsForCleaner() {
        if (cleanUpRoutine == 1) {
            while(cleanUpRoutine != 1) {
                next();
            }
        }
        else {
            while (changeIterator != -1) {
                next();
                cleanUpRoutine = 0;
            }
        }
    }

    private void getStep(int step) {
        tree.setActiveStep(step);
        changeIterator = 0;
        tree.printTree();
        change = tree.getChange().split(SEMICOLON);
    }

    private void doNextSteps() {
        cleanUpRoutine = 0;
        boolean retrieveChanges = true;
        previousChangeMark = changeIterator;
        while (retrieveChanges) {
            if (changeIterator < change.length) {
                retrieveChanges = doChange(change[changeIterator]);
            }
            else {
                changeIterator = -1;
                firstMark = false;
                retrieveChanges = false;
                if (step == 4) cleanUpRoutine = 1;
            }
        }
    }

    private boolean doChange(String change) {
        if (change.contains(MARK) && !firstMark) {
            firstMark = true;
        }
        else if (change.contains(MARK) && firstMark) {
            firstMark = false;
            return firstMark;
        }
        changeIterator++;
        setChange(change);
        return firstMark;
    }

    private void setChange(String change) {
        int begin = change.indexOf(OPENBRACKET) + 1;
        int end = change.indexOf(CLOSEBRACKET);
        if(change.contains(MARK)) {
            change = change.substring(begin,end);
            mark(change);
            addtoList = new ArrayList<>();
        }
        else if (change.contains(HIGHLIGHTALL)) {
            change = change.substring(begin, end);
            highlightAll(change);
        }
        else if(change.contains(HIGHLIGHT)) {
            change = change.substring(begin,end);
            highlight(change);
        }
        else if(change.contains(ADDNODE)) {
            change = change.substring(begin,end);
            addNode(change, infoBox);
            changedItems.add("0," + change);
        }
        else if(change.contains(ADDTO)) {
            change = change.substring(begin,end);
            addTo(change, infoBox);
            changedItems.add("4," + change);
        }
        else if(change.contains(ADD)) {
            change = change.substring(begin,end);
            add(change, infoBox);
            changedItems.add("5," + change);
        }
        else if(change.contains(DELETENODE)) {
            change = change.substring(begin,end);
            deleteNode(change, infoBox);
            changedItems.add("1," + change);
        }
        else if(change.contains(DELETE)) {
            change = change.substring(begin,end);
            delete(change, infoBox);
            changedItems.add("2," + change);
        }
        else if(change.contains(REPLACE)) {
            change = change.substring(begin,end);
            replace(change, infoBox);
            changedItems.add("3," + change);
        }
    }

    private void highlight(String element) {
        String[] split = element.split(COMMA);
        if (split.length == 1) {
            if (ruleName.equals(CHAINRULE)) highlightAll(element);
            else highlightFromRootList(element);
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
                if  (children.getText().equalsIgnoreCase(SPACE)) count++;
            }
        }
    }

    private void highlightChildren(String root, String element) {
        for (ArrayList<Text> node : childListMap.get(root)) {
            for (Text children : node) {
                if (children.getText().equals(element)) {
                    children.setFill(Color.DARKGOLDENROD);
                }
                else if(children.getText().equals(actualRoot) && ruleName.equals(EPSILONRULE)) {
                    children.setFill(Color.DARKGOLDENROD);
                }
                else if (children.getText().equals(actualRoot) && ruleName.equals(CHAINRULE) && getItem(root, rootTextList).getFill().equals(Color.DARKGOLDENROD)) {
                    children.setFill(Color.RED);
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
        String[] split = change.split(COMMA);
        int value = Integer.parseInt(split[2]) + 1;
        infoBox.getChildren().add(new Text(ADDTOROOT + split[0] + DEPICT + value + FROMROOT + split[1] + ADDSTRING));
    }

    private void addTo(String change, TextFlow infoBox) {
        String[] split = change.split(COMMA);
        infoBox.getChildren().add(new Text(ADDTOROOT + split[0] + ELEMENT + split[1] + ADDSTRING));
    }

    private void add(String change, TextFlow infoBox) {
        infoBox.getChildren().add(new Text(CREATEROOT + change + LINEENDSTRING));
    }

    private void deleteNode(String change, TextFlow infoBox) {
        String[] split = change.split(COMMA);
        int value = Integer.parseInt(split[1]) + 1;
        infoBox.getChildren().add(new Text(DELETEDEPICT + value  + FROMROOT + split[0] + LINEENDSTRING));
    }

    private void delete(String change, TextFlow infoBox) {
        String[] split = change.split(COMMA);
        String infoText;
        if (split.length == 1) {
            infoText = DELETEROOT + split[0] + LINEENDSTRING;
            checkDoubleDelete(infoText);
            infoBox.getChildren().add(new Text(infoText));
        }
        else {
            infoText = DELETEELEMENT + split[1] + FROMROOT+ split[0] + LINEENDSTRING;
            checkDoubleDelete(infoText);
            infoBox.getChildren().add(new Text());
        }

    }

    private void replace(String change, TextFlow infoBox) {
        String[] split = change.split(COMMA);
        if (split.length == 2) infoBox.getChildren().add(new Text(REPLACEELEMET + split[1] + WITHELEMENT + split[0] + LINEENDSTRING));
        else infoBox.getChildren().add(new Text(REPLACEINROOT + split[0] + ELEMENT + split[1] + WITHNEWELEMENT + split[2] + LINEENDSTRING));
    }

    private void checkDoubleDelete(String infoText) {
        for (int i = 0; i < infoBox.getChildren().size(); i++) {
            Text text = (Text) infoBox.getChildren().get(i);
            if (text.getText().equals(infoText)) {
                infoBox.getChildren().remove(i);

            }
        }
    }

    private boolean initCleanUp() {
        boolean returnValue = false;
        doChangeInPane();
        previousPane.getChildren().clear();
        initPane(previousPane);
        cleanUpRoutine++;
        unmark();
        initInfoBox();
        infoBox.getChildren().add(new Text(CLEANUP));
        for (String rootElement : mapKeySet) {
            ArrayList<String> detector = new ArrayList<>();
            for (ArrayList<Text> innerList : childListMap.get(rootElement)) {
                 boolean returnedValue =  checkCleanUpRoutines(rootElement, innerList,detector);
                 if (!returnValue) returnValue = returnedValue;
            }
        }
        return returnValue;
    }

    private boolean checkCleanUpRoutines(String rootElement, ArrayList<Text> list, ArrayList<String> detector) {
        boolean returnValue = false;
        if (list.size() == 2 && mapKeySet.contains(list.get(0).getText())) {
            if (list.get(0).getText().equals(rootElement)) {
                list.get(0).setFill(Color.RED);
                changedItems.add(new String("1," + rootElement + COMMA + childListMap.get(rootElement).indexOf(list)));
                returnValue = true;
            }
            else {
                list.get(0).setFill(Color.DARKGOLDENROD);
                returnValue = true;
            }
        }
        String foundDepict = findIdenticalinList(list);
        if (detector.contains(foundDepict)) {
            mark(list);
            changedItems.add(new String("1," + rootElement + COMMA + childListMap.get(rootElement).indexOf(list)));
            returnValue = true;
        }
        else detector.add(foundDepict);
        return returnValue;
    }

    private String findIdenticalinList(ArrayList<Text> list) {
        String value = "";
        for (Text text : list) {
            value += text.getText();
        }
        return value;
    }

    private void mark(ArrayList<Text> depict) {
        for (Text text: depict) {
            text.setFill(Color.RED);
        }
    }

    private void doCleanUp() {
        boolean changesFound = true;
        while(changesFound) {
            removeAllRedMarked();
            for (String root : mapKeySet) {
                ArrayList<ArrayList<Text>> toAdd = new ArrayList<>();
                for (ArrayList<Text> innerList : childListMap.get(root)) {
                    doCleanUp(innerList,toAdd);
                }
                childListMap.put(root,toAdd);
            }
            previousPane.getChildren().clear();
            initPane(previousPane);
            changesFound = initCleanUp();
        }
    }

    private void doCleanUp(ArrayList<Text> list,ArrayList<ArrayList<Text>> toAdd) {
        if (list.get(0).getFill().equals(Color.DARKGOLDENROD)) {
            String replacedRootName = list.get(0).getText();
            for (ArrayList<Text> innerList : childListMap.get(replacedRootName)) {
                toAdd.add(copyNode(innerList));
            }
        }
        else {
            toAdd.add(list);
        }
    }

    private ArrayList<Text> copyNode(ArrayList<Text> list) {
        ArrayList<Text> newList = new ArrayList<>();
        for (Text text : list) {
            newList.add(new Text(text.getText()));
        }
        return newList;
    }

    private void removeAllRedMarked() {
        doChangeInPane();
    }
}