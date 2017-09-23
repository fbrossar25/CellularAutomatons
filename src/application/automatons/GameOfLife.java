package application.automatons;

import java.util.stream.IntStream;

public class GameOfLife extends CellularAutomaton {

    @Override
    protected void init() {
        randomizeCells();
    }

    @Override
    public void nextStep() {
        IntStream.range(0, rows()).parallel().forEach(row -> {
            for (int col = 0; col < cols(); col++) {
                int neighbors = getAliveNeighbors(row, col);
                boolean populated = isCellPopulated(row, col);
                // System.out.println("Cell ("+row+","+col+") -> "+neighbors+" "+(populated ?
                // "O" : "X"));
                setNextGenCell(row, col, (neighbors == 3 || (populated && neighbors == 2)));
            }
        });
        if (nextGeneration.equals(currentGeneration))// stable state
            end();
    }

    public int getAliveNeighbors(int row, int col) {
        if (!currentGeneration.isValidCell(row, col))
            return 0;
        int neighbors = 0;
        neighbors += isCellPopulated(row - 1, col - 1) ? 1 : 0; // NW
        neighbors += isCellPopulated(row - 1, col) ? 1 : 0; // N
        neighbors += isCellPopulated(row - 1, col + 1) ? 1 : 0; // NE
        neighbors += isCellPopulated(row, col - 1) ? 1 : 0; // W
        // don't look at itself
        neighbors += isCellPopulated(row, col + 1) ? 1 : 0; // E
        neighbors += isCellPopulated(row + 1, col - 1) ? 1 : 0; // SW
        neighbors += isCellPopulated(row + 1, col) ? 1 : 0; // S
        neighbors += isCellPopulated(row + 1, col + 1) ? 1 : 0; // SE
        return neighbors;
    }

    @Override
    public Automatons getAutomatonType() {
        return Automatons.GAME_OF_LIFE;
    }

    @Override
    protected void endAutomaton() {

    }
}
