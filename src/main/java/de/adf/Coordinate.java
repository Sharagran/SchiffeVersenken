package de.adf;

/**
 * Koordinate
 */
public class Coordinate {
    public int x;
    public int y;

    /**
     * Erzeugt eine neue Koordinate
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Wandelt einen Array Index in die auf dem Gameboard entsprechende X Koordinate um.
     * @return Integer zwischen 1-10
     */
    static public int indexToXCoordinate(int index) {
        index = (index % 10) + 1; // 1-10
        return index+1;
    }

    /**
     * Wandelt einen Array Index in die auf dem Gameboard entsprechende Y Koordinate um.
     * @return Character (A-J)
     */
    static public char indexToYCoordinate(int index) {
        index = index % 10; // 0-9
        return (char) (index + 65);
    }

    /**
     * Erzeugt einen String welcher die übergebenen Koodinaten beschreibt.
     * @return X|Y
     */
    static public String toString(int x, int y) {
        return String.format("%s|%s", indexToXCoordinate(x), indexToYCoordinate(y));
    }

    /**
     * Erzeugt einen String welcher die Koodinaten des Objekts beschreibt.
     * @return X|Y
     */
    @Override
    public String toString() {
        return String.format("%s|%s", indexToXCoordinate(x), indexToYCoordinate(y));
    }
}