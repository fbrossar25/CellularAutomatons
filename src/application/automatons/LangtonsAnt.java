package application.automatons;

public class LangtonsAnt extends CellularAutomaton {
    private int       antRow, antCol;
    private Direction antDir;

    public static enum Direction {
        N(-1, 0, "N"), E(0, 1, "E"), S(1, 0, "S"), W(0, -1, "W");

        public final int    rowDir, colDir;
        public final String display;

        private Direction(int rowDir, int colDir, String s) {
            this.rowDir = rowDir;
            this.colDir = colDir;
            this.display = s;
        }

        @Override
        public String toString() {
            return display;
        }
    };

    @Override
    protected void init() {
        antRow = rows() / 2;
        antCol = cols() / 2;
        antDir = Direction.N;
    }

    private void rotateRight() {
        switch (antDir) {
        case N:
            antDir = Direction.E;
            break;
        case E:
            antDir = Direction.S;
            break;
        case S:
            antDir = Direction.W;
            break;
        case W:
            antDir = Direction.N;
            break;
        default:
        }
    }

    private void rotateLeft() {
        switch (antDir) {
        case N:
            antDir = Direction.W;
            break;
        case E:
            antDir = Direction.N;
            break;
        case S:
            antDir = Direction.E;
            break;
        case W:
            antDir = Direction.S;
            break;
        default:
        }
    }

    @Override
    protected void nextStep() {
        boolean populated = isCellPopulated(antRow, antCol);
        if (populated) { // White cell
            rotateRight();
        } else {// Black cell
            rotateLeft();
        }
        setNextGenCell(antRow, antCol, !populated);
        antRow += antDir.rowDir;
        antCol += antDir.colDir;
        if (antRow < 0 || antRow >= rows() || antCol < 0 || antCol >= cols())
            end();
    }

    @Override
    public Automatons getAutomatonType() {
        return Automatons.LANGTONS_ANT;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("Direction : " + antDir.toString() + "\n");
        sb.append(super.toString());
        return sb.toString();
    }

    @Override
    protected void endAutomaton() {

    }
}
