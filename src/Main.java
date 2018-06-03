import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by yenerozsoy on 21.03.18.
 */
public class Main  {





    public static void main (String args[]) {

        Model m = new Model();
        //m.start();
        View view = new View();
        view.startScreen();
    }
}
