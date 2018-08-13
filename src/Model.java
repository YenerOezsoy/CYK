import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Model {

    private CNF cnf;
    private Output output;
    private Tree tree;
    private String[] nonTerminals;
    private String[] terminals;
    private String[] line;
    private String left;
    private String[] right;
    private String path;
    private String[] change;
    private int changeIterator = 0;
    private boolean changeStep = false;
    private HashMap<String,Elem> map;
    private ArrayList<Text> rootList;
    private ArrayList<Text> childList;
    private HashMap<String, ArrayList<Text>> childMap;

    public Model() {

    }

    public void readFile(String path) {
        this.path = path;
        tree = new Tree(path);
    }

    public void writeFile(String path) {
        System.out.println(path);
        this.path = path;
        output = new Output(path);
        output.newFile();
    }

    public boolean writeIntoFile(String nonTerminal, String terminal, String startsymbol, String production) {
        if (!userInput(nonTerminal, terminal, startsymbol, production)) {
            return false;
        }
        output.addToFile();
        cnf = new CNF(tree, output);
        cnf.startCNF();
        tree = new Tree(path);
        return true;
    }



    public boolean userInput(String nonTerminal, String terminal, String startsymbol, String production) {
        if (checkInput(nonTerminal,terminal, startsymbol, production)) return tree.buildTree();
        else return false;
    }

    public Tree getStep(int step) {
        if (step < 6) {
            tree.setActiveStep(step);
            tree.printTree();
            this.change = tree.getChange().split(";");
            changeIterator = 0;
            clearList();
            return tree;
        }
        return null;
    }

    private boolean checkInput(String nonTerminal, String terminal, String startsymbol, String production) {
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

    public void initPane(TextFlow flow) {
        flow.getChildren().clear();
        this.map = tree.getMap();
        List<String> list = new ArrayList<>(map.keySet());
        list = listSort(list, map);
        for (int i = 0; i < list.size(); i++) {
            initRoot(flow, map.get(list.get(i)));
            String result = map.get(list.get(i)).getChildren();
            while(result != null) {
                Text text = new Text(result);
                flow.getChildren().add(text);
                result = map.get(list.get(i)).getChildren();
            }
            flow.getChildren().add(new Text("\n"));
        }

    }

    private void initRoot(TextFlow flow, Elem root) {
        flow.getChildren().add(new Text(root.getString()));
        flow.getChildren().add(new Text("->"));
    }

    private List<String> listSort(List<String> list, HashMap<String,Elem> map) {
        List<String> sortedList = new ArrayList<>();
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

    public void initInfobox(TextFlow infobox) {
        writeInfobox(infobox);
    }

    public boolean writeInfobox(TextFlow infobox, TextFlow previousCNFText) {
        String change = getChange();
        writeInfobox(infobox);
        if (change == null) return false;
        while (change != null && !change.equals("/end")) {
            writeChange(change, infobox, previousCNFText);
            change = getChange();
        }
        return true;
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
        return null;
    }

    private void writeInfobox(TextFlow infobox) {
        infobox.getChildren().clear();
        setNameInfobox(tree.getName(), infobox);
    }

    private void setNameInfobox(String name, TextFlow infobox) {
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

    private void writeChange(String change, TextFlow infobox, TextFlow previousCNFText) {
        int begin = change.indexOf('(') + 1;
        int end = change.indexOf(')');
        if(change.contains("mark")) {
            change = change.substring(begin,end);
            mark(change, previousCNFText);
        }
        else if(change.contains("highlight")) {
            change = change.substring(begin,end);
            highlight(change, previousCNFText);
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
        }
        else if(change.contains("delete")) {
            change = change.substring(begin,end);
            delete(change, infobox);
        }
        else if(change.contains("replace")) {
            change = change.substring(begin,end);
            replace(change, infobox);
        }
    }

    private void mark(String change, TextFlow previousCNFText) {
        unmark(previousCNFText);
        String[] split = change.split(",");
        boolean foundRoot = false;
        if (split.length == 1) foundRoot = true;
        for (int j = 0; j < split.length; j++) {
            for (int i = 0; i < previousCNFText.getChildren().size(); i++) {
                Text text = ((Text) previousCNFText.getChildren().get(i));
                String childElement = text.getText();
                String element = split[j];
                if (childElement.equals(element) && foundRoot && !markedList.contains(text)) {
                    text.setFill(Color.RED);
                    markedList.add(((Text) previousCNFText.getChildren().get(i)));
                    break;
                }
                else if (childElement.equals(element) && !foundRoot) {
                    foundRoot = true;
                    j++;
                }
            }
        }

    }

    private void unmark(TextFlow previousCNFText) {
        for (int i = 0; i < markedList.size(); i++) {
            markedList.get(i).setFill(Color.BLACK);
        }
    }

    private void highlight(String change, TextFlow previousCNFText) {
        for (int i = 0; i < previousCNFText.getChildren().size(); i++) {
            if (( (Text) previousCNFText.getChildren().get(i)).getText().equals(change)) ( (Text) previousCNFText.getChildren().get(i)).setFill(Color.DARKGOLDENROD);
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

/*public void start() {
        tree.buildTree();
        System.out.println("===========");
        System.out.println("===========");
        System.out.println("FIRST RULE");
        System.out.println("___________");
        cnf = new CNF(tree);
        cnf.terminalRule();
        System.out.println("===========");
        System.out.println("===========");
        System.out.println("SECOND RULE");
        cnf.lengthRule();
        System.out.println("===========");
        System.out.println("===========");
        System.out.println("THIRD RULE");
        cnf.epsilonRule();
        System.out.println("===========");
        System.out.println("===========");
        System.out.println("LAST RULE");
        cnf.chainRule();

        /*cyk.setMap(tree.getMap());
        cyk.initialize("aba");
        //System.out.println(cyk.isWordInGrammar());
        int[] arr = cyk.nextStep();
        while(arr != null) {
            cyk.printArray(arr);
            arr = cyk.nextStep();
        }
        cyk.print();

    }*/
}
