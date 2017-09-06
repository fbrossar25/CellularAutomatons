package application.gui;

import java.util.Arrays;
import java.util.ResourceBundle;

import application.automatons.Automatons;
import application.automatons.CellularAutomaton;
import application.events.AutomatonEvent;
import application.utils.Drawings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
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
        mainBorderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            adaptCanvasSize();
        });
        mainBorderPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            adaptCanvasSize();
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

        mainBorderPane.setCenter(canvas);
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_STARTED, (event) -> handleAutomatonStarted());
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_ENDED, (event) -> handleAutomatonEnded());
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_PAUSED, (event) -> handleAutomatonPaused());
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_RESET, (event) -> handleAutomatonReset());
        mainBorderPane.addEventHandler(AutomatonEvent.AUTOMATON_STEP, (event) -> handleAutomatonStep());

        adaptCanvasSize();
    }

    @FXML
    public void adaptCanvasSize() {
        Node bottom = mainBorderPane.getBottom();
        Node top = mainBorderPane.getTop();
        Node right = mainBorderPane.getRight();
        Node left = mainBorderPane.getLeft();
        double maxX = mainBorderPane.getLayoutBounds().getWidth() - (left != null ? left.getLayoutBounds().getWidth() : 0)
                - (right != null ? right.getLayoutBounds().getWidth() : 0);
        double maxY = mainBorderPane.getLayoutBounds().getHeight() - (top != null ? top.getLayoutBounds().getHeight() : 0)
                - (bottom != null ? bottom.getLayoutBounds().getHeight() : 0);
        double size = Math.max(maxX, maxY);
        if (maxX <= 0.0 || maxY <= 0.0) {
            canvas.setWidth(DEFAULT_CANVAS_SIZE);
            canvas.setHeight(DEFAULT_CANVAS_SIZE);
        } else {
            canvas.setWidth(size);
            canvas.setHeight(size);
        }
        canvas.draw();
    }

    private void handleAutomatonStarted() {
        canvas.scheduleUpdate();
    }

    private void handleAutomatonEnded() {
        System.out.println("Automaton ended at generation " + automaton.getGeneration());
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
        // System.out.println(automaton);
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
