import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class View extends Application{

    private FileChooser chooser = new FileChooser();
    private File file;
    private int base;
    private int rowSize;
    private Image playImage;// = new Image(getClass().getResourceAsStream("play.svg"));
    private Image pauseImage;// = new Image(getClass().getResourceAsStream("pause.svg"));
    private Image nextImage;// = new Image(getClass().getResourceAsStream("next.svg"));
    private Image previousImage;
    private boolean startButton = false;
    private Timer timer;
    private Controller controller;
    private int step = 1;
    private Tree tree;
    private boolean stopNext = false;


    private GridPane pane;

    @FXML Button chooseGrammar;
    @FXML Button cykConfirm;
    @FXML Button cykNextButton;
    @FXML Button cykPlayButton;
    @FXML Button cnfStepBack;
    @FXML Button cnfPlayButton;
    @FXML Button cnfStepForward;

    @FXML Pane startScreen;
    @FXML Pane selectionScreen;
    @FXML Pane grammarForm;
    @FXML Pane cykInput;
    @FXML Pane cykVisual;
    @FXML Pane cykAlgorithm;
    @FXML Pane errorPane;
    @FXML Pane cnfPane;
    @FXML Pane previousCNF;
    @FXML Pane actualCNF;
    @FXML Pane cnfInfoboxPane;
    @FXML TextFlow previousCNFText;
    @FXML TextFlow actualCNFText;
    @FXML TextFlow infoboxCNFText;
    @FXML TextArea production;
    @FXML Text title;
    @FXML Text errorText;
    @FXML TextField cykInputText;
    @FXML TextField nonTerminals;
    @FXML TextField terminals;
    @FXML TextField root;
    @FXML Slider cykSlider;
    @FXML Slider cnfSlider;

    public View() {
        controller = new Controller();
        playImage = new Image(getClass().getResourceAsStream("play.png"));
        pauseImage = new Image(getClass().getResourceAsStream("pause.png"));
        previousImage = new Image(getClass().getResourceAsStream("previous.png"));
        nextImage = new Image(getClass().getResourceAsStream("next.png"));
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
    protected void fileChooser() {
        file = chooser.showOpenDialog(null);
        startScreen.setVisible(false);
        selectionScreen.setVisible(true);
        controller.inputFile(file);
    }

    @FXML
    protected void newFile() {
        file = chooser.showSaveDialog(null);
        startScreen.setVisible(false);
        grammarForm.setVisible(true);
        controller.outputFile(file);
    }

    @FXML
    protected void cancel() {
        grammarForm.setVisible(false);
        startScreen.setVisible(true);
    }

    @FXML
    protected void userInput() {
        grammarForm.setVisible(false);
        selectionScreen.setVisible(true);
        controller.writeFile(nonTerminals.getText(), terminals.getText(), root.getText(), production.getText());
    }

    @FXML
    protected void cnfStart() {
        selectionScreen.setVisible(false);
        title.setText("Chomsky Normalform");
        cnfPane.setVisible(true);
        initButton();
        initcnfPane();

    }

    @FXML
    protected void cykInput() {
        selectionScreen.setVisible(false);
        title.setText("CYK Algorithmus");
        cykAlgorithm.setVisible(true);
        cykInput.setVisible(true);
    }

    @FXML
    protected void cykConfirm() {
        if (checkWordInput(cykInputText.getText())) {
            cykInput.setVisible(false);
            cykVisual.setVisible(true);
            base = cykInputText.getText().length();
            rowSize = base + 1;
            generateGrid();
            initButton();
        }
    }

    @FXML
    protected void errorConfirm() {
        errorPane.setVisible(false);
    }

    @FXML
    protected void play() {
        Button activeButton = getActivePlayButton();
        if (startButton) {
            cykPlayButton.setGraphic(new ImageView(playImage));
            startButton = false;
            stopTimer();
        }
        else {
            cykPlayButton.setGraphic(new ImageView(pauseImage));
            startButton = true;
            startTimer();
        }

    }

    private Button getActivePlayButton() {
        if(cnfPane.isVisible()) return cnfPlayButton;
        return cykPlayButton;
    }

    @FXML
    protected void cykNext() {
        System.out.println("next");
        cykNextStep();
    }

    @FXML
    protected void cnfPrevious() {
        System.out.println("previous");
        //stepChange(false);
        controller.viewControllerCNFPrevious();
    }

    @FXML
    protected void cnfNext() {
        System.out.println("next");
        //if(!stopNext) stepChange(true);
        controller.viewControllerCNFNext();
    }

    /*private void stepChange(boolean next) {
        if (!controller.writeInfobox(next, infoboxCNFText, previousCNFText)) {
            controller.initCNFpane(previousCNFText);
            if(next) tree = controller.getTreeStep(++step);
            else tree = controller.getTreeStep(--step);
            if (tree == null) {
                stopNext = true;
                if (next) tree = controller.getTreeStep(--step);
                else tree = controller.getTreeStep(++step);
            }
            else {
                controller.initCNFpane(actualCNFText);
                controller.writeInfobox(next, infoboxCNFText, previousCNFText);
            }
        }
    }*/

    private void initcnfPane() {
        /*tree = controller.getTreeStep(++step);
        controller.initCNFpane(previousCNFText);
        tree = controller.getTreeStep(++step);
        controller.initCNFpane(actualCNFText);
        controller.initInfobox(infoboxCNFText);
        tree = controller.getTreeStep(step);*/
        tree = controller.getTreeStep(step);
        controller.initViewController(actualCNFText, previousCNFText, infoboxCNFText, tree);
    }

    private void initButton() {
        cykPlayButton.setGraphic(new ImageView(playImage));
        cykNextButton.setGraphic(new ImageView(nextImage));
        cykPlayButton.toFront();
        cykNextButton.toFront();
        cykSlider.toFront();
        cnfStepBack.setGraphic(new ImageView(previousImage));
        cnfPlayButton.setGraphic(new ImageView(playImage));
        cnfStepForward.setGraphic(new ImageView(nextImage));
    }

    @FXML
    protected void sliderEvent() {
        if (startButton) {
            stopTimer();
            startTimer();
        }
    }

    private void startTimer() {
        Slider activeSlider = getActiveSlider();
        int period = (int) (activeSlider.getValue() * 1000);
        System.out.println(period);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("I would be called every " + period + " milliseconds");
                if(cnfPane.isVisible()) cnfNextStep();
                else cykNextStep();
            }
        }, 0, period);

    }

    private Slider getActiveSlider() {
        if (cnfPane.isVisible()) return cnfSlider;
        return cykSlider;
    }

    private void stopTimer() {
        timer.cancel();
        System.out.println("CANCELLED");
    }
    private void cykNextStep() {
       /* int[] arr = cyk.nextStep();
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }*/
    }

    private boolean checkWordInput(String word) {
        if (word.length() == 0) {
            errorPane.setVisible(true);
            errorText.setText("Bitte gültiges Wort eingeben");
            return false;
        }
        generateGrid();
        return true;
    }

    private void generateGrid() {
        pane = new GridPane();
        makeGrid(pane, cykInputText.getText());
        createSteps();
        cykVisual.getChildren().addAll(pane);
    }

    private void makeGrid(GridPane pane, String word) {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < base ; j++) {
                Label label = new Label();
                pane.add(label,j,i);
                label.setMinSize(845 / base, 660 /(base + 1));
                label.setStyle("-fx-alignment: center; -fx-font-size: 20;");
            }
        }
    }

    private void createSteps() {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < base; j++) {
                if (i == 0) initfirstRow(i, j);
                else {
                    int count = base - (i - 1);
                    if (j < count) colorGrey(i, j);
                }
            }
        }
    }

    private void initfirstRow(int i, int j) {
        Label label = (Label) pane.getChildren().get(i * base + j);
        label.setText(String.valueOf(cykInputText.getText().charAt(j)));
    }

    private void colorGrey(int i, int j) {
        Label label = (Label) pane.getChildren().get(i * base + j);
        //label.setStyle("-fx-background-color: lightgrey;");
        label.setStyle("-fx-border-color: black;");
    }

    private void cnfNextStep() {

    }

}