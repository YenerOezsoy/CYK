import java.io.File;

/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Controller {
    private Model model;

    public Controller() {
        model = new Model();
    }


    public void inputFile(File file) {
        model.readFile(file.getPath());
    }

    public void outputFile(File file) {
       // output = new Output(file.getPath());

    }

    public Tree getTreeStep(int step) {
        model.getStep(step);
        return null;
    }
}
