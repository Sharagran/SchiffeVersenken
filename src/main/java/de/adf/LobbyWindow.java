package de.adf;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;

import java.rmi.RemoteException;
import java.util.*;

/**
 * Fenster zum erstellen, suchen und beitreten einer Lobby.
 */
public class LobbyWindow extends JFrame {

    private DefaultListModel ip_ListModel;
    private JTextField ip_text;
    private JList ip_lst;
    private JButton refresh_btn;
    private JButton join_btn;
    private JButton host_btn;
    private GameManager gm;
    private ArrayList<String> localAddresses;

    public LobbyWindow() {
        initUI();
        setVisible(true);

        localAddresses = new ArrayList<String>();
        setAddresses();
    }

    /**
     * Initialisiere das Nutzerinterface.
     */
    private void initUI() {
        setSize(400, 300);
        setTitle("Lobby");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        Container pane = getContentPane();

        ip_ListModel = new DefaultListModel();

        ip_text = new JTextField();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridwidth = 3;
        pane.add(ip_text, gbc);
        ip_text.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {

            }

            public void removeUpdate(DocumentEvent e) {

            }

            public void insertUpdate(DocumentEvent e) {
                join_btn.setEnabled(ip_text.getText().matches("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$"));
            }
        });

        ip_lst = new JList(ip_ListModel);
        ip_lst.setEnabled(false);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 0;
        gbc.gridy = 1;
        pane.add(ip_lst, gbc);
        ip_lst.addListSelectionListener(e -> ListSelectionChanged(e));

        host_btn = new JButton("Host");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LAST_LINE_START;
        pane.add(host_btn, gbc);
        host_btn.addActionListener(e -> {
            try {
                hostClicked(e);
            } catch (RemoteException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        });

        refresh_btn = new JButton(Character.toString(128472));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.PAGE_END;
        pane.add(refresh_btn, gbc);
        refresh_btn.addActionListener(e -> refreshList(e));

        join_btn = new JButton("Join");
        join_btn.setEnabled(false);
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LAST_LINE_END;
        pane.add(join_btn, gbc);
        join_btn.addActionListener(e -> {
            try {
                joinClicked(e);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        });
    }

    /**
     * Wird ausgeführt, wenn ein Element in der Liste ausgewählt wurde.
     * 
     * @param e Eventarg der Liste
     */
    private void ListSelectionChanged(ListSelectionEvent e) {
        ip_text.setText((String) ip_lst.getSelectedValue());
        join_btn.setEnabled(true);
    }

    /**
     * Wird ausgeführt, wenn ein der refresh button geklickt wird.
     * 
     * @param e Eventarg des Buttons
     */
    private void refreshList(ActionEvent e) {
        System.out.println("Starting Discover.");
        System.out.println("----------------------------------");
        refresh_btn.setText("Searching ...");
        refresh_btn.setEnabled(false);
        ip_lst.setEnabled(false);
        Runnable r = new ClientDiscover();
        SwingUtilities.invokeLater(r);
    }

    /**
     * Wird ausgeführt, wenn ein der join button geklickt wird.
     * 
     * @param e Eventarg des Buttons
     */
    private void joinClicked(ActionEvent ev) throws RemoteException {
        String ip = ip_text.getText();
        gm = new GameManager(ip);
        new GameWindow(gm);
    }

    /**
     * Wird ausgeführt, wenn ein der host button geklickt wird.
     * 
     * @param e Eventarg des Buttons
     */
    private void hostClicked(ActionEvent e) throws RemoteException {
        gm = new GameManager();
        new GameWindow(gm);
    }

    /**
     * Überprüft, ob ein host auf einen bestimmten Port hört.
     * 
     * @param host Zu prüfende Adresse.
     * @param port Zu prüfender Port.
     */
    public boolean serverListening(String host, int port) {
        Socket s = null;
        try {
            s = new Socket();
            s.connect(new InetSocketAddress(host, port), 10);
            s.close();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (Exception e) {

                }
            }
        }
    }

    public void setAddresses() {
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
    }

    /**
     * Entdeckt alle Clients im Local Area Network in einem seperaten Thread. Nur
     * für Netze mit einer CIDR von 24.
     */
    public class ClientDiscover implements Runnable {
        /**
         * Wird ausgeführt, wenn ein der refresh button geklickt wird.
         */
        public void run() {
            try {
                for (String addr : localAddresses) {
                    addr = addr.substring(0, addr.lastIndexOf('.') + 1);
                    System.out.println(String.format("Searching in \t%s0-254 ", addr));
                    for (int i = 1; i <= 254; i++) {
                        String currentIP = addr.concat(Integer.toString(i));
                        if (serverListening(currentIP, GameManager.PORT) && !ip_ListModel.contains(currentIP)) {
                            System.out.println("IP found -> \t" + currentIP);

                            ip_ListModel.addElement(currentIP);
                        } else if (!serverListening(currentIP, GameManager.PORT) && ip_ListModel.contains(currentIP)) {
                            ip_ListModel.removeElement(currentIP);
                        }
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            refresh_btn.setText(Character.toString(128472));
            refresh_btn.setEnabled(true);
            ip_lst.setEnabled(true);

            System.out.println("----------------------------------");
            System.out.println("Discover Finished.");
        }

    }
}
