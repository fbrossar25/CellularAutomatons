package application.automatons;

public class ElementaryRule {
    // Always 8 boolean representing the 8 combinations of the previous
    // Default rule -> rule 222 (full pyramid), see
    // http://mathworld.wolfram.com/ElementaryCellularAutomaton.html
    private boolean[] results;

    public ElementaryRule() {
    }

    public ElementaryRule(boolean[] results) {
        if (results.length != 8) {
            throw new IllegalArgumentException("Results must be an array of 8 booleans");
        }
        this.results = results;
    }

    public ElementaryRule(int rule) {
        if (rule < 0 || rule > 255)
            throw new IllegalArgumentException("The rule must be between 0 and 255");
        results = new boolean[8];
        String s = Integer.toBinaryString(rule);
        for (int i = s.length(); i < 8; i++) {
            s = "0" + s;
        }
        for (int i = 0; i < 8; i++) {
            results[i] = s.charAt(i) == '1';
        }
    }

    public String toBinaryString() {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < 8; i++)
            sb.append(results[i] ? "1" : "0");
        return sb.toString();
    }

    public int toInteger() {
        String bs = toBinaryString();
        return Integer.parseInt(bs, 2);
    }

    public static int lmrToIndex(boolean left, boolean middle, boolean right) {
        String s = (left ? "1" : "0") + (middle ? "1" : "0") + (right ? "1" : "0");
        switch (s) {
        case "000":
            return 7;
        case "001":
            return 6;
        case "010":
            return 5;
        case "011":
            return 4;
        case "100":
            return 3;
        case "101":
            return 2;
        case "110":
            return 1;
        case "111":
        default:
            return 0;
        }
    }

    public boolean getResult(boolean left, boolean middle, boolean right) {
        return results[lmrToIndex(left, middle, right)];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        String bs = toBinaryString();
        sb.append("Rule " + toInteger() + " (" + bs + "):\n");
        String ibs = "";
        for (int i = 0; i < 8; i++) {
            ibs = "000" + Integer.toBinaryString(i); // toBinString() returns the number without leading zeros so we add the maximum
                                                     // zeros possible then we use substring
            sb.append(ibs.substring(ibs.length() - 3, ibs.length()) + " -> " + results[i] + "\n");
        }
        return sb.toString();
    }
}
