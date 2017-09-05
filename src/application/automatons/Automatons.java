package application.automatons;

public enum Automatons {
    GAME_OF_LIFE("Conway's Game of Life"), LANGTONS_ANT("Langton's Ant"), ELEMENTARY(
            "Elementary Automaton " + ElementaryAutomaton.DEFAULT_RULE), ELEMENTARY_54("Elementary Automaton 54"), ELEMENTARY_126(
                    "Elementary Automaton 126"), ELEMENTARY_30("Elementary Automaton 30 (some chaos :D)");

    private final String display;

    private Automatons(String s) {
        display = s;
    }

    public CellularAutomaton getInstance() {
        switch (this) {
        case GAME_OF_LIFE:
            return new GameOfLife();
        case LANGTONS_ANT:
            return new LangtonsAnt();
        case ELEMENTARY:
            return new ElementaryAutomaton();
        case ELEMENTARY_54:
            return new ElementaryAutomaton(54);
        case ELEMENTARY_126:
            return new ElementaryAutomaton(126);
        case ELEMENTARY_30:
            return new ElementaryAutomaton(30);
        default:
            return null;
        }
    }

    @Override
    public String toString() {
        return display;
    }
}