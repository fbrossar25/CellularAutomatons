package application.automatons;

import java.util.TimerTask;

public class AutomatonUpdateTask extends TimerTask {
    private CellularAutomaton automaton;

    public AutomatonUpdateTask(CellularAutomaton automaton) {
        if (automaton == null)
            throw new IllegalArgumentException("Automaton cannot be null");
        this.automaton = automaton;
    }

    @Override
    public void run() {
        if (!automaton.isPaused() && !automaton.isEnd())
            automaton.next();
    }
}