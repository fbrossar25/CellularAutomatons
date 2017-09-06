package application.gui;

import java.util.Arrays;
import java.util.ResourceBundle;

import application.automatons.Automatons;
import application.automatons.CellularAutomaton;
import application.events.AutomatonEvent;
import application.utils.Drawings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;

public class GUIController {
    public static final double      DEFAULT_CANVAS_SIZE = 400.0;
    private CellularAutomaton       automaton;
    private CellularAutomatonCanvas canvas;

    @FXML
    Slider                          speedSlider;
    @FXML
    ResourceBundle                  resourcesBundle;
    @FXML
    BorderPane                      mainBorderPane;
    @FXML
    ComboBox<Automatons>            automatonChooser;
    @FXML
    Button                          nextButton;
    @FXML
    Button                          startButton;
    @FXML
    ComboBox<Integer>               stepCountsChooser;
    @FXML
    Spinner<Integer>                rowsSpinner;
    @FXML
    Spinner<Integer>                colsSpinner;
    @FXML
    Button                          changeSizeButton;

    public GUIController() {
        canvas = new CellularAutomatonCanvas(DEFAULT_CANVAS_SIZE, DEFAULT_CANVAS_SIZE, null);
    }

    @FXML
    public void initialize() {
        automatonChooser.getItems().setAll(Automatons.values());
        stepCountsChooser.setItems(FXCollections.observableArrayList(Arrays.asList(1, 2, 5, 10)));
        stepCountsChooser.getSelectionModel().selectFirst();

        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            speedChanged(newValue.intValue());
        });

        mainBorderPane.setCenter(canvas);
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_STARTED, (event) -> handleAutomatonStarted());
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_ENDED, (event) -> handleAutomatonEnded());
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_PAUSED, (event) -> handleAutomatonPaused());
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_RESET, (event) -> handleAutomatonReset());
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_STEP, (event) -> handleAutomatonStep());

        adaptCanvasSize();
        // Drawings.drawBackground(canvas);
    }

    @FXML
    public void adaptCanvasSize() {
        // TODO adapt canvas size with computed one
        canvas.setWidth(DEFAULT_CANVAS_SIZE);
        canvas.setHeight(DEFAULT_CANVAS_SIZE);
    }

    private void handleAutomatonStarted() {
        // System.out.println("Automaton started !");
    }

    private void handleAutomatonEnded() {
        System.out.println("Automaton ended at generation " + automaton.getGeneration());
    }

    private void handleAutomatonPaused() {
        // System.out.println("Automaton paused");
    }

    private void handleAutomatonReset() {
        // System.out.println("Automaton reset");
    }

    private void handleAutomatonStep() {
        // System.out.println("Automaton step");
    }

    @FXML
    public void automatonChanged() {
        if (automaton != null) {
            automaton.end();
            automaton.clear();
        }
        goPauseState();
        setAutomaton(automatonChooser.getValue().getInstance());
        if (automaton.getAutomatonType() == Automatons.ELEMENTARY)
            colsSpinner.setDisable(true);
        else
            colsSpinner.setDisable(false);
        colsSpinner.increment(automaton.cols() - colsSpinner.getValue().intValue());
        rowsSpinner.increment(automaton.rows() - rowsSpinner.getValue().intValue());
    }

    @FXML
    public void stepCountsChanged() {
        automaton.setStepsByUpdate(stepCountsChooser.getValue().intValue());
    }

    @FXML
    public void nextButtonPressed() {
        if (automaton == null)
            return;
        automaton.next();
        // System.out.println(automaton);
        Drawings.drawCellularAutomaton(canvas, automaton);
    }

    @FXML
    public void changeSizeButtonPressed() {

    }

    @FXML
    public void startButtonPressed() {
        if (automaton == null)
            return;
        if (automaton.isPaused()) {
            goStartState();
        } else {
            goPauseState();
        }
    }

    public void goPauseState() {
        if (automaton != null)
            automaton.pause();
        nextButton.setDisable(false);
        startButton.setText("Start");
    }

    public void goStartState() {
        if (automaton != null)
            automaton.start();
        nextButton.setDisable(true);
        startButton.setText("Pause");
    }

    @FXML
    public void resetButtonPressed() {
        if (automaton == null)
            return;
        goPauseState();
        automaton.reset();
    }

    @FXML
    public void canvasClicked() {
        // TODO switch between alive and dead cells
    }

    @FXML
    public void handleResizeCommand() {
        // TODO resize automaton
    }

    public void speedChanged(int value) {
        // System.out.println("Speed -> "+value);
        if (automaton == null)
            return;
        automaton.setSpeed(value);
    }

    public void setAutomaton(CellularAutomaton newAutomaton) {
        if (this.automaton != null)
            this.automaton.removeAllEventTargets();

        this.automaton = newAutomaton;

        if (this.automaton != null) {
            this.automaton.addEventTarget(mainBorderPane);
            automaton.setSpeed(speedSlider.valueProperty().intValue());
        }
        canvas.setAutomaton(this.automaton);
    }
}
