package de.adf;

import java.rmi.*;
import java.rmi.server.*;

interface GameManagerInterface extends Remote {
    public boolean shoot(int x, int y) throws RemoteException;

    public boolean isLost() throws RemoteException;
}

public class GameManager extends UnicastRemoteObject implements GameManagerInterface {
    // TODO: change GameManager into Multiplayer interface
    // TODO: each GameManager only has the local player board. Local methods
    // (placeShip, placeShipPart, getWinner[modified]), multiplayer methods (shoot,
    // getWinner[modified])

    private int[][] myBoard = new int[10][10];

    public GameManager(String ip) throws RemoteException {
        super();

        System.setProperty("java.security.policy", "./java.policy");
        System.setSecurityManager(new SecurityManager());
    }

    // leftmost x, topmost y
    public boolean placeShip(int x, int y, int shipLenght, Boolean horizontal) {
        // Out of bound check
        if (horizontal) {
            if (x + shipLenght >= 10)
                return false;
        } else {
            if (y + shipLenght >= 10)
                return false;
        }

        // place parts
        for (int i = 0; i < shipLenght; i++) {
            placeShipPart(x, y);

            if (horizontal)
                x++;
            else
                y++;
        }

        // ship completely placed
        return true;
    }

    private void placeShipPart(int x, int y) {
        myBoard[x][y] = 1;
    }

    ////#region Remote methods
    public boolean shoot(int x, int y) throws RemoteException {
        boolean shipHit = myBoard[x][y] == 1;
        myBoard[x][y] = 0;
        return shipHit;
    }

    public boolean isLost() throws RemoteException {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (myBoard[i][j] == 1)
                    return false; // Schiff gefunden = noch nicht verloren
            }
        }

        return true; // kein Schiff gefunden = verloren
    }
    //#endregion
    
}