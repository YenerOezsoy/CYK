/*import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewController {

    private Output output;
    private Tree tree;
    private String[] nonTerminals;
    private String[] terminals;
    private String[] line;
    private String left;
    private String[] right;
    private String path;
    private String[] change;
    private String name;
    private int changeIterator = 0;
    private boolean changeStep = false;
    private HashMap<String,Elem> map;
    private HashMap<String,Elem> previousMap;
    private ArrayList<Text> rootList;
    private ArrayList<Text> childList;
    private HashMap<String, ArrayList<Text>> childMap;
    private ArrayList<String> highlightChainRule;
    private String actualRoot;
    private boolean firstCNF = true;
    private String changedItems;

    public void setPath(String path) {
        this.path = path;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public boolean userInput(String nonTerminal, String terminal, String startsymbol, String production) {
        if (checkInput(nonTerminal,terminal, startsymbol, production)) return tree.buildTree();
        else return false;
    }

    public void initPane(TextFlow flow) {
        flow.getChildren().clear();
        this.map = tree.getMap();
        List<String> list = new ArrayList<>(map.keySet());
        list = listSort(list, map);
        if(firstCNF) rootList.clear();
        for (int i = 0; i < list.size(); i++) {
            String root = null;
            if(firstCNF) root = map.get(list.get(i)).getString();
            initRoot(flow, map.get(list.get(i)));
            String result = map.get(list.get(i)).getChildren();
            childList = new ArrayList<>();
            while(result != null) {
                Text text = new Text(result);
                flow.getChildren().add(text);
                if(firstCNF) childList.add(text);
                result = map.get(list.get(i)).getChildren();
            }
            if(firstCNF) childMap.put(root, childList);
            flow.getChildren().add(new Text("\n"));
        }
        changedItems = null;
        firstCNF = !firstCNF;
    }

    public Tree getStep(int step) {
        if (step < 6) {
            previousMap = tree.getMap();
            tree.setActiveStep(step);
            tree.printTree();
            this.change = tree.getChange().split(";");
            changeIterator = -2;
            if(firstCNF) clearList();
            return tree;
        }
        return null;
    }

    public void initInfobox(TextFlow infobox) {
        writeInfobox(infobox);
    }

    public boolean writeNextInfobox(boolean next, TextFlow infobox, TextFlow previousCNFText) {
        if (changeIterator == -2) {
            changeIterator = 0;
            writeInfobox(infobox);
            return true;
        }
        if(!next) changeIterator = findPreviousMark(changeIterator);
        if (changeIterator == -1) {
            if (!hasPreviousStep()) return false;
        }
        String change = getChange();
        writeInfobox(infobox);
        highlightChainRule = new ArrayList<>();
        if (change == null) return false;
        while (change != null && !change.equals("/end")) {
            writeChange(change, infobox, previousCNFText);
            change = getChange();
        }
        if(name.equals("chainRule")) highlightChildren();
        return true;
    }

    private boolean hasPreviousStep() {
        int step = tree.getActiveStep();
        if (step > 0) {
            step = step--;
            getStep(step);
            changeIterator = change.length;
            findPreviousMark(changeIterator);
            return true;
        }
        return false;
    }

    private String getChange() {
        if (changeIterator < change.length) {
            if (change[changeIterator].contains("mark") && !changeStep) {
                changeStep = true;
                return change[changeIterator++];
            }
            else if (change[changeIterator].contains("mark") && changeStep) {
                changeStep = false;
                return "/end";
            }
            else return change[changeIterator++];
        }
        changeStep = false;
        return null;
    }

    private int findPreviousMark(int changeIterator) {
        boolean secondMark = false;
        while (changeIterator > 0) {
            --changeIterator;
            if (change[changeIterator].contains("mark") && secondMark) return changeIterator;
            else if(change[changeIterator].contains("mark") && !secondMark) secondMark = true;
        }
        return -1;
    }


    /*private boolean checkInput(String nonTerminal, String terminal, String startsymbol, String production) {
        nonTerminals = nonTerminal.split("\\s+");
        terminals = terminal.split("\\s+");
        if(isTerminalInNonterminal()) return false ;//FEHLER
        if(!isStartsymbolNonterminal(startsymbol)) return false ; //Fehler
        writeInput(startsymbol, production);
        tree = new Tree(path);
        return true;
    }

    private boolean isTerminalInNonterminal() {
        for (int i = 0; i < nonTerminals.length; i++) {
            for (int j = 0; j < terminals.length; j++) {
                if(terminals[j].equals(nonTerminals[i])) return true;
            }
        }
        return false;
    }

    private boolean isStartsymbolNonterminal(String startsymbol) {
        for (int i = 0; i < nonTerminals.length; i++) {
            if (nonTerminals[i].equals(startsymbol)) return true;
        }
        return false;
    }


    private void writeInput(String startsymbol, String production) {
        output.addToFile();
        writeName();
        writeNonterminal();
        writeTerminal();
        writeStartsymbol(startsymbol);
        writeProduction(production);
        output.finishInit();
    }

    private void writeName() {
        output.initName("Grammatik");
    }

    private void writeNonterminal() {
        output.initNonterminal(writer(nonTerminals));
    }

    private void writeTerminal() {
        output.initTerminal(writer(terminals));
    }

    private String writer(String[] symbol) {
        String toWrite = "";
        for (int i = 0; i < symbol.length; i++) {
            toWrite += symbol[i] + ";";
        }
        return toWrite.substring(0, toWrite.length() - 1);
    }

    private void writeStartsymbol(String startsymbol) {
        output.initStartsymbol(startsymbol);
    }


    private void writeProduction(String production) {
        output.initProduction();
        line = production.split("\n");
        for (int i = 0; i < line.length; i++) {
            String[] split = line[i].split("->");
            left = split[0];
            right = split[1].split(",");
            output.initLine(left, right);
        }

    }

    private void initRoot(TextFlow flow, Elem root) {
        Text rootText = new Text(root.getString());
        flow.getChildren().add(rootText);
        if(firstCNF) rootList.add(rootText);
        flow.getChildren().add(new Text("->"));
    }

    private List<String> listSort(List<String> list, HashMap<String,Elem> map) {
   /*     List<String> sortedList = new ArrayList<>();
        sortedList.add(tree.getRootElem().getString());
        for (int i = 0; i < sortedList.size(); i++) {
            String child = map.get(sortedList.get(i)).getChildren();
            while(child != null) {
                if (!sortedList.contains(child) && list.contains(child)) sortedList.add(child);
                child = map.get(sortedList.get(i)).getChildren();
            }
        }
        return sortedList;
    }

    private void writeInfobox(TextFlow infobox) {
        infobox.getChildren().clear();
        setNameInfobox(tree.getName(), infobox);
    }

    private void setNameInfobox(String name, TextFlow infobox) {
        this.name = name;
        switch(name) {
            case "terminalRule": infobox.getChildren().add(new Text("1. Regel:\n"));
                infobox.getChildren().add(new Text("Alle Regeln enthalten auf der rechten Seite nur Nichtterminale oder ein Terminalsymbol.\n\n"));
                break;
            case "lengthRule":  infobox.getChildren().add(new Text("2. Regel:\n"));
                infobox.getChildren().add(new Text("Alle rechten Seiten haben eine maximale Länge von 2.\n\n"));
                break;
            case "epsilonRule": infobox.getChildren().add(new Text("3. Regel:\n"));
                infobox.getChildren().add(new Text("Epsilon-Übergänge werden aufgelöst und entfernt.\n\n"));
                break;
            case "chainRule":   infobox.getChildren().add(new Text("4. Regel:\n"));
                infobox.getChildren().add(new Text("Alle Kettenregeln werden aufgelöst und entfernt.\n\n"));
                break;
        }

    }

    /*private void writeChange(String change, TextFlow infobox, TextFlow previousCNFText) {
        int begin = change.indexOf('(') + 1;
        int end = change.indexOf(')');
        if(change.contains("mark")) {
            change = change.substring(begin,end);
            mark(change, previousCNFText);
        }
        else if (change.contains("highlightAll")) {
            change = change.substring(begin, end);
            highlightAll(change, previousCNFText);
        }
        else if(change.contains("highlight")) {
            change = change.substring(begin,end);
            highlightChainRule.add(change);
            highlight(change);
        }
        else if(change.contains("addNode")) {
            change = change.substring(begin,end);
            addNode(change, infobox);
        }
        else if(change.contains("addTo")) {
            change = change.substring(begin,end);
            addTo(change, infobox);
        }
        else if(change.contains("add")) {
            change = change.substring(begin,end);
            add(change, infobox);
        }
        else if(change.contains("deleteNode")) {
            change = change.substring(begin,end);
            deleteNode(change, infobox);
            changedItems = change;
        }
        else if(change.contains("delete")) {
            change = change.substring(begin,end);
            delete(change, infobox);
            changedItems = change;
        }
        else if(change.contains("replace")) {
            change = change.substring(begin,end);
            replace(change, infobox);
            changedItems = change;
        }
    }

    private void mark(String change, TextFlow previousCNF) {
        unmark(previousCNF);
        doChanges();
        if (change.length() == 1) {
            findSymbolInList(change, rootList);
            actualRoot = change;
        }
        else {
            String[] splitted = change.split(",");
            findSymbolInList(splitted[0], rootList);
            findSymbolInList(splitted[1], childMap.get(splitted[0]));
            actualRoot = splitted[0];
        }

    }

    private void findSymbolInList(String symbol, ArrayList<Text> list) {
        for (Text name: list) {
            if (name.getText().equals(symbol)) {
                name.setFill(Color.RED);
                break;
            }
        }
    }

    private void unmark(TextFlow previousCNF) {
        for (int i = 0; i < previousCNF.getChildren().size(); i++) {
            if( ((Text) previousCNF.getChildren().get(i)).getFill() != Color.BLACK) ((Text) previousCNF.getChildren().get(i)).setFill(Color.BLACK);
        }
    }

    private void highlightAll(String change, TextFlow previousCNF) {
        for (int i = 0; i < previousCNF.getChildren().size(); i++) {
            Text text = (Text) previousCNF.getChildren().get(i);
            if(text.getText().equals(change)) text.setFill(Color.DARKGOLDENROD);
        }
    }

    /*private void doChanges() {
        if (changedItems != null) {
            String[] splitted = changedItems.split(",");
            List<Text> list = childMap.get(splitted[0]);
            if (splitted.length == 3) {
                getItem(splitted[1], list).setText(splitted[2]);
            }
            else if (splitted.length == 2) {
                if (splitted[1].matches("0-9")) {
                    map.get(splitted[0]).getList().remove(Integer.parseInt(splitted[1]));
                }
                else {
                    list.remove(getItem(splitted[1], list));
                }
            }
            else {
                map.remove(splitted[0]);
            }

        }
    }

    private Text getItem(String item, List<Text> list) {
        for (Text name: list) {
            if (name.getText().equals(item)) {
            return name;
            }
        }
        return null;
    }

    private void highlight(String change) {
        String[] split = change.split(",");
        if(split.length == 1) highlightRoot(split[0]);
        else highlightChild(split);
    }

    private void highlightRoot(String root) {
        for (int j = 0; j < rootList.size(); j++) {
            if (rootList.get(j).getText().equals(root)) {
                rootList.get(j).setFill(Color.DARKGOLDENROD);
            }
        }
    }

    private void highlightChild(String[] split) {
        int value = Integer.parseInt(split[1]);
        int counter = 0;
        for (int i = 0; i < childMap.get(actualRoot).size(); i++) {
            if (childMap.get(actualRoot).get(i).getText().equals(split[0]) && counter == value) {
                childMap.get(actualRoot).get(i).setFill(Color.DARKGOLDENROD);
                break;
            }
            else if (childMap.get(actualRoot).get(i).getText().equals("| ")) counter = 0;
            else if (childMap.get(actualRoot).get(i).getText().equals(" ")) counter++;
        }
    }

    private void highlightChildren() {
        String root = actualRoot;
        for (int j = 0; j < childMap.get(root).size(); j++) {
            for (int i = 0; i < highlightChainRule.size(); i++) {
                if(childMap.get(root).get(j).getText().equals(highlightChainRule.get(i))) {
                    childMap.get(root).get(j).setFill(Color.DARKGOLDENROD);
                    root = childMap.get(root).get(j).getText();
                    highlightChainRule.remove(i);
                    i = j = 0;
                }
            }
        }

        for (int i = 0; i < childMap.get(root).size(); i++) {
            if (childMap.get(root).get(i).getText().equals(actualRoot)) childMap.get(root).get(i).setFill(Color.RED);
        }

    }

    private void addNode(String change, TextFlow infobox) {
        String[] split = change.split(",");
        int value = Integer.parseInt(split[2]) + 1;
        infobox.getChildren().add(new Text("Füge der Wurzel " + split[0] + " in Abbildung " + value + " das Element " + split[1] + " hinzu.\n"));
    }

    private void addTo(String change, TextFlow infobox) {
        String[] split = change.split(",");
        infobox.getChildren().add(new Text("Füge der Wurzel " + split[0] + " das Element " + split[1] + " hinzu.\n"));
    }

    private void add(String change, TextFlow infobox) {
        infobox.getChildren().add(new Text("Erstelle die Wurzel " + change + ".\n"));
    }

    private void deleteNode(String change, TextFlow infobox) {
        String[] split = change.split(",");
        int value = Integer.parseInt(split[1]) + 1;
        infobox.getChildren().add(new Text("Lösche die Abbildung " + value  + " von der Wurzel " + split[0] + ".\n"));
    }

    private void delete(String change, TextFlow infobox) {
        String[] split = change.split(",");
        if (split.length == 1) infobox.getChildren().add(new Text("Lösche die Wurzel " + split[0] + ".\n"));
        else infobox.getChildren().add(new Text("Lösche das Element " + split[1] + " von der Wurzel " + split[0] + ".\n"));

    }

    private void replace(String change, TextFlow infobox) {
        String[] split = change.split(",");
        if (split.length == 2) infobox.getChildren().add(new Text("Ersetze das Element " + split[0] + " mit dem Element " + split[1] + ".\n"));
        else infobox.getChildren().add(new Text("Ersetze in Wurzel " + split[0] + " das Element " + split[1] + " mit dem neuen Element " + split[2] + ".\n"));
    }

    private void clearList() {
        rootList = new ArrayList<>();
        childList = new ArrayList<>();
        childMap = new HashMap<>();
    }
}

*/


