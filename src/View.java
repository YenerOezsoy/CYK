import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class View extends Application{

    private FileChooser chooser = new FileChooser();
    private File file;
    private int base;
    private int rowSize;
    private Image playImage;// = new Image(getClass().getResourceAsStream("play.svg"));
    private Image pauseImage;// = new Image(getClass().getResourceAsStream("pause.svg"));
    private Image nextImage;// = new Image(getClass().getResourceAsStream("next.svg"));
    private Image previousImage;
    private Image backArrow;
    private Image forwardArrow;
    private boolean startButton = false;
    private Controller controller;
    private int step = 1;
    private Tree tree;
    private int activePaneIndex = -1;
    private boolean loop;
    private ScheduledExecutorService service;

    private Pane activePane;

    private GridPane pane;

    @FXML Button chooseGrammar;
    @FXML Button cykConfirm;
    @FXML Button cykNextButton;
    @FXML Button cykPreviousButton;
    @FXML Button cykPlayButton;
    @FXML Button cnfStepBack;
    @FXML Button cnfPlayButton;
    @FXML Button cnfStepForward;
    @FXML Button cykBackButton;
    @FXML Button cnfForwardButton;
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
    @FXML Pane cykGrid;
    @FXML TextFlow previousCNFText;
    @FXML TextFlow actualCNFText;
    @FXML TextFlow infoboxCNFText;
    @FXML TextArea production;
    @FXML Text title;
    @FXML Text errorText;
    @FXML Text cykSliderInfo;
    @FXML TextField cykInputText;
    @FXML TextField nonTerminals;
    @FXML TextField terminals;
    @FXML TextField root;
    @FXML Slider cykSlider;
    @FXML Slider cnfSlider;
    @FXML MenuItem fileChooseMenuItem;
    @FXML MenuItem algorithmChooseMenuItem;

    public View() {
        controller = new Controller();
        playImage = new Image(getClass().getResourceAsStream("play.png"));
        pauseImage = new Image(getClass().getResourceAsStream("pause.png"));
        previousImage = new Image(getClass().getResourceAsStream("previous.png"));
        nextImage = new Image(getClass().getResourceAsStream("next.png"));
        backArrow = new Image(getClass().getResourceAsStream("back.png"));
        forwardArrow = new Image(getClass().getResourceAsStream("forward.png"));
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
    protected void menuItemChooseFile() {
       changePane(startScreen);
    }

    @FXML
    protected void menuItemChooseAlgorithm() {
        changePane(selectionScreen);
    }

    private void changePane(Pane pane) {
        if (activePane != null) activePane.setVisible(false);
        activePane = pane;
        pane.setVisible(true);
        activePaneIndex = -1;
    }

    @FXML
    protected void fileChooser() {
        file = chooser.showOpenDialog(null);
        if (file != null) {
            startScreen.setVisible(false);
            selectionScreen.setVisible(true);
            activePane = selectionScreen;
            controller.inputFile(file);
            algorithmChooseMenuItem.setDisable(false);
        }
    }

    @FXML
    protected void newFile() {
        file = chooser.showSaveDialog(null);
        if (file != null) {
            startScreen.setVisible(false);
            grammarForm.setVisible(true);
            activePane = grammarForm;
            controller.outputFile(file);
        }
    }

    @FXML
    protected void cancel() {
        grammarForm.setVisible(false);
        startScreen.setVisible(true);
        activePane = startScreen;
    }

    @FXML
    protected void userInput() {
        grammarForm.setVisible(false);
        selectionScreen.setVisible(true);
        activePane = selectionScreen;
        controller.writeFile(nonTerminals.getText(), terminals.getText(), root.getText(), production.getText());
        algorithmChooseMenuItem.setDisable(false);
    }


    @FXML
    protected void cnfStart() {
        activePaneIndex = 1;
        controller.inputFile(file);
        selectionScreen.setVisible(false);
        title.setText("Chomsky Normalform");
        cnfPane.setVisible(true);
        activePane = cnfPane;
        initButton();
        initcnfPane();
    }

    @FXML
    protected void cykInput() {
        initButton();
        selectionScreen.setVisible(false);
        title.setText("CYK Algorithmus");
        cykAlgorithm.setVisible(true);
        cykInput.setVisible(true);
        activePane = cykInput;
        cykVisual.setVisible(false);
        cykGrid.setVisible(false);
        cykIteratButtonVisibility(false);
    }

    @FXML
    protected void cykConfirm() {
        if (checkWordInput(cykInputText.getText())) {
            activePaneIndex = 2;
            cykInput.setVisible(false);
            cykVisual.setVisible(true);
            cykGrid.setVisible(true);
            activePane = cykVisual;
            base = cykInputText.getText().length();
            rowSize = base + 1;
            generateGrid();
            controller.initViewControllerCYK(cykInputText.getText(), file, pane);
            cykIteratButtonVisibility(true);
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
            activeButton.setGraphic(new ImageView(playImage));
            startButton = false;
            stopTimer();
        }
        else {
            activeButton.setGraphic(new ImageView(pauseImage));
            startButton = true;
            startTimer();
        }
    }

    private Button getActivePlayButton() {
        if(cnfPane.isVisible()) return cnfPlayButton;
        return cykPlayButton;
    }

    @FXML
    protected boolean cykNext() {
        System.out.println("next");
        return controller.viewControllerCYKNext();
    }

    @FXML
    protected void cykPrevious() {
        System.out.println("previous");
        controller.viewControllerCYKPrevious();
        cykNextButton.setDisable(false);
    }

    @FXML
    protected void cnfPrevious() {
        System.out.println("previous");
        controller.viewControllerCNFPrevious();
        cnfStepForward.setDisable(false);
    }

    @FXML
    protected boolean cnfNext() {
        System.out.println("next");
        boolean value = controller.viewControllerCNFNext();
        if(!value) cnfStepForward.setDisable(true);
        return value;
    }

    private void initcnfPane() {
        tree = controller.getTreeStep(step);
        controller.initViewController(actualCNFText, previousCNFText, infoboxCNFText, tree);
    }

    private void initButton() {
        cykPlayButton.setGraphic(new ImageView(playImage));
        cykNextButton.setGraphic(new ImageView(nextImage));
        cykPreviousButton.setGraphic(new ImageView(previousImage));
        cykBackButton.setGraphic(new ImageView(backArrow));
        cykPreviousButton.toFront();
        cykPlayButton.toFront();
        cykNextButton.toFront();
        cykSlider.toFront();
        cnfStepBack.setGraphic(new ImageView(previousImage));
        cnfPlayButton.setGraphic(new ImageView(playImage));
        cnfStepForward.setGraphic(new ImageView(nextImage));
        cnfForwardButton.setGraphic(new ImageView(forwardArrow));
        cnfForwardButton.setContentDisplay(ContentDisplay.RIGHT);
    }

    @FXML
    protected void sliderEvent() {
        if (startButton) {
            stopTimer();
            startTimer();
            getActivePlayButton().setGraphic(new ImageView(playImage));
        }
    }

    @FXML
    protected void cnfToCYK() {
        stopTimer();
        cykInput();
        cnfPane.setVisible(false);
        cykAlgorithm.setVisible(true);
        cykInput.setVisible(true);
    }

    @FXML
    protected void cykToCNF() {
        stopTimer();
        cykAlgorithm.setVisible(false);
        cykVisual.setVisible(false);
        cykInput.setVisible(false);
        cykGrid.setVisible(false);
        cnfPane.setVisible(true);
        cnfStart();
    }

    private void startTimer() {
        Slider activeSlider = getActiveSlider();
        int period = (int) activeSlider.getValue();

        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Timer");
                            doNext();
                        }
                    });
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

            }
        };

        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, period, TimeUnit.SECONDS);
    }

    private void stopTimer() {
        System.out.println("Stop");
        service.shutdown();
        getActivePlayButton().setGraphic(new ImageView(playImage));
    }

    private void doNext() {
        if (activePaneIndex == 1) loop = cnfNext();
        else if (activePaneIndex == 2) loop = cykNext();
        checkStatusOfTimer();
    }

    private void checkStatusOfTimer() {
        if (!loop) {
            service.shutdown();
            getActivePlayButton().setGraphic(new ImageView(playImage));
            if (activePaneIndex == 1) cnfStepForward.setDisable(true);
            else if (activePaneIndex == 2) cykNextButton.setDisable(true);
        }

    }

    private Slider getActiveSlider() {
        if (cnfPane.isVisible()) return cnfSlider;
        return cykSlider;
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
        makeGrid(pane);
        createSteps();
        cykGrid.getChildren().clear();
        cykGrid.getChildren().addAll(pane);
    }

    private void makeGrid(GridPane pane) {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < base ; j++) {
                Label label = new Label();
                pane.add(label,j,i);
                label.setMinSize(845 / base, 598 /(base + 1));
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
        label.setStyle("-fx-border-color: black;");
    }

    private void cykIteratButtonVisibility(boolean visibility) {
        cykNextButton.setVisible(visibility);
        cykPreviousButton.setVisible(visibility);
        cykSlider.setVisible(visibility);
        cykSliderInfo.setVisible(visibility);
    }
}