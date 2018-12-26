import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;

import java.io.File;

/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Controller {
    private Model model;
    private File file;
    private ViewControllerCNF viewControllerCNF;
    ViewControllerCYK viewControllerCYK;

    public Controller() {
        model = new Model();
    }


    public void inputFile(File file) {
        this.file = file;
        model.readFile(file.getPath());
    }

    public void outputFile(File file) {
        this.file = file;
        model.writeFile(file.getPath());
    }

    public void writeFile(String nonterminal, String terminal, String startsymbol, String production) {
       // output = new Output(file.getPath());
        if(!model.writeIntoFile(nonterminal, terminal, startsymbol, production)) file.delete();
    }

    public Tree getTreeStep(int step) {
        return model.getStep(step);
    }

    public void initViewControllerCYK(String word, Tree tree, File file, Pane pane) {
        this.file = file;
        model.readFile(file.getPath());
        viewControllerCYK = new ViewControllerCYK(model.getStep(5), word, pane);
    }

    public void initViewController(TextFlow nextPane, TextFlow previousPane,TextFlow infoBox, Tree tree) {
        viewControllerCNF = new ViewControllerCNF(nextPane, previousPane, infoBox, tree);
    }

    public boolean viewControllerCNFNext() {
         return viewControllerCNF.next();
    }

    public void viewControllerCNFPrevious() {
         viewControllerCNF.previous();
    }

    public boolean viewControllerCYKNext() {
        return viewControllerCYK.next();
    }

    public void viewControllerCYKPrevious() {
        viewControllerCYK.previous();
    }
}