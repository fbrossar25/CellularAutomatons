package application.gui;

import java.util.ResourceBundle;
import application.automatons.Automatons;
import application.automatons.CellularAutomaton;
import application.helpers.Drawings;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;

public class GUIController {
	private CellularAutomaton automaton;
	@FXML
	Slider speedSlider;
	@FXML
	ResourceBundle resourcesBundle;
	@FXML
	Canvas canvas;
	@FXML
	ComboBox<Automatons> automatonChooser;
	
	
	
	public GUIController() {
		
	}
	
	@FXML
	public void initialize() {
		automatonChooser.getItems().setAll(Automatons.values());
		
		speedSlider.valueProperty().addListener((observable,oldValue,newValue)->{
			speedChanged(newValue.intValue());
		});
		Drawings.drawBackground(canvas);
	}
	
	@FXML
	public void automatonChanged() {
		//System.out.println("Automaton changed -> "+automatonChooser.getValue());
		setAutomaton(automatonChooser.getValue().getInstance());
		if(automaton == null)
			return;
		
		System.out.println(automaton);
		Drawings.drawCellularAutomaton(canvas, automaton);
	}
	
	@FXML
	public void nextButtonPressed() {
		if(automaton == null)
			return;
		automaton.next();
		System.out.println("============= GENERATION "+automaton.getGeneration()+" =============");
		System.out.println(automaton);
		Drawings.drawCellularAutomaton(canvas, automaton);
	}
	
	@FXML
	public void startButtonPressed() {
		if(automaton == null)
			return;
		if(automaton.isPaused())
			automaton.start();
		else
			automaton.pause();
	}
	
	@FXML
	public void resetButtonPressed() {
		if(automaton == null)
			return;
		automaton.clear();
	}
	
	@FXML
	public void canvasClicked() {
		
	}
	
	public void speedChanged(int value) {
		//System.out.println("Speed -> "+value);
		if(automaton == null)
			return;
		automaton.setSpeed(value);
	}
	
	public void setAutomaton(CellularAutomaton automaton) {
		this.automaton = automaton;
	}
}
