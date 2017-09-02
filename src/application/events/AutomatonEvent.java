package application.events;

import application.automatons.CellularAutomaton;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class AutomatonEvent extends Event {
    private static final long                     serialVersionUID  = 20170902L;
    public static final EventType<AutomatonEvent> AUTOMATON_ENDED   = new EventType<>(Event.ANY, "AUTOMATON_ENDED");
    public static final EventType<AutomatonEvent> AUTOMATON_STARTED = new EventType<>(Event.ANY, "AUTOMATON_STARTED");
    public static final EventType<AutomatonEvent> AUTOMATON_PAUSED  = new EventType<>(Event.ANY, "AUTOMATON_PAUSED");
    public static final EventType<AutomatonEvent> AUTOMATON_STEP    = new EventType<>(Event.ANY, "AUTOMATON_STEP");
    public static final EventType<AutomatonEvent> AUTOMATON_RESET   = new EventType<>(Event.ANY, "AUTOMATON_RESET");

    public AutomatonEvent(CellularAutomaton automaton, EventTarget target, EventType<AutomatonEvent> eventType) {
        super(automaton, target, eventType);
    }

    @Override
    public AutomatonEvent copyFor(Object newSource, EventTarget newTarget) {
        return (AutomatonEvent) super.copyFor(newSource, newTarget);
    }
}
