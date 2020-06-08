package de.adf;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.net.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import javax.swing.*;

/**
 * GameWindow Fenster
 */
public class GameWindow extends JFrame {

    GameManager gm;
    GameBoard localBoard, remoteBoard;
    int[] ships = new int[] { 4, 3, 3, 2, 2, 2, 1, 1, 1, 1 };
    int shipIndex = 0;
    boolean placeHorizontal = true, prepare = true;
    JLabel status_lbl;
    JButton changeBtn;

    /**
     * Erzeugt das GameWindow und verbindet sich mit einem Server
     * 
     * @param ip IP des Servers
     */
    public GameWindow(String ip) throws RemoteException {
        // gm.remote.[methode()] für das remote Objekt
        // gm.[methode()] für lokales Objekt
        gm = new GameManager(ip);
        if (!gm.isHost)
            gm.remote.initStub(getLocalAddress(ip));

        generateUI();
        validate();
        repaint();
    }

    /**
     * Erzeugt das Fenster und das UI
     */
    public void generateUI() throws RemoteException {
        // Fenster Eigenschaften
        setTitle("Schiffe versenken");
        setSize(Settings.SCREENWIDTH, Settings.SCREENHEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 50, 20, 50);

        // Erzeugt die beiden Spielfelder
        localBoard = new GameBoard();
        localBoard.setEnabledAll(true);
        remoteBoard = new GameBoard();
        remoteBoard.setEnabledAll(false);
        // Fügt die Spielfelder hinzu
        add(localBoard, gbc);
        gbc.gridx = 1;
        add(remoteBoard, gbc);

        // Erzeugt das Status Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        status_lbl = new JLabel("Platziere Schiff in größe " + ships[shipIndex] + " (" + (shipIndex + 1) + " von "
                + ships.length + ")");
        status_lbl.setFont(new Font(status_lbl.getName(), Font.PLAIN, 23));
        add(status_lbl, gbc);

