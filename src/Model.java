import javafx.scene.text.TextFlow;

/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Model {

    private CNF cnf;
    private Output output;
    private Tree tree;
    private String path;
    private ViewControllerInput vcInput;

    public void readFile(String path) {
        this.path = path;
        //vc.setPath(path);
        tree = new Tree(path);
       // vc.setTree(tree);
    }

    public void writeFile(String path) {
        System.out.println(path);
        this.path = path;
        output = new Output(path);
        //vc.setOutput(output);
        //vc.setPath(path);
        output.newFile();
    }

    public boolean writeIntoFile(String nonTerminal, String terminal, String startsymbol, String production) {
        vcInput = new ViewControllerInput(path, output, tree);
        if (!userInput(nonTerminal, terminal, startsymbol, production)) {
            return false;
        }
        output.addToFile();
        cnf = new CNF(tree, output);
        cnf.startCNF();
        tree = new Tree(path);
        //vc.setTree(tree);
        return true;
    }

    public boolean userInput(String nonTerminal, String terminal, String startsymbol, String production) {
        return vcInput.userInput(nonTerminal, terminal, startsymbol, production);
    }



    public Tree getStep(int step) {
        tree.setActiveStep(step);
        return tree;
    }
/*
    public void initPane(TextFlow flow) {
        vc.initPane(flow);
    }

    public void initInfobox(TextFlow infobox) {
        vc.initInfobox(infobox);
    }

    public boolean writeNextInfobox(boolean next, TextFlow infobox, TextFlow previousCNFText) {
        return vc.writeNextInfobox(next, infobox, previousCNFText);
    }*/

}
