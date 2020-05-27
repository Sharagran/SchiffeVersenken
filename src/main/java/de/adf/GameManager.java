package de.adf;

import java.rmi.*;
import java.rmi.server.*;

interface GameManagerInterface extends Remote {
    public boolean shoot(int x, int y) throws RemoteException;
    public int getWinner() throws RemoteException;
}

public class GameManager extends UnicastRemoteObject implements GameManagerInterface {
    //TODO: change GameManager into Multiplayer interface
    //TODO: each GameManager only has the local player board. Local methods (placeShip, placeShipPart, getWinner[modified]), multiplayer methods (shoot, getWinner[modified])

    private int[][] boardPlayer1 = new int[10][10];
    private int[][] boardPlayer2 = new int[10][10];

    public GameManager(String ip) throws RemoteException {
        super();

        System.setProperty("java.security.policy", "./java.policy");
        System.setSecurityManager(new SecurityManager());
    }

    // leftmost x, topmost y
    public boolean placeShip(int targetBoard, int x, int y, int shipLenght, Boolean horizontal) throws Exception {
        // Valid player board check
        if(targetBoard != 1 || targetBoard != 2) {
            throw new Exception("Invalid targetBoard");
        }

        // Out of bound check
        if(horizontal) {
            if(x + shipLenght >= 10)
                return false;
        } else {
            if(y + shipLenght >= 10)
                return false;
        }

        // place parts
        for (int i = 0; i < shipLenght; i++) {
            placeShipPart(targetBoard, x, y);

            if(horizontal)
                x++;
            else
                y++;
        }

        // ship completely placed
        return true;
    }

    public void shoot(int targetBoard, int x, int y) throws Exception {
        if(targetBoard == 1) {
            boardPlayer1[x][y] = 0;
        } else if (targetBoard == 2) {
            boardPlayer2[x][y] = 0;
        } else {
            throw new Exception("Invalid targetBoard");
        }
    }

    private void placeShipPart(int targetBoard, int x, int y) {
        if(targetBoard == 1) {
            boardPlayer1[x][y] = 1;
        } else if (targetBoard == 2) {
            boardPlayer2[x][y] = 1;
        }
    }

    public int getWinner() {
        int shipcountPlayer1 = 0;
        int shipcountPlayer2 = 0;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                shipcountPlayer1 += boardPlayer1[i][j];
                shipcountPlayer2 += boardPlayer2[i][j];
            }
        }

        if (shipcountPlayer1 == 0)
            return 2;   //Spieler 2 gewinnt
        else if (shipcountPlayer2 == 0)
            return 1;   //Spiler 1 gewinnt
        else
            return 0;   //Es gibt noch keinen gewinner

    }

}