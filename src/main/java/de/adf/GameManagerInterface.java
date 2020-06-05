package de.adf;

import java.rmi.*;

interface GameManagerInterface extends Remote {
    public boolean shoot(int x, int y) throws RemoteException;

    public boolean isLost() throws RemoteException;

    public void done() throws RemoteException;

    public void initStub(String ip) throws RemoteException;
}
