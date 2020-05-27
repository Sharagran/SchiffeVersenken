package de.adf;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 * LobbyWindow
 */
public class LobbyWindow extends JFrame {

   private DefaultListModel ip_ListModel;
   private JTextField ip_text;
   private JList ip_lst;


    public LobbyWindow() {
        initUI();
        setVisible(true);
    }

    /**
     * initialisiere das Nutzerinterface.
     */
    private void initUI() {
        setSize(400, 300);
        setTitle("Lobby");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
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

        ip_lst = new JList(ip_ListModel);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 0;
        gbc.gridy = 1;
        pane.add(ip_lst, gbc);
        ip_lst.addListSelectionListener(e -> ListSelectionChanged(e));

        JButton host_btn = new JButton("Host");
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
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        JButton refresh_btn = new JButton(Character.toString(128472));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.PAGE_END;
        pane.add(refresh_btn, gbc);
        refresh_btn.addActionListener(e -> refreshList(e));

        JButton join_btn = new JButton("Join");
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LAST_LINE_END;
        pane.add(join_btn, gbc);
        join_btn.addActionListener(e -> {
            try {
                joinClicked(e);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
    }

    private void ListSelectionChanged(ListSelectionEvent e) {
        ip_text.setText((String) ip_lst.getSelectedValue());
    }

    private void refreshList(ActionEvent e) {
        try {
            String hostip = InetAddress.getLocalHost().getHostAddress();
            hostip = hostip.substring(0, hostip.lastIndexOf('.') + 1);

            for (int i = 1; i <= 254; i++) {
                String currentIP = hostip.concat(Integer.toString(i));
                if(serverListening(currentIP, 80)) {
                    ip_ListModel.addElement(currentIP);
                    System.out.println(currentIP);
                }
            }

            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public boolean serverListening(String host, int port)
    {
        Socket s = null;
        try
        {
            s = new Socket();
            s.connect(new InetSocketAddress(host, port), 10);
            s.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            if(s != null) {
                try {
                    s.close();
                }
                catch(Exception e){

                }
            } 
        }
    }  

    private void joinClicked(ActionEvent e) throws RemoteException {
        String ip = ip_text.getText();
        new GameManager(ip);
    }

    private void hostClicked(ActionEvent e) throws RemoteException {
        String ip = "127.0.0.1";
        new GameManager(ip);
    }
}
