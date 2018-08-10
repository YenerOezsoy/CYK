import java.io.File;
import java.nio.file.Files;

/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Model {

    private CNF cnf;
    private Input input;
    private Output output;
    private Tree tree;
    private String[] nonTerminals;
    private String[] terminals;
    private String[] line;
    private String left;
    private String[] right;
    private String path;

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
        return toWrite = toWrite.substring(0, toWrite.length() - 1);
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
