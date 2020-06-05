package de.adf;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import javax.swing.*;

import de.adf.GameWindow.GameBoard.Cell;

public class GameWindow extends JFrame {

    GameManager gm;
    GameBoard localBoard, remoteBoard;

    public GameWindow(String ip) throws RemoteException {
        setTitle("Schiffe versenken");
        setSize(Settings.SCREENWIDTH, Settings.SCREENHEIGHT);
        setResizable(true); // FIXME: debug only (set to false)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 50, 0, 50);

        localBoard = new GameBoard();
        remoteBoard = new GameBoard();

        // gm.remote.[methode()] für das remote objekt
        // gm.[methode()] für lokales objekt
        gm = new GameManager(ip);
        if (!gm.isHost)
            gm.remote.initStub(getLocalAddress(ip));

        localBoard.setEnabledAll(false);
        remoteBoard.setEnabledAll(!gm.isHost);

        add(localBoard, gbc);
        add(remoteBoard, gbc);

        validate();
        repaint();
    }

    public char indexToCoordinate(int i) { // FIXME: debug only
        return (char) (i + 65);
    }

    public String getLocalAddress(String remoteip) {
        ArrayList<String> localAddresses = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> ownNetworks = NetworkInterface.getNetworkInterfaces();
            while (ownNetworks.hasMoreElements()) {
                NetworkInterface e = ownNetworks.nextElement();
                Enumeration<InetAddress> a = e.getInetAddresses();
                while (a.hasMoreElements()) {
                    InetAddress addr = a.nextElement();
                    if (addr.isSiteLocalAddress()) {
                        localAddresses.add(addr.getHostAddress());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        for (String addr : localAddresses) {
            if (remoteip.contains(addr.substring(0, addr.lastIndexOf('.') + 1))) {
                return addr;
            }
        }
        return null;
    }

    public class GameBoard extends JPanel {

        public Cell[][] cells = new Cell[10][10];

        public GameBoard() {
            setLayout(new GridLayout(11, 11));
            // setPreferredSize(new Dimension(500, 500));

            generateBoard();
        }

        private void generateBoard() {
            // Empty top left corner
            JLabel empty = new JLabel();
            add(empty);
            for (int i = 1; i <= 10; i++) {
                // Label Y axis (numbers)
                JLabel number = new JLabel(Integer.toString(i));
                number.setHorizontalAlignment(SwingConstants.CENTER);
                add(number);
            }

            for (int i = 1; i < 11; i++) {
                // Label X axis (letters)
                JLabel letter = new JLabel(Character.toString(i + 64));
                letter.setVerticalAlignment(SwingConstants.CENTER);
                add(letter);

                // Cells
                for (int j = 1; j < 11; j++) {
                    Cell cell = new Cell(i - 1, j - 1);
                    cell.setPreferredSize(new Dimension(32, 32));
                    add(cell);
                    cells[i - 1][j - 1] = cell;
                }
            }
        }

        private void setEnabledAll(boolean b) {
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells.length; j++) {
                    cells[i][j].setEnabled(b);
                }
            }
        }

        public class Cell extends JButton {

            private Map<String, Color> colors = Map.of("background", Color.white, "hit", Color.red,
                    "background-disabled", Color.gray);

            private boolean hasShip = Math.random() > 0.5;
            private boolean gotShot = false;
            private int x;
            private int y;

            public Cell(int x, int y) {
                super();
                setFocusable(false);

                this.x = x;
                this.y = y;

                addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        gotShot = true; // local
                        try {
                            System.out.println("Shooting: " + (x + 1) + "," + indexToCoordinate(y));
                            hasShip = gm.remote.shoot(x, y);
                            System.out.println("ShipHit: " + hasShip);
                            repaint();
                            if (!hasShip) {
                                remoteBoard.setEnabledAll(false);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

                if (isEnabled())
                    g2.setColor(colors.get("background"));
                else
                    g2.setColor(colors.get("background-disabled"));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setStroke(new BasicStroke(4));

                if (gotShot) {
                    g2.setColor(colors.get("hit"));
                    if (hasShip) {
                        int padding = 10;
                        g2.drawLine(0 + padding, 0 + padding, getWidth() - padding, getHeight() - padding);
                        g2.drawLine(0 + padding, getHeight() - padding, getWidth() - padding, 0 + padding);
                    } else {
                        int size = 10;
                        g2.fillArc(getWidth() / 2 - size / 2, getHeight() / 2 - size / 2, size, size, 0, 360);
                    }

                }

                g2.dispose();
            }

        }
    }

    class GameManager extends UnicastRemoteObject implements GameManagerInterface {
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
            initSkeleton();
            if (ip == null) {
                isHost = true; // Hosting
                // initSkeleton();
            } else {
                isHost = false; // Joining
                initStub(ip);
            }
        }

        // start server
        public void initSkeleton() throws RemoteException {
            reg = LocateRegistry.createRegistry(Settings.PORT);
            boolean bound = false;
            for (int i = 0; !bound && i < 2; i++) {
                try {
                    reg.rebind(Settings.REMOTEOBJ, this);
                    System.out.println(Settings.REMOTEOBJ + " bound to registry, port " + Settings.PORT + ".");
                    bound = true;
                } catch (RemoteException e) {
                    System.out.println("Rebinding failed, " + "retrying ...");
                    reg = LocateRegistry.createRegistry(Settings.PORT);
                    System.out.println("Registry started on port " + Settings.PORT + ".");
                }
            }
            System.out.println("Server ready.");
        }

        // connect to server
        public void initStub(String ip) throws RemoteException {
            try {
                String rmiurl = "rmi://" + ip + ":" + Settings.PORT + "/" + Settings.REMOTEOBJ;
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
            boolean shipHit = myBoard[x][y] == 1; // FIXME: ---> getroffene schiffe werden falsch beim client angezeigt,
                                                  // da auf myBoard geguckt wird welches immer 0 ist und nicht auf Cell
                                                  // welche random sind <---
            myBoard[x][y] = 0;
            if (!shipHit)
                done();

            System.out.println((x + 1) + "," + indexToCoordinate(y) + "\tshipHit: " + shipHit);

            localBoard.cells[x][y].gotShot = true;
            localBoard.cells[x][y].repaint();
            return shipHit;
        }

        public boolean isLost() throws RemoteException {
            System.out.println("ausgeführt"); // FIXME: Debug
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (myBoard[i][j] == 1)
                        return false; // Schiff gefunden = noch nicht verloren
                }
            }

            return true; // kein Schiff gefunden = verloren
        }

        public void done() {
            // yourturn = !yourturn;
            remoteBoard.setEnabledAll(true);
        }
        // #endregion
    }

}