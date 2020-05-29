package de.adf;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

interface GameManagerInterface extends Remote {
    public boolean shoot(int x, int y) throws RemoteException;

    public boolean isLost() throws RemoteException;
    public void done() throws RemoteException;
}

public class GameManager extends UnicastRemoteObject implements GameManagerInterface {
    //TODO: change GameManager into Multiplayer interface
    //TODO: each GameManager only has the local player board. Local methods (placeShip, placeShipPart, getWinner[modified]), multiplayer methods (shoot, getWinner[modified])
    private static int port = 4711;
    private static String remoteobj = "remote";
    private int[][] myBoard;
    private GameManagerInterface remote;
    private GameManager gm;
    private boolean yourturn;

    public GameManager(String ip) throws RemoteException {
        super();
        myBoard = new int[10][10];

        initSkeleton();
        initStub(ip);
    }
    
    public GameManager() throws RemoteException {

    }

    public void initSkeleton() throws RemoteException {
        //System.setProperty("java.security.policy", "./java.policy");
        //System.setSecurityManager(new SecurityManager());
        Registry reg = LocateRegistry.createRegistry(port);
        gm = new GameManager();
        boolean bound = false;
        for (int i = 0; ! bound && i < 2; i++) {
            try{
                reg.rebind (remoteobj, gm);
                System.out.println (remoteobj + " bound to registry, port " + port + ".");
                bound = true;
            }
            catch (RemoteException e) 
            {
                System.out.println ("Rebinding failed, " + "retrying ...");
                reg = LocateRegistry.createRegistry(port);
                System.out.println ("Registry started on port " + port + ".");
            }
        }
        System.out.println ("Server ready.");
    }

    public void initStub(String ip) {
        try {
            ip = "localhost"; //FIXME: debug
            String rmiurl = "rmi://" + ip + ":" + port + "/" + remoteobj;
            System.out.println(rmiurl);
            remote = (GameManagerInterface) Naming.lookup("rmi://localhost:4711/remote");
            boolean b = remote.isLost();
            System.out.println("test: " + b);
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
    //#endregion
    
}