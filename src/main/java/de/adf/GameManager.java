package de.adf;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

interface GameManagerInterface extends Remote {
    int PORT = 50000; // Dynamic Port Number / Private Port
    String REMOTEOBJ = "remote";

    public boolean shoot(int x, int y) throws RemoteException;

    public boolean isLost() throws RemoteException;

    public void done() throws RemoteException;
}

public class GameManager extends UnicastRemoteObject implements GameManagerInterface {
    // TODO: change GameManager into Multiplayer interface
    // TODO: each GameManager only has the local player board. Local methods
    // (placeShip, placeShipPart, getWinner[modified]), multiplayer methods (shoot,
    // getWinner[modified])
    private int[][] myBoard;
    public GameManagerInterface remote;
    private boolean yourturn;
    public boolean isHost;
    private Registry reg;

    public GameManager(String ip) throws RemoteException {
        super();
        myBoard = new int[10][10];

        if (ip == null) {
            isHost = true; //Hosting
            initSkeleton();
        }
        else {
            isHost = false; //Joining
            initStub(ip);
        }
    }

    // start server
    public void initSkeleton() throws RemoteException {
        reg = LocateRegistry.createRegistry(PORT);
        boolean bound = false;
        for (int i = 0; !bound && i < 2; i++) {
            try {
                reg.rebind(REMOTEOBJ, this);
                System.out.println(REMOTEOBJ + " bound to registry, port " + PORT + ".");
                bound = true;
            } catch (RemoteException e) {
                System.out.println("Rebinding failed, " + "retrying ...");
                reg = LocateRegistry.createRegistry(PORT);
                System.out.println("Registry started on port " + PORT + ".");
            }
        }
        System.out.println("Server ready.");
    }

    // connect to server
    public void initStub(String ip) {
        try {
            String rmiurl = "rmi://" + ip + ":" + PORT + "/" + REMOTEOBJ;
            System.out.println(rmiurl);
            remote = (GameManagerInterface) Naming.lookup(rmiurl);
            System.out.println("connected to: " + ip);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public GameManagerInterface getRemoteObject() {
        return remote;
    }

    // leftmost x, topmost y
    public boolean placeShip(int x, int y, int shipLenght, Boolean horizontal) {
        // #region Out of bound check
        if (horizontal) {
            if (x + shipLenght >= 10)
                return false;
        } else {
            if (y + shipLenght >= 10)
                return false;
        }
        // #endregion

        // #region schaut, ob Schiffe in der Nähe sind(horizontal & vertikal)

        // FIXME: besseren variablennamen als thisY & thisX finden
        int thisY;
        int thisX;
        if (horizontal) {
            thisY = y;
            thisX = x;
        } else {
            thisY = x;
            thisX = y;
        }
        int frameHorizontal = 0;
        for (int i = thisY - 1; i <= thisY + 1; i++) {
            for (int j = thisX - 1; i < shipLenght + 2; j++) {
                if (x >= 0 && y >= 0 && x <= 9 && y <= 9) {
                    if (horizontal)
                        frameHorizontal += myBoard[j][i];
                    else
                        frameHorizontal += myBoard[i][j];
                }
            }
        }
        if (shipLenght != frameHorizontal)
            return false;
        // #endregion

        placeParts(x, y, shipLenght, horizontal);
        return true;
    }

    private void placeParts(int x, int y, int shipLenght, Boolean horizontal) {
        for (int i = 0; i < shipLenght; i++) {
            placeShipPart(x, y);

            if (horizontal)
                x++;
            else
                y++;
        }
    }

    private void placeShipPart(int x, int y) {
        myBoard[x][y] = 1;
    }

    // #region Remote methods
    public boolean shoot(int x, int y) throws RemoteException {
        boolean shipHit = myBoard[x][y] == 1;
        myBoard[x][y] = 0;
        if (!shipHit)
            done();

        return shipHit;
    }

    public boolean isLost() throws RemoteException {
        System.out.println("ausgeführt"); //FIXME: Debug
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
    // #
}