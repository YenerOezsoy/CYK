import java.io.File;
import java.nio.file.Files;

/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Controller {
    private Model model;
    private File file;

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
}
