public class ViewControllerInput {

    private String path;
    private Output output;
    private Tree tree;

    private String[] nonTerminals;
    private String[] terminals;

    private String[] line;
    private String left;
    private String[] right;

    public ViewControllerInput(String path, Output output, Tree tree) {
        this.path = path;
        this.output = output;
        this.tree = tree;
    }

    public boolean userInput(String nonTerminal, String terminal, String startsymbol, String production) {
        if (checkInput(nonTerminal, terminal, startsymbol, production)) return tree.buildTree();
        else return false;
    }

    private boolean checkInput(String nonTerminal, String terminal, String startsymbol, String production) {
        nonTerminals = nonTerminal.split("\\s+");
        terminals = terminal.split("\\s+");
        if (isTerminalInNonterminal()) return false;//FEHLER
        if (!isStartsymbolNonterminal(startsymbol)) return false; //Fehler
        writeInput(startsymbol, production);
        tree = new Tree(path);
        return true;
    }

    private boolean isTerminalInNonterminal() {
        for (int i = 0; i < nonTerminals.length; i++) {
            for (int j = 0; j < terminals.length; j++) {
                if (terminals[j].equals(nonTerminals[i])) return true;
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

    public Tree getTree() {
        return tree;
    }
}