        // Erzeugt den Button um Schiffe um 90grad zu drehen
        changeBtn = new JButton("Horizontal");
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        changeBtn.addActionListener(e -> changeClicked(e));
        add(changeBtn, gbc);
    }

    /**
     * Wird aufgerufen wenn der Horizontal/Vertikal button gedrückt wird
     */
    private void changeClicked(ActionEvent e) {
        placeHorizontal = !placeHorizontal;
        changeBtn.setText(placeHorizontal ? "Horizontal" : "Vertikal");
    }

    /**
     * TODO: DANIEL
     */
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

    /**
     * Gameboard (Spielfeld)
     */
    public class GameBoard extends JPanel {

        public Cell[][] cells = new Cell[10][10];

        /**
         * Legt das Layout fest und erzeugt das Spielfeld
         */
        public GameBoard() {
            setLayout(new GridLayout(11, 11));
            generateBoard();
        }

        /**
         * Erzeugt das Spielfeld
         */
        private void generateBoard() {
            // Leere Ecke oben links
            JLabel empty = new JLabel();
            add(empty);
            for (int i = 1; i <= 10; i++) {
                // X-Achsen Beschriftung (Nummern)
                JLabel number = new JLabel(Integer.toString(i));
                number.setHorizontalAlignment(SwingConstants.CENTER);
                add(number);
            }

            for (int i = 1; i < 11; i++) {
                // Y-Achsen Beschriftung (Buchstaben)
                JLabel letter = new JLabel(Character.toString(i + 64));
                letter.setVerticalAlignment(SwingConstants.CENTER);
                add(letter);

                // Zellen
                for (int j = 1; j < 11; j++) {
                    Cell cell = new Cell(j - 1, i - 1);
                    cell.setPreferredSize(new Dimension(32, 32));
                    add(cell);
                    cells[j - 1][i - 1] = cell;
                }
            }
        }

        /**
         * Aktiviert/Deaktiviert alle Zellen
         * 
         * @param b Aktiviert/Deaktiviert
         */
        private void setEnabledAll(boolean b) {
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells.length; j++) {
                    if (!cells[i][j].gotShot) {
                        cells[i][j].setEnabled(b);
                    }
                }
            }
        }

        /**
         * Spielfeld Zelle
         */
        public class Cell extends JButton {
            private boolean hasShip, gotShot;
            private int x, y;

            /**
             * Erzeugt eine Zelle an einer Koordinate
             */
            public Cell(int x, int y) {
                super();
                setFocusable(false);

                this.x = x;
                this.y = y;

                addActionListener(new ActionListener() {

                    /**
                     * Wird ausgeführt wenn auf eine Zelle gedrückt wird
                     */
                    public void actionPerformed(ActionEvent e) {

                        if (prepare) {
                            // Schiffe platzieren
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
                                status_lbl.setText("Platziere Schiff in größe " + ships[shipIndex] + " ("
                                        + (shipIndex + 1) + " von " + ships.length + ")");
                            } else {
                                // Alle Schiffe platziert
                                gm.ready = true;
                                if (!gm.remReady) {
                                    if (!gm.isHost) {
                                        status_lbl.setText("Platzierungsphase beendet, warte bis der Host bereit ist.");
                                    } else {
                                        status_lbl.setText("Starte Spiel.");
                                    }
                                } else {
                                    status_lbl.setText("Host ist bereit, starte Spiel.");
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

                                status_lbl.setText("[" + Coordinate.indexToXCoordinate(x) + " , "
                                        + Coordinate.indexToYCoordinate(y) + "]"
                                        + (hasShip ? " getroffen." : " verfehlt."));

                                if (!hasShip) {
                                    // Kein Schiff getroffen
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

                // #region Hintergrund
                if (isEnabled())
                    g2.setColor(Settings.colors.get("background"));
                else
                    g2.setColor(Settings.colors.get("background-disabled"));

                if (hasShip) {
                    g2.setColor(Settings.colors.get("ship"));
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setStroke(new BasicStroke(4));
                // #endregion

                // #region Vordergrund
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
                // #endregion

                g2.dispose();
            }

        }
    }

    /**
     * GameManager (verwaltet die Spiellogik & Mehrspieler Verbindung)
     */
    class GameManager extends UnicastRemoteObject implements GameManagerInterface {
        public GameManagerInterface remote;
        private Registry reg;
        private boolean yourturn, ready, remReady;
        public boolean isHost;

        /**
        * Erzeugt den GameManager und verbindet sich mit dem Server
        * @param ip Server IP
        */
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

        /**
        * Erzeugt den Server
        */
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

        /**
        * Verbindet sich mit dem Server
        * @param ip Server IP
        */
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

        /**
        * Überprüft ob ein Schiff platziert werden kann und platziert diesen wenn möglich
        * @param x Linke Position des Schiffs (left)
        * @param y Obere Position des Schiffs (top)
        * @param shipLenght Länge des Schiffs
        * @param horizontal Ausrichtung des Schiffs (Horizontal/Vertikal)
        * @return Gibt True zurück wennn ein Schiff platziert werden konnte
        */
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

            // #region Überprüft ob andere Schiffe zu nah sind (horizontal & vertikal)
            int shortEdge; // Kurze Seite des Schiffs
            int longEdge; // Lange Seite des Schiffs
            if (horizontal) {
                shortEdge = y;
                longEdge = x;
            } else {
                shortEdge = x;
                longEdge = y;
            }

            ArrayList<Coordinate> disabledCells = new ArrayList<>(); // Zellen welche nach erfolgreichen platzieren eines Schiffs deaktiviert werden

            // überprüft ob Schiffe überlappen oder zu nah aneinander liegen 
            int frameHorizontal = shipLenght;
            for (int i = shortEdge - 1; i <= shortEdge + 1; i++) {
                for (int j = longEdge - 1; j <= longEdge + shipLenght; j++) {
                    if (i >= 0 && j >= 0 && i <= 9 && j <= 9) { // Im Array bereich bleiben
                        if (horizontal) {
                            frameHorizontal += localBoard.cells[j][i].hasShip ? 1 : 0; // Schiffe zählen
                            disabledCells.add(new Coordinate(j, i));
                        } else {
                            frameHorizontal += localBoard.cells[i][j].hasShip ? 1 : 0; // Schiffe zählen
                            disabledCells.add(new Coordinate(i, j));
                        }
                    }
                }
            }
            if (shipLenght != frameHorizontal)
                return false; // Andere Schiffe sind zu nah
            // #endregion

            // Zellen deaktivieren
            for (Coordinate cord : disabledCells) {
                localBoard.cells[cord.x][cord.y].setEnabled(false);
            }

            placeParts(x, y, shipLenght, horizontal); // Schiffteile platzieren
            return true; // Schiff wurde platziert
        }

        /**
        * Platziert alle Schiffteile
        * @param x Linke Position des Schiffs (left)
        * @param y Obere Position des Schiffs (top)
        * @param shipLenght Länge des Schiffs
        * @param horizontal Ausrichtung des Schiffs (Horizontal/Vertikal)
        */
        private void placeParts(int x, int y, int shipLenght, Boolean horizontal) {
            for (int i = 0; i < shipLenght; i++) {
                placeShipPart(x, y); // Schiffteil platzieren

                if (horizontal)
                    x++;
                else
                    y++;
            }
        }

        /**
        * Platziert ein Schiffsteil an der angegebenen Koordinate
        */
        private void placeShipPart(int x, int y) {
            localBoard.cells[x][y].hasShip = true;
            localBoard.cells[x][y].repaint();
        }

        /**
        * Spiel vorbei
        * @param win True = Gewonnen/False = Verloren
        */
        private void gameOver(boolean win) {
            localBoard.setEnabledAll(false);
            remoteBoard.setEnabledAll(false);

            try {
                UnicastRemoteObject.unexportObject(gm.reg, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (win) {
                status_lbl.setText("Gewonnen!");
            } else {
                status_lbl.setText("Verloren!");
            }
        }

        // #region Remote methods
        /**
        * Schießt auf die übergebenen Koordinaten und gibt an den Client zurück ob ein Schiff getroffen wurde
        * @return True = Schiff getroffen
        */
        public boolean shoot(int x, int y) throws RemoteException {
            boolean shipHit = localBoard.cells[x][y].hasShip;
            localBoard.cells[x][y].gotShot = true;
            localBoard.cells[x][y].repaint();

            if (!shipHit)
                done();

            status_lbl.setText("[" + Coordinate.indexToXCoordinate(x) + " , " + Coordinate.indexToYCoordinate(y)
                    + "] Gegner hat" + (shipHit ? " getroffen." : " verfehlt."));

            try {
                if (isLost()) {
                    gameOver(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return shipHit;
        }

        /**
        * Überprüft ob das Spiel verloren ist und gibt die Antwort an den Client zurück
        * @return True = Verloren / Spiel vorbei
        */
        public boolean isLost() throws RemoteException {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (localBoard.cells[i][j].hasShip && localBoard.cells[i][j].gotShot == false)
                        return false; // Schiff gefunden = noch nicht verloren
                }
            }

            return true; // kein Schiff gefunden = verloren
        }

        /**
        * Spielzüge vorbei (anderer Spieler ist an der Reihe)
        */
        public void done() {
            // Change turn
            yourturn = !yourturn;
            remoteBoard.setEnabledAll(yourturn);
        }

        /**
        * Host ist bereit anzufangen
        */
        public void ready() {
            gm.remReady = true;
            status_lbl.setText("Host ist bereit, starte Spiel.");

            if (gm.ready)
                remoteBoard.setEnabledAll(true);
        }
        // #endregion
    }

}