package application.gui;

import application.automatons.CellularAutomaton;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class CellularAutomatonCanvas extends Canvas {
    private CellularAutomaton automaton;
    private CanvasUpdateTimer timer;
    private boolean           autoRedraw;
    private GraphicsContext   ctx;
    public static final Color COLOR_UNPOPULATED = Color.GRAY;
    public static final Color COLOR_POPULATED   = Color.YELLOW;
    public static final Color COLOR_BACKGROUND  = Color.BLACK;
    public static final Color COLOR_TEST        = Color.GREENYELLOW;
    private boolean           updateScheduled   = true;

    public CellularAutomatonCanvas(double width, double height, CellularAutomaton automaton) {
        this(width, height, automaton, true);
    }

    public CellularAutomatonCanvas(double width, double height, CellularAutomaton automaton, boolean autoRedraw) {
        super(width, height);
        this.automaton = automaton;
        timer = new CanvasUpdateTimer(this);
        setAutoRedraw(autoRedraw);
        ctx = getGraphicsContext2D();
    }

    public boolean isAutoRedraw() {
        return this.autoRedraw;
    }

    public void setAutoRedraw(boolean autoRedraw) {
        this.autoRedraw = autoRedraw;
        if (autoRedraw) {
            timer.start();
        } else {
            timer.stop();
        }
    }

    public void setAutomaton(CellularAutomaton automaton) {
        this.automaton = automaton;
        scheduleUpdate();
    }

    public CellularAutomaton getAutomaton() {
        return this.automaton;
    }

    public void scheduleUpdate() {
        updateScheduled = true;
    }

    public boolean isUpdateScheduled() {
        return updateScheduled;
    }

    private void drawBackground() {
        double width = getWidth();
        double height = getHeight();
        Paint previousPaint = ctx.getFill();
        ctx.setFill(COLOR_BACKGROUND);
        ctx.clearRect(0, 0, width, height);
        ctx.setFill(previousPaint);
    }

    private void drawAutomaton() {
        if (automaton == null)
            return;
        double x, y;
        double cw = getCellWidth();
        double ch = getCellHeight();
        int rows = automaton.rows();
        int cols = automaton.cols();
        drawBackground();
        for (int row = 0; row < rows; row++) {
            y = row * ch;
            for (int col = 0; col < cols; col++) {
                x = col * cw;
                ctx.setFill(automaton.isCellPopulated(row, col) ? COLOR_POPULATED : COLOR_UNPOPULATED);
                ctx.fillRect(x, y, cw, ch);
            }
        }
    }

    public void draw() {
        if (!updateScheduled)
            return;
        drawBackground();
        if (automaton != null)
            drawAutomaton();
        updateScheduled = false;
    }

    public double getCellWidth() {
        return getWidth() / automaton.cols();
    }

    public double getCellHeight() {
        return getHeight() / automaton.rows();
    }

    public void drawEmptyGrid() {
        double w = getWidth();
        double h = getHeight();
        ctx.setFill(COLOR_BACKGROUND);
        ctx.fillRect(0, 0, w, h);
    }

    public void test() {
        ctx.setFill(COLOR_TEST);
        ctx.fillRect(0, 0, getWidth(), getHeight());
    }
}
