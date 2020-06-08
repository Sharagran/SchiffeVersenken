package de.adf;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import javax.swing.*;

public class GameWindow extends JFrame {

    GameManager gm;
    GameBoard localBoard, remoteBoard;
    int shipIndex = 0;
    int[] ships = new int[] { 4, 3, 3, 2, 2, 2, 1, 1, 1, 1 };
    private boolean prepare = true;
    private boolean placeHorizontal = true;
    JLabel status_lbl;
    JButton changeBtn;

    public GameWindow(String ip) throws RemoteException {
        setTitle("Schiffe versenken");
        setSize(Settings.SCREENWIDTH, Settings.SCREENHEIGHT);
        setResizable(true); // TODO: debug only (set to false)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 50, 20, 50);

        localBoard = new GameBoard();
        remoteBoard = new GameBoard();

        generateUI();

        // gm.remote.[methode()] für das remote objekt
        // gm.[methode()] für lokales objekt
        gm = new GameManager(ip);
        if (!gm.isHost)
            gm.remote.initStub(getLocalAddress(ip));

        localBoard.setEnabledAll(true);
        remoteBoard.setEnabledAll(false);

        add(localBoard, gbc);
        gbc.gridx = 1;
        add(remoteBoard, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        status_lbl = new JLabel("Platziere Schiff in größe " + ships[shipIndex]);
        status_lbl.setFont(new Font(status_lbl.getName(), Font.PLAIN, 23));
        add(status_lbl, gbc);

        validate();
        repaint();
    }

    public void generateUI(){
        changeBtn = new JButton("Horizontal");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        changeBtn.addActionListener(e -> changeClicked(e));
        add(changeBtn, gbc);
    }

    private void changeClicked(ActionEvent e) {
        placeHorizontal = !placeHorizontal;
        changeBtn.setText(placeHorizontal ? "Horizontal" : "Vertikal");
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
            generateBoard();
        }

        private void generateBoard() {
            // Empty top left corner
            JLabel empty = new JLabel();
            add(empty);
            for (int i = 1; i <= 10; i++) {
                // Label X axis (numbers)
                JLabel number = new JLabel(Integer.toString(i));
                number.setHorizontalAlignment(SwingConstants.CENTER);
                add(number);
            }

            for (int i = 1; i < 11; i++) {
                // Label Y axis (letters)
                JLabel letter = new JLabel(Character.toString(i + 64));
                letter.setVerticalAlignment(SwingConstants.CENTER);
                add(letter);

                // Cells
                for (int j = 1; j < 11; j++) {
                    Cell cell = new Cell(j - 1, i - 1);
                    cell.setPreferredSize(new Dimension(32, 32));
                    add(cell);
                    cells[j - 1][i - 1] = cell;
                }
            }
        }

        private void setEnabledAll(boolean b) {
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells.length; j++) {
                    if (!cells[i][j].gotShot) {
                        cells[i][j].setEnabled(b);
                    }
                }
            }
        }

        public class Cell extends JButton {



            private boolean hasShip = false;
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

                        if (prepare) {
                            //Schiffe platzieren
                            if (gm.placeShip(x, y, ships[shipIndex], placeHorizontal)) {
                                shipIndex++;

                                if (shipIndex >= ships.length) {
                                    prepare = false;
                                    localBoard.setEnabledAll(false);
                                    changeBtn.setVisible(false);
                                    try {
                                        if (gm.isHost && gm.remote != null) {
                                            gm.remote.ready();
                                        } else {
                                            remoteBoard.setEnabledAll(false);
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }

                            if (shipIndex < ships.length) {
                                status_lbl.setText("Platziere Schiff in größe " + ships[shipIndex]);
                            } else {
                                gm.ready = true;
                                if (!gm.remReady) {
                                    if (!gm.isHost) {
                                        status_lbl.setText("Placing done, waiting for Host to be ready.");
                                    } else {
                                        status_lbl.setText("Begin game.");
                                    }
                                } else {
                                    status_lbl.setText("Host ready, begin game.");
                                    remoteBoard.setEnabledAll(gm.remReady);
                                }
                            }
                        } else {
                            // Schießen
                            gotShot = true;
                            setEnabled(false);
                            try {
                                System.out.println("Shooting: " + Coordinate.indexToXCoordinate(x) + ","
                                        + Coordinate.indexToYCoordinate(y));
                                hasShip = gm.remote.shoot(x, y);
                                repaint();

                                status_lbl.setText("[" + Coordinate.indexToXCoordinate(x) + " , " + Coordinate.indexToYCoordinate(y) + "]" + (hasShip ? " hit." : " miss."));

                                
                                if (!hasShip) {
                                    gm.done();
                                }

                                try {
                                    if (gm.remote.isLost()) {
                                        gm.gameOver(true);
                                    }
                                } catch (Exception exc) {
                                    exc.printStackTrace();
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
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
                    g2.setColor(Settings.colors.get("background"));
                else
                    g2.setColor(Settings.colors.get("background-disabled"));

                if (hasShip) {
                    g2.setColor(Settings.colors.get("ship"));
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setStroke(new BasicStroke(4));

                if (gotShot) {
                    g2.setColor(Settings.colors.get("hit"));
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
        public GameManagerInterface remote;
        private boolean yourturn;
        public boolean isHost;
        private boolean remReady;
        private boolean ready;
        private Registry reg;

        public GameManager(String ip) throws RemoteException {
            super();

            initSkeleton();
            if (ip == null) {
                isHost = true;
            } else {
                isHost = false;
                initStub(ip);
            }

            yourturn = !isHost;
        }

        // start server
        public void initSkeleton() throws RemoteException {
            System.setProperty("java.security.policy", "./java.policy");
            System.setSecurityManager(new SecurityManager());
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
                e.printStackTrace();
            }
        }

        public GameManagerInterface getRemoteObject() {
            return remote;
        }

        // leftmost x, topmost y
        public boolean placeShip(int x, int y, int shipLenght, Boolean horizontal) {
            // #region Out of bound check
            // shipLenght-1 da startfeld mitgezählt werden muss
            if (horizontal) {
                if (x + shipLenght - 1 > 9)
                    return false;
            } else {
                if (y + shipLenght - 1 > 9)
                    return false;
            }
            // #endregion

            // #region schaut, ob Schiffe in der Nähe sind(horizontal & vertikal)
            int shortEdge;
            int longEdge;
            if (horizontal) {
                shortEdge = y;
                longEdge = x;
            } else {
                shortEdge = x;
                longEdge = y;
            }

            ArrayList<Coordinate> disabledCells = new ArrayList<>();
            int frameHorizontal = shipLenght;
            for (int i = shortEdge - 1; i <= shortEdge + 1; i++) {
                for (int j = longEdge - 1; j <= longEdge + shipLenght; j++) {
                    if (i >= 0 && j >= 0 && i <= 9 && j <= 9) {
                        if (horizontal) {
                            frameHorizontal += localBoard.cells[j][i].hasShip ? 1 : 0;
                            disabledCells.add(new Coordinate(j, i));
                        } else {
                            frameHorizontal += localBoard.cells[i][j].hasShip ? 1 : 0;
                            disabledCells.add(new Coordinate(i, j));
                        }
                    }
                }
            }
            if (shipLenght != frameHorizontal)
                return false;
            // #endregion

            for (Coordinate cord : disabledCells) {
                localBoard.cells[cord.x][cord.y].setEnabled(false);
            }
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
            localBoard.cells[x][y].hasShip = true;
            localBoard.cells[x][y].repaint();
        }

        private void gameOver(boolean win) {
            localBoard.setEnabledAll(false);
            remoteBoard.setEnabledAll(false);

            try {
                UnicastRemoteObject.unexportObject(gm.reg, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if(win) {
                status_lbl.setText("Gewonnen!");
            } else {
                status_lbl.setText("Verloren!");
            }
        }

        // #region Remote methods
        public boolean shoot(int x, int y) throws RemoteException {
            boolean shipHit = localBoard.cells[x][y].hasShip;
            localBoard.cells[x][y].gotShot = true;
            localBoard.cells[x][y].repaint();

            if (!shipHit)
                done();

            status_lbl.setText("[" + Coordinate.indexToXCoordinate(x) + " , " + Coordinate.indexToYCoordinate(y) + "] Enemy" + (shipHit ? " hit." : " miss."));

            try {
                if (isLost()) {
                    gameOver(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return shipHit;
        }

        public boolean isLost() throws RemoteException {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (localBoard.cells[i][j].hasShip && localBoard.cells[i][j].gotShot == false)
                        return false; // Schiff gefunden = noch nicht verloren
                }
            }

            return true; // kein Schiff gefunden = verloren
        }

        public void done() {
            // Change turn
            yourturn = !yourturn;
            remoteBoard.setEnabledAll(yourturn);
        }

        public void ready() {
            gm.remReady = true;
            status_lbl.setText("Host ready, begin game.");

            if (gm.ready) {
                remoteBoard.setEnabledAll(true);
            }
        }
        // #endregion
    }

}