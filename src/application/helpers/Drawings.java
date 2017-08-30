package application.helpers;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import application.automatons.CellularAutomaton;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Drawings {
	public static final Color COLOR_UNPOPULATED = Color.GRAY;
	public static final Color COLOR_POPULATED = Color.YELLOW;
	public static final Color COLOR_BACKGROUND = Color.BLACK;
	
	public static void drawCellularAutomaton(Canvas canvas, CellularAutomaton automaton) {
		if(canvas == null || automaton == null)
			return;
		int cellWidth = getCellWidth(canvas,automaton);
		int cellHeight= getCellHeight(canvas,automaton);
		//TODO draw cell grid
	}
	
	private static int getCellWidth(Canvas canvas, CellularAutomaton automaton) {
		//TODO compute cell width
		return -1;
	}
	
	private static int getCellHeight(Canvas canvas, CellularAutomaton automaton) {
		//TODO compute cell height
		return -1;
	}
	
	public static void drawTest(Canvas canvas) {
		if(canvas == null) {
			System.err.println("@drawTest -> cannot draw on null canvas");
			return;
		}
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.1, true,
	               CycleMethod.REFLECT,
	               new Stop(0.0, Color.BLACK),
	               new Stop(1.0, Color.WHITE)));
	    gc.fill();
	}
}
