package application.automatons;

import java.util.Timer;

public class AutomatonUpdater extends Timer {

    public AutomatonUpdater(CellularAutomaton automaton) {
        super();
        if (automaton == null)
            throw new IllegalArgumentException("automaton cannot be null");
        scheduleAtFixedRate(new AutomatonUpdateTask(automaton), 0, 1000 / automaton.getSpeed());
    }
}
