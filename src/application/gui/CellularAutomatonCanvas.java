package application.gui;

import application.automatons.CellularAutomaton;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class CellularAutomatonCanvas extends Canvas {
    private CellularAutomaton automaton;
    private CanvasUpdateTimer timer;
    private boolean           autoRedraw;
    private GraphicsContext   ctx;
    public static final Color COLOR_UNPOPULATED           = Color.GRAY;
    public static final Color COLOR_POPULATED             = Color.YELLOW;
    public static final Color COLOR_BACKGROUND            = Color.BLACK;
    public static final Color COLOR_TEST                  = Color.GREENYELLOW;
    public static final Color COLOR_HIGHLIGHT_POPULATED   = Color.RED;
    public static final Color COLOR_HIGHLIGHT_UNPOPULATED = Color.GREEN;
    private boolean           updateScheduled             = true;
    private int               highlightRow                = -1, highlightCol = -1;

    public CellularAutomatonCanvas(double width, double height, CellularAutomaton automaton) {
        this(width, height, automaton, true);
    }

    public CellularAutomatonCanvas(double width, double height, CellularAutomaton automaton, boolean autoRedraw) {
        super(width, height);
        this.automaton = automaton;
        timer = new CanvasUpdateTimer(this);
        setAutoRedraw(autoRedraw);
        ctx = getGraphicsContext2D();
        setOnMouseClicked((evt) -> {
            handleMouseClicked(evt);
        });
        setOnMouseMoved((evt) -> {
            handleMouseMoved(evt);
        });
        setOnMouseExited((evt) -> {
            highlightRow = -1;
            highlightCol = -1;
            scheduleUpdate();
        });
    }

    public boolean isAutoRedraw() {
        return autoRedraw;
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
        return automaton;
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

    @Deprecated
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
        if (!updateScheduled || automaton == null)
            return;
        double cellWidth = getCellWidth();
        double cellHeight = getCellHeight();
        double width = getWidth();
        double height = getHeight();
        double lineWidth = getLineWidth();
        double lineHeight = getLineHeight();
        ctx.clearRect(0.0, 0.0, width, height);
        int W = (int) width, H = (int) height;
        WritableImage img = new WritableImage(W, H);
        PixelWriter pw = img.getPixelWriter();
        Color c;
        for (int row = 0; row < automaton.rows(); row++) {
            for (int col = 0; col < automaton.cols(); col++) {
                if (automaton.isCellPopulated(row, col)) {
                    c = (row == highlightRow && col == highlightCol) ? COLOR_HIGHLIGHT_POPULATED : COLOR_POPULATED;
                } else {
                    c = (row == highlightRow && col == highlightCol) ? COLOR_HIGHLIGHT_UNPOPULATED : COLOR_UNPOPULATED;
                }
                int xBegin = (int) (col * cellWidth), xEnd = (int) ((col + 1) * cellWidth);
                int yBegin = (int) (row * cellHeight), yEnd = (int) ((row + 1) * cellHeight);
                for (int x = xBegin; x < xEnd; x++) {
                    for (int y = yBegin; y < yEnd; y++) {
                        pw.setColor(x, y, c);
                    }
                }
            }
        }
        ctx.drawImage(img, 0.0, 0.0);
        ctx.setFill(COLOR_BACKGROUND);
        if (lineWidth > 0.0) {
            ctx.setLineWidth(getLineWidth());
            for (int i = 1; i < automaton.cols(); i++) {
                ctx.strokeLine(i * cellWidth, 0, i * cellWidth, height);
            }
        }

        if (lineHeight > 0.0) {
            ctx.setLineWidth(getLineHeight());
            for (int i = 1; i < automaton.rows(); i++) {
                ctx.strokeLine(0, i * cellHeight, width, i * cellHeight);
            }
        }

        updateScheduled = false;
    }

    @Deprecated
    public void oldDraw() {
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

    public double getLineWidth() {
        if (automaton.rows() < 100)
            return 1.0;
        else if (automaton.rows() < 250)
            return 0.5;
        else
            return 0.0;
    }

    public double getLineHeight() {
        if (automaton.cols() < 100)
            return 1.0;
        else if (automaton.cols() < 200)
            return 0.5;
        else
            return 0.0;
    }

    public int getColForX(double xPos) {
        return (int) (xPos / getCellWidth());
    }

    public int getRowForY(double yPos) {
        return (int) (yPos / getCellHeight());
    }

    public void handleMouseClicked(MouseEvent evt) {
        if (automaton == null || !automaton.isPaused() || automaton.isEnd())
            return;
        int row = getRowForY(evt.getY());
        if (row < 0 || row > automaton.rows())
            return;
        int col = getColForX(evt.getX());
        if (col < 0 || col > automaton.cols())
            return;
        automaton.setCurrentGenCell(row, col, !automaton.isCellPopulated(row, col));
        scheduleUpdate();
    }

    public void handleMouseMoved(MouseEvent evt) {
        if (automaton == null || !automaton.isPaused() || automaton.isEnd())
            return;
        int row = getRowForY(evt.getY());
        if (row < 0 || row > automaton.rows())
            return;
        int col = getColForX(evt.getX());
        if (col < 0 || col > automaton.cols())
            return;
        if (row != highlightRow || col != highlightCol) {
            highlightRow = row;
            highlightCol = col;
            scheduleUpdate();
        }
    }
}
