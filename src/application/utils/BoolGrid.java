package application.utils;

public class BoolGrid {
    private boolean[][]     grid;
    private int             rows, cols;
    public static final int DEFAULT_SIZE = 10;

    public BoolGrid() {
        this(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public BoolGrid(int size) {
        this(size, size);
    }

    public BoolGrid(int rows, int cols) {
        if (rows < 1 || cols < 1)
            throw new IllegalArgumentException("Invalid dimensions : (" + rows + "," + cols + ")");
        this.rows = rows;
        this.cols = cols;
        this.grid = new boolean[this.rows][this.cols];
        this.reset();
    }

    public void reset() {
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < this.cols; col++) {
                this.grid[row][col] = false;
            }
        }
    }

    public boolean isValidCell(int row, int col) {
        return (row >= 0 && row < this.rows) && (col >= 0 && col < this.cols);
    }

    public boolean get(int row, int col) {
        if (!this.isValidCell(row, col))
            throw new IndexOutOfBoundsException("Invalid index : (" + row + "," + col + ")");
        return this.grid[row][col];
    }

    public void set(int row, int col, boolean value) {
        if (!this.isValidCell(row, col))
            throw new IndexOutOfBoundsException("Invalid index : (" + row + "," + col + ")");
        this.grid[row][col] = value;
    }

    public int rows() {
        return this.rows;
    }

    public int height() {
        return this.rows;
    }

    public int cols() {
        return this.cols;
    }

    public int width() {
        return this.cols;
    }

    public void initWithOther(BoolGrid other) {
        if (other.rows() != this.rows() || other.cols() != this.cols())
            throw new IllegalArgumentException("BoolGrid dimension not equals : (" + this.rows + "," + this.cols + ") != " + "("
                    + other.rows + "," + other.cols + ")");
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < this.cols(); col++) {
                this.grid[row][col] = other.grid[row][col];
            }
        }
    }

    public String getRowAsBinaryString(int row) {
        if (row < 0 || row >= rows())
            throw new IllegalArgumentException("row must be between 0 and " + (rows() - 1));
        StringBuilder sb = new StringBuilder("");
        for (int col = 0; col < cols(); col++)
            sb.append(get(row, col) ? "1" : "0");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < this.cols; col++) {
                sb.append(get(row, col) ? "O" : "X");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean equals(BoolGrid o) {
        if (rows() != o.rows() || cols() != o.cols())
            return false;
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < this.cols; col++) {
                if (get(row, col) != o.get(row, col))
                    return false;
            }
        }
        return true;
    }
}
