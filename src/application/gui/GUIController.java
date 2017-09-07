package application.gui;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import application.Main;
import application.automatons.Automatons;
import application.automatons.CellularAutomaton;
import application.events.AutomatonEvent;
import application.i18n.LanguageMenuItem;
import application.utils.Drawings;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

//TODO Dynamic locale changes

public class GUIController {
    public static final double      DEFAULT_CANVAS_SIZE = 400.0;
    private CellularAutomaton       automaton;
    private CellularAutomatonCanvas canvas;
    private ResourceBundle          resourcesBundle;
    private Locale                  currentLocale;
    private Stage                   primaryStage;
    private FXMLLoader              loader;

    @FXML
    private Slider                  speedSlider;
    @FXML
    private BorderPane              mainBorderPane;
    @FXML
    private ComboBox<Automatons>    automatonChooser;
    @FXML
    private Button                  nextButton;
    @FXML
    private Button                  startButton;
    @FXML
    private ComboBox<Integer>       stepCountsChooser;
    @FXML
    private Spinner<Integer>        rowsSpinner;
    @FXML
    private Spinner<Integer>        colsSpinner;
    @FXML
    private Button                  changeSizeButton;
    @FXML
    private HBox                    canvasBox;
    @FXML
    private Menu                    languageMenu;
    @FXML
    private Label                   generationLabel;

    public GUIController() {
        canvas = new CellularAutomatonCanvas(DEFAULT_CANVAS_SIZE, DEFAULT_CANVAS_SIZE, null);
    }

    public void setStage(Stage s) {
        primaryStage = s;
    }

    @FXML
    public void initialize() {
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

        for (Locale l : Main.supportedLocales) {
            MenuItem item = new LanguageMenuItem(l, this);
            languageMenu.getItems().add(item);
        }

        initDynamicI18NBindings();
    }

    public void initDynamicI18NBindings() {
        // TODO create bindings for each i18n text
    }

    public void setLoader(FXMLLoader loader) {
        this.loader = loader;
    }

    public void setResourceBundle(ResourceBundle rb) {
        // System.out.println("switching to " + rb.getLocale().getDisplayLanguage());
        resourcesBundle = rb;
        loader.setResources(rb);
    }

    private void handleAutomatonStarted() {
        canvas.scheduleUpdate();
    }

    private void handleAutomatonEnded() {
        // Prevents any Timer Thread or non-FX Application thread to modify GUI directly
        // Exceptions and bugs otherwise
        Platform.runLater(() -> {
            canvas.scheduleUpdate();
            goEndState();
            alertAutomatonEnded();
        });
    }

    private void handleAutomatonPaused() {
        canvas.scheduleUpdate();
    }

    private void handleAutomatonReset() {
        canvas.scheduleUpdate();
        Platform.runLater(() -> {
            generationLabel.setText(resourcesBundle.getString("label.generation.text") + automaton.getGeneration());
        });
    }

    private void handleAutomatonStep() {
        canvas.scheduleUpdate();
        Platform.runLater(() -> {
            generationLabel.setText(resourcesBundle.getString("label.generation.text") + automaton.getGeneration());
        });
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

    public void speedChanged(int value) {
        // System.out.println("Speed -> "+value);
        if (automaton == null)
            return;
        automaton.setSpeed(value);
    }

    public void setAutomaton(CellularAutomaton newAutomaton) {
        if (automaton != null)
            automaton.removeAllEventTargets();

        automaton = newAutomaton;

        if (automaton != null) {
            automaton.addEventTarget(mainBorderPane);
            automaton.setSpeed(speedSlider.valueProperty().intValue());
        }
        canvas.setAutomaton(automaton);
    }

    public ResourceBundle getResourceBundle() {
        return resourcesBundle;
    }

    public void setLocale(Locale locale) {
        currentLocale = locale;
    }

    public Locale getLocale() {
        return currentLocale;
    }
}
