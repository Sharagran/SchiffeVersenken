package de.adf;
import java.awt.*;
import java.rmi.RemoteException;

import javax.swing.*;

public class GameWindow extends JFrame {
    GameManager gm;

    public GameWindow(String ip) throws RemoteException {
        setTitle("Schiffe versenken");
        setSize(1000,630);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        if (ip != null) {
            gm = new GameManager(ip); //Joining
            System.out.println(gm.remote.isLost()); //FIXME: debug
        }
        else {
            gm = new GameManager(); //Hosting (gm.isHost)
        }

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 50, 0, 50);

        add(new GameBoard(), gbc);
        add(new GameBoard(), gbc);
        
        validate();
        repaint();
    }
}