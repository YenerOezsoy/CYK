import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class View extends Application{

    private FileChooser chooser = new FileChooser();
    private File file;

    @FXML Button chooseGrammar;
    @FXML Pane startScreen;
    @FXML Pane selectionScreen;
    @FXML Pane grammarForm;
    @FXML Pane cykAlgorithm;
    @FXML Text title;

    public View() {
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML Datei", "*.xml"));
    }


    public void startScreen() {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Read file fxml and draw interface.
            Parent root = FXMLLoader.load(getClass().getResource("startScreen.fxml"));
            primaryStage.setTitle("CYK Algorithmus");
            primaryStage.setScene(new Scene(root,1024, 768));
            primaryStage.show();


        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void fileChooser(ActionEvent event) {
        file = chooser.showOpenDialog(null);
        System.out.println(file.getAbsolutePath());
        startScreen.setVisible(false);
        selectionScreen.setVisible(true);
    }

    @FXML
    protected void newFile(ActionEvent event) {
        file = chooser.showSaveDialog(null);
        System.out.println(file.getAbsolutePath());
        startScreen.setVisible(false);
        grammarForm.setVisible(true);
    }

    @FXML
    protected void cancel(ActionEvent event) {
        grammarForm.setVisible(false);
        startScreen.setVisible(true);
    }

    @FXML
    protected void cyk() {
        selectionScreen.setVisible(false);
        title.setText("CYK Algorithmus");
        cykAlgorithm.setVisible(true);
        generateTest();
    }

    private void generateTest() {
        GridPane pane = new GridPane();
        pane.setStyle("-fx-background-color: lightgrey;");
        pane.addRow(0);
        cykAlgorithm.getChildren().addAll(pane);
        System.out.println(cykAlgorithm.getChildren().get(1));
    }



}