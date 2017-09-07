package application.automatons;

import java.util.ArrayList;
import java.util.List;

import application.events.AutomatonEvent;
import application.utils.BoolGrid;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public abstract class CellularAutomaton {
    private int                 rows, cols;
    protected BoolGrid          currentGeneration;
    protected BoolGrid          nextGeneration;
    protected int               speed         = 1;
    protected boolean           paused        = true;
    protected int               generation    = 0;
    protected boolean           end           = false;
    protected AutomatonUpdater  updater;
    public static final int     DEFAULT_SIZE  = 32;
    protected List<EventTarget> evtTargetList = new ArrayList<EventTarget>();
    protected int               stepsByUpdate = 1;

    public CellularAutomaton() {
        this(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public CellularAutomaton(int size) {
        this(size, size);
    }

    public CellularAutomaton(int rows, int cols) {
        if (rows < 1 || cols < 1)
            throw new IllegalArgumentException("Invalid dimensions : (" + rows + "," + cols + ")");
        this.rows = rows;
        this.cols = cols;
        currentGeneration = new BoolGrid(rows, cols);
        nextGeneration = new BoolGrid(rows, cols);
        updater = new AutomatonUpdater(this);
        init();
    }

    protected void fireAutomatonEvent(EventType<AutomatonEvent> type) {
        for (EventTarget target : evtTargetList)
            Event.fireEvent(target, new AutomatonEvent(this, target, type));
    }

    protected abstract void init();

    public void clear() {
        currentGeneration = new BoolGrid(rows(), cols());
        nextGeneration = new BoolGrid(rows(), cols());
        cancelUpdater();
        generation = 0;
        end = false;
        paused = true;
    }

    public void reset() {
        clear();
        init();
        fireAutomatonEvent(AutomatonEvent.AUTOMATON_RESET);
    }

    public boolean isEnd() {
        return end;
    }

    public void next() {
        if (end)
            return;
        for (int step = 0; step < stepsByUpdate; step++) {
            nextStep();
            generation++;
            currentGeneration.initWithOther(nextGeneration);
            // this.nextGeneration.reset();
            fireAutomatonEvent(AutomatonEvent.AUTOMATON_STEP);
            if (isEnd())
                break;
        }
    }

    protected abstract void nextStep();

    protected void cancelUpdater() {
        try {
            updater.cancel();
        } catch (IllegalStateException e) {
            // ignoring e
        }
    }

    public void pause() {
        if (paused)// if already paused
            return;
        paused = true;
        if (!end)
            fireAutomatonEvent(AutomatonEvent.AUTOMATON_PAUSED);
        cancelUpdater();
    }

    public void start() {
        if (!paused)// if already started
            return;
        paused = false;
        rescheduleUpdater();
        fireAutomatonEvent(AutomatonEvent.AUTOMATON_STARTED);
    }

    public boolean isPaused() {
        return paused;
    }

    public int getSpeed() {
        return speed;
    }

    public void rescheduleUpdater() {
        try {
            updater.cancel();
        } catch (IllegalStateException e) {
            // ignoring e
        }
        updater = new AutomatonUpdater(this);
    }

    public void setSpeed(int value) {
        if (value <= 0) {
            speed = 1;
        } else {
            speed = value;
            if (!paused)
                rescheduleUpdater();
        }
    }

    public int getGeneration() {
        return generation;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public boolean isCellPopulated(int row, int col) {
        boolean b;
        try {
            b = currentGeneration.get(row, col);
        } catch (IndexOutOfBoundsException e) {
            // ignoring e
            b = false;
        }
        return b;
    }

    protected void setCell(BoolGrid grid, int row, int col, boolean alive) {
        if (!grid.isValidCell(row, col))
            throw new IndexOutOfBoundsException("Cell does not exists : (" + row + "," + col + ")");
        grid.set(row, col, alive);
    }

    public void setCurrentGenCell(int row, int col, boolean alive) throws IndexOutOfBoundsException {
        setCell(currentGeneration, row, col, alive);
    }

    public void setNextGenCell(int row, int col, boolean alive) throws IndexOutOfBoundsException {
        setCell(nextGeneration, row, col, alive);
    }

    public void randomizeCells() {
        clear();
        for (int row = 0; row < rows(); row++) {
            for (int col = 0; col < cols(); col++) {
                setCurrentGenCell(row, col, Math.random() < 0.5);
            }
        }
    }

    protected abstract void endAutomaton();

    public void end() {
        end = true;
        pause();
        cancelUpdater();
        endAutomaton();
        fireAutomatonEvent(AutomatonEvent.AUTOMATON_ENDED); // FIXME fired sometimes when not desired (synchronization stuff related ?)
    }

    public void addEventTarget(EventTarget target) {
        evtTargetList.add(target);
    }

    public boolean removeEventTarget(EventTarget target) {
        return evtTargetList.remove(target);
    }

    public void removeAllEventTargets() {
        evtTargetList.clear();
    }

    public void setStepsByUpdate(int value) {
        if (value < 1 || value > 10)
            throw new IllegalArgumentException("value must be between 1 and 10");
        stepsByUpdate = value;
    }

    public int getStepsByUpdate() {
        return stepsByUpdate;
    }

    public void changeSize(int rows, int cols) {
        if (rows < 1 || cols < 1)
            throw new IllegalArgumentException("rows and cols must be >= 1");
        this.rows = rows;
        this.cols = cols;
        reset();
    }

    public abstract Automatons getAutomatonType();

    @Override
    public String toString() {
        String s = "============= GENERATION " + generation + " =============";
        return end ? "Automaton ended at generation " + generation : s + "\n" + currentGeneration.toString();
    }
}
