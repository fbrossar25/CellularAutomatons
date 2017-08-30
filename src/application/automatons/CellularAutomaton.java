package application.automatons;

import application.helpers.BoolGrid;

public abstract class CellularAutomaton {
	protected BoolGrid grid;
	protected int speed = 1;
	protected boolean paused = false;
	protected int generation = 0;
	
	public CellularAutomaton() {
		this(BoolGrid.DEFAULT_SIZE, BoolGrid.DEFAULT_SIZE);
	}
	
	public CellularAutomaton(int size) {
		this(size,size);
	}
	
	public CellularAutomaton(int rows, int cols) {
		if(rows < 1 || cols < 1)
			throw new IllegalArgumentException("Invalid dimensions : ("+rows+","+cols+")");
		this.grid = new BoolGrid(rows,cols);
	}
	
	public void clear() {
		this.grid.reset();
		this.generation = 0;
	}
	
	public void next() {
		this.generation++;
		nextStep();
	}
	
	protected abstract void nextStep();
	
	public void pause() {
		this.paused = true;
	}
	
	public void start() {
		this.paused = false;
	}
	
	public boolean isPaused() {
		return this.paused;
	}
	
	public int getSpeed() {
		return this.speed;
	}
	
	public void setSpeed(int value) {
		if(value < 0) {
			this.speed = 1;
			this.pause();
		}
		else {
			this.speed = value;
		}
	}
	
	public int getGeneration() {
		return this.generation;
	}
	
	public int rows() {
		return this.grid.rows();
	}
	
	public int cols() {
		return this.grid.cols();
	}
	
	public boolean isCellPopulated(int row, int col) {
			return this.grid.get(row, col);
	}
	
	public void setCell(int row, int col, boolean alive) throws IndexOutOfBoundsException{
		if(!this.grid.isValidCell(row, col))
			throw new IndexOutOfBoundsException("Cell does not exists : ("+row+","+col+")");
		this.grid.set(row, col, alive);
	}
	
	public void randomizeCells() {
		for(int row=0; row<rows(); row++) {
			for(int col=0; col<cols(); col++) {
				setCell(row,col,Math.random() < 0.5);
			}
		}
	}
	
	@Override
	public String toString() {
		return this.grid.toString();
	}
}
