package application.automatons;

public class ElementaryAutomaton extends CellularAutomaton {
    public static final int      DEFAULT_ROWS = 32;
    public static final int      DEFAULT_RULE = 222;
    private final ElementaryRule rule;

    public ElementaryAutomaton() {
        this(DEFAULT_RULE, DEFAULT_ROWS);
    }

    public ElementaryAutomaton(int rule) {
        this(rule, DEFAULT_ROWS);
    }

    public ElementaryAutomaton(int rule, int rows) {
        super(rows, rows * 2 - 1);// cols must be odd
        if (rows < 2)
            throw new IllegalArgumentException("rows must be >= 2");
        if (rule < 0 || rule > 255)
            throw new IllegalArgumentException("rule must be between 0 and 255");
        this.rule = new ElementaryRule(rule);
    }

    public ElementaryRule getRule() {
        return rule;
    }

    @Override
    protected void init() {
        setCurrentGenCell(0, ((cols() - 1) / 2), true);
        generation = 1;
    }

    @Override
    protected void nextStep() {
        if (generation < rows()) {
            nextGeneration.initWithOther(currentGeneration);
            boolean left, middle, right;
            for (int col = 0; col < cols(); col++) {
                left = col > 0 ? isCellPopulated(generation - 1, col - 1) : false;
                middle = isCellPopulated(generation - 1, col);
                right = col < (cols() - 1) ? isCellPopulated(generation - 1, col + 1) : false;
                setNextGenCell(generation, col, rule.getResult(left, middle, right));
            }
        } else {
            end();
        }
    }

    @Override
    public void changeSize(int rows, int cols) {
        super.changeSize(rows, rows * 2 - 1);
    }

    @Override
    protected void endAutomaton() {
    }

    @Override
    public Automatons getAutomatonType() {
        return Automatons.ELEMENTARY;
    }
}
