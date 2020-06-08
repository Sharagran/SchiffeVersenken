package de.adf;

public class Coordinate {
    public int x;
    public int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    static public int indexToXCoordinate(int i) {
        return i+1;
    }

    static public char indexToYCoordinate(int i) {
        return (char) (i + 65);
    }
}