package application.gui;

import java.util.Arrays;
import java.util.ResourceBundle;

import application.automatons.Automatons;
import application.automatons.CellularAutomaton;
import application.events.AutomatonEvent;
import application.utils.Drawings;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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
    @FXML
    HBox                            canvasBox;
    Stage                           primaryStage;

    public GUIController() {
        canvas = new CellularAutomatonCanvas(DEFAULT_CANVAS_SIZE, DEFAULT_CANVAS_SIZE, null);
    }

    public void setStage(Stage s) {
        primaryStage = s;
    }

    @FXML
    public void initialize() {
        canvas.setOnMouseClicked((event) -> {
            canvasClicked(event);
        });

        automatonChooser.getItems().setAll(Automatons.values());
        stepCountsChooser.setItems(FXCollections.observableArrayList(Arrays.asList(1, 2, 5, 10)));
        stepCountsChooser.getSelectionModel().selectFirst();

        rowsSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                rowsSpinner.increment(0); // force committing value change on focus loss
            }
        });

        colsSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                colsSpinner.increment(0); // force committing value change on focus loss
            }
        });

        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            speedChanged(newValue.intValue());
        });

        canvasBox.getChildren().add(canvas);
        canvasBox.setMinHeight(DEFAULT_CANVAS_SIZE);
        canvasBox.setMinWidth(DEFAULT_CANVAS_SIZE);
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_STARTED, (event) -> handleAutomatonStarted());
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_ENDED, (event) -> handleAutomatonEnded());
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_PAUSED, (event) -> handleAutomatonPaused());
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_RESET, (event) -> handleAutomatonReset());
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_STEP, (event) -> handleAutomatonStep());
    }

    private void handleAutomatonStarted() {
        canvas.scheduleUpdate();
    }

    private void handleAutomatonEnded() {
        // Prevents any Timer Thread or non-FX Application thread to modify GUI directly
        // Exceptions and bugs otherwise
        Platform.runLater(() -> {
            canvas.scheduleUpdate();
            System.out.println("Automaton ended at generation " + automaton.getGeneration());
            goEndState();
            alertAutomatonEnded();
        });
    }

    private void handleAutomatonPaused() {
        canvas.scheduleUpdate();
    }

    private void handleAutomatonReset() {
        canvas.scheduleUpdate();
    }

    private void handleAutomatonStep() {
        canvas.scheduleUpdate();
    }

    private void alertAutomatonEnded() {
        Alert a = new Alert(AlertType.INFORMATION, "Automaton ended successfully at generation " + automaton.getGeneration());
        a.setHeaderText("Automaton as ended");
        a.setTitle("Information");
        a.showAndWait();
    }

    @FXML
    public void automatonChanged() {
        if (automaton != null) {
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
        canvas.scheduleUpdate();
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
        Drawings.drawCellularAutomaton(canvas, automaton);
    }

    @FXML
    public void changeSizeButtonPressed() {
        if (automaton == null)
            return;
        goPauseState();
        automaton.changeSize(rowsSpinner.getValue().intValue(), colsSpinner.getValue().intValue());
        canvas.scheduleUpdate();
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
        canvas.scheduleUpdate();
    }

    public void goPauseState() {
        if (automaton != null)
            automaton.pause();
        nextButton.setDisable(false);
        startButton.setText("Start");
        startButton.setDisable(false);
    }

    public void goStartState() {
        if (automaton != null)
            automaton.start();
        nextButton.setDisable(true);
        startButton.setText("Pause");
        startButton.setDisable(false);
    }

    public void goEndState() {
        if (automaton != null)
            automaton.start();
        nextButton.setDisable(true);
        startButton.setText("Start");
        startButton.setDisable(true);
    }

    @FXML
    public void resetButtonPressed() {
        if (automaton == null)
            return;
        goPauseState();
        automaton.reset();
        canvas.scheduleUpdate();
    }

    public void canvasClicked(MouseEvent evt) {
        // TODO switch between alive and dead cells
        System.out.println("Canvas clicked at (" + evt.getX() + "," + evt.getY() + ")");
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
