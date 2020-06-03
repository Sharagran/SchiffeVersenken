package de.adf;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

interface GameManagerInterface extends Remote {
    public boolean shoot(int x, int y) throws RemoteException;
    public boolean isLost() throws RemoteException;
    public void done() throws RemoteException;
    public void pair(String ip) throws RemoteException;
}

public class GameManager extends UnicastRemoteObject implements GameManagerInterface {
    //TODO: change GameManager into Multiplayer interface
    //TODO: each GameManager only has the local player board. Local methods (placeShip, placeShipPart, getWinner[modified]), multiplayer methods (shoot, getWinner[modified])
    public static final int PORT = 4711;
    private final String REMOTEOBJ = "remote";
    private int[][] myBoard;
    private GameManagerInterface remote;
    private boolean yourturn;
    private boolean isHost;

    public GameManager(String ip) throws RemoteException {
        super();
        myBoard = new int[10][10];

        initSkeleton();
        initStub(ip);
    }
    
    public GameManager() throws RemoteException {
        super();
        myBoard = new int[10][10];
        isHost = true;
        initSkeleton();
    }

    public void initSkeleton() throws RemoteException {
        Registry reg = LocateRegistry.createRegistry(PORT);
        boolean bound = false;
        for (int i = 0; ! bound && i < 2; i++) {
            try{
                reg.rebind (REMOTEOBJ, this);
                System.out.println (REMOTEOBJ + " bound to registry, port " + PORT + ".");
                bound = true;
            }
            catch (RemoteException e) 
            {
                System.out.println ("Rebinding failed, " + "retrying ...");
                reg = LocateRegistry.createRegistry(PORT);
                System.out.println ("Registry started on port " + PORT + ".");
            }
        }
        System.out.println ("Server ready.");
    }

    public void initStub(String ip) {
        try {
            String rmiurl = "rmi://" + ip + ":" + PORT + "/" + REMOTEOBJ;
            System.out.println(rmiurl);
            remote = (GameManagerInterface) Naming.lookup(rmiurl);
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
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
        if (!shipHit)
            done();
        
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

    public void done() {
        yourturn = !yourturn;
    }
    
    /**
     * Erwiedert die verbindung mit dem Client.
     * @param ip ip des Clients.
     */
    public void pair(String ip) {
        if (remote == null) {
            String rmiurl = "rmi://" + ip + ":" + PORT + "/" + REMOTEOBJ;
            try {
                remote = (GameManagerInterface) Naming.lookup(rmiurl);
            } catch (Exception e) {
                //TODO: handle exception
                e.printStackTrace();
            }
        }

    }
    //#endregion
    
}