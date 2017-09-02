package application.automatons;

public enum Automatons {
    GAME_OF_LIFE("Conway's Game of Life"), LANGTONS_ANT("Langton's Ant");

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
        default:
            return null;
        }
    }

    @Override
    public String toString() {
        return display;
    }
}