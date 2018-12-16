import javafx.scene.text.TextFlow;

import java.io.File;

/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Controller {
    private Model model;
    private File file;
    private ViewControllerCNF vc;

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

    /*public void initCNFpane(TextFlow flow) {
        model.initPane(flow);
    }

    public boolean writeInfobox(boolean next, TextFlow infobox, TextFlow previousCNFText) {
        return model.writeNextInfobox(next, infobox, previousCNFText);

    }

    public void initInfobox(TextFlow infobox) {
        model.initInfobox(infobox);
    }*/

    public void initViewController(TextFlow nextPane, TextFlow previousPane,TextFlow infoBox, Tree tree) {
        vc = new ViewControllerCNF(nextPane, previousPane, infoBox, tree);
    }

    public boolean viewControllerCNFNext() {
         return vc.next();
    }

    public void viewControllerCNFPrevious() {
         vc.previous();
    }
}