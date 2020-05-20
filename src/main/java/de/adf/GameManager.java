package de.adf;

public class GameManager {

    private int[][] cells = new int[10][10];

    public GameManager() {
        super();
    }

    private int getWinner() {
        int shipcountPlayer1 = 0;
        int shipcountPlayer2 = 0;

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells.length; j++) {
                int element = cells[i][j];

                if (element == 1)
                    shipcountPlayer1++;
                else if (element == 2)
                    shipcountPlayer2++;
            }
        }

        if (shipcountPlayer1 == 0)
            return 2;
        else if (shipcountPlayer2 == 0)
            return 1;
        else
            return 0;

    }

}