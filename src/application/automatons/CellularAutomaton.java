package application.automatons;

import application.helpers.BoolGrid;

public abstract class CellularAutomaton {
	protected BoolGrid currentGeneration;
	protected BoolGrid nextGeneration;
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
		this.currentGeneration = new BoolGrid(rows,cols);
		this.nextGeneration = new BoolGrid(rows,cols);
		init();
	}
	
	protected abstract void init();
	
	public void clear() {
		this.currentGeneration.reset();
		this.nextGeneration.reset();
		this.generation = 0;
	}
	
	public void next() {
		nextStep();
		this.generation++;
		this.currentGeneration.initWithOther(this.nextGeneration);
		//this.nextGeneration.reset();
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
		return this.currentGeneration.rows();
	}
	
	public int cols() {
		return this.currentGeneration.cols();
	}
	
	public boolean isCellPopulated(int row, int col) {
		boolean b;
		try {
			b = this.currentGeneration.get(row, col);
		}catch(IndexOutOfBoundsException e){
			//ignoring e
			b = false;
		}
		return b;
	}
	
	protected void setCell(BoolGrid grid, int row, int col, boolean alive) {
		if(!grid.isValidCell(row, col))
			throw new IndexOutOfBoundsException("Cell does not exists : ("+row+","+col+")");
		grid.set(row, col, alive);
	}
	
	public void setCurrentGenCell(int row, int col, boolean alive) throws IndexOutOfBoundsException{
		setCell(this.currentGeneration,row,col,alive);
	}
	
	public void setNextGenCell(int row, int col, boolean alive)  throws IndexOutOfBoundsException{
		setCell(this.nextGeneration,row,col,alive);
	}
	
	public void randomizeCells() {
		this.clear();
		/*
		for(int row=0; row<rows(); row++) {
			for(int col=0; col<cols(); col++) {
				setCurrentGenCell(row,col,Math.random() < 0.5);
			}
		}
		*/
		setCurrentGenCell(0,1,true);
		setCurrentGenCell(1,0,true);
		setCurrentGenCell(1,1,true);
		setCurrentGenCell(1,2,true);
		setCurrentGenCell(2,0,true);
	}
	
	@Override
	public String toString() {
		return this.currentGeneration.toString();
	}
}
