package de.adf;

import java.rmi.*;

public interface GameManagerInterface extends Remote {
    public boolean shoot(int x, int y) throws RemoteException;
    public int getWinner() throws RemoteException;
}