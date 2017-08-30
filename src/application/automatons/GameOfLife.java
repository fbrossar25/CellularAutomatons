package application.automatons;

public class GameOfLife extends CellularAutomaton{
	
	public GameOfLife() {
		super();
		randomizeCells();
	}
	
	public GameOfLife(int size) {
		super(size,size);
		randomizeCells();
	}
	
	public GameOfLife(int rows, int cols) {
		super(rows,cols);
		randomizeCells();
	}
	
	@Override
	public void nextStep() {
		for(int row=0; row<rows(); row++) {
			for(int col=0; col<cols(); col++) {
				int neighbours = getAliveNeighbours(row,col);
				if(isCellPopulated(row,col)) {
					if(neighbours < 2)
						setCell(row, col, false);
					else if (neighbours > 3)
						setCell(row,col,false);
				}else {
					if(neighbours == 3)
						setCell(row,col,true);
				}
			}
		}
	}
	
	public int getAliveNeighbours(int row, int col) {
		//FIXME bad neighbours count
		if(!grid.isValidCell(row, col))
			return 0;
		int neighbours = 0;
		System.out.println("Cell ("+row+","+col+") : ");
		for(int i=row-1; i<=row+1; i++) {
			for(int j=col-1; j<=col+1; j++) {
				try {
					if(i >= 0 && i < grid.rows() && j >= 0 && j < grid.cols()) {
						if(!(i==row && j==col)) {
							if(isCellPopulated(i,j)) {
								System.out.println("\t("+i+","+j+") alive");
								neighbours++;
							}
						}
					}
				}catch(IndexOutOfBoundsException e) {
					//ignoring
					System.err.println(e.getMessage());
				}
			}
		}
		System.out.println("\ttotal : "+neighbours);
		return neighbours;
	}

}
