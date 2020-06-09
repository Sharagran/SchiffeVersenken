package de.adf;

import java.rmi.*;

/**
 * Interface mit Methoden, welche von RMI ausgeführt werden.
 */
interface GameManagerInterface extends Remote {
    public boolean shoot(int x, int y) throws RemoteException;

    public boolean isLost() throws RemoteException;

    public void initStub(String ip) throws RemoteException;

    public void ready() throws RemoteException;
}
