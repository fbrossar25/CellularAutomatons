package application.utils;

import javafx.scene.paint.Color;

import java.util.stream.IntStream;

import application.automatons.CellularAutomaton;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Drawings {
    public static final Color COLOR_UNPOPULATED = Color.GRAY;
    public static final Color COLOR_POPULATED   = Color.YELLOW;
    public static final Color COLOR_BACKGROUND  = Color.BLACK;

    public static void drawCellularAutomaton(Canvas canvas, CellularAutomaton automaton) {
        if (canvas == null || automaton == null)
            return;
        double cw = getCellWidth(canvas, automaton.cols());
        double ch = getCellHeight(canvas, automaton.rows());
        int rows = automaton.rows();
        int cols = automaton.cols();
        drawBackground(canvas);
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        IntStream.range(0, rows).parallel().forEach(row -> {
        	double y = row * ch;
            for (int col = 0; col < cols; col++) {
                double x = col * cw;
                ctx.setFill(automaton.isCellPopulated(row, col) ? COLOR_POPULATED : COLOR_UNPOPULATED);
                ctx.fillRect(x, y, cw, ch);
            }
        });
    }

    private static double getCellWidth(Canvas canvas, int cols) {
        return canvas.getWidth() / cols;
    }

    private static double getCellHeight(Canvas canvas, int rows) {
        return canvas.getHeight() / rows;
    }

    public static void drawEmptyGrid(Canvas canvas, int rows, int cols) {
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        ctx.setFill(COLOR_BACKGROUND);
        ctx.fillRect(0, 0, w, h);

    }

    public static void drawTest(Canvas canvas) {
        if (canvas == null) {
            System.err.println("@drawTest -> cannot draw on null canvas");
            return;
        }
        canvas.getGraphicsContext2D().fillText("It works !", 0.0, canvas.getHeight() / 2);
    }

    public static void drawBackground(Canvas canvas) {
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        ctx.setFill(COLOR_BACKGROUND);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
