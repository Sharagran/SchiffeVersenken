package de.adf;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class MainMenu extends JFrame {

    /**
     * Erzeugt das Hauptmenü des Spiels, mit allen Buttons.
     */
    public MainMenu() {
        setTitle("Schiffe versenken");
        setLayout(new GridBagLayout());
        setSize(Settings.SCREENWIDTH, Settings.SCREENHEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel backgroundImage;
        ImageIcon iconShips;
        iconShips = new ImageIcon("resources/background.png");
        backgroundImage = new JLabel(iconShips);
        backgroundImage.setBounds(0, 0, 1000, 562);
        add(backgroundImage);


        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 0, 0, 0);

        final JButton start_btn = new JButton("Spiel starten");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 2;
        constraints.weighty = 4;
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        getContentPane().add(start_btn, constraints);
        start_btn.addActionListener(e -> startClicked(e));

        final JButton close_btn = new JButton("Schließen");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 2;
        constraints.weighty = 4;
        constraints.anchor = GridBagConstraints.LAST_LINE_START;
        getContentPane().add(close_btn, constraints);
        close_btn.addActionListener(e -> closeClicked(e));

        setVisible(true);
    }

    private void startClicked(ActionEvent e) {
        new LobbyWindow();
        setVisible(false);
    }

    private void closeClicked(ActionEvent e) {
        setVisible(false);
        dispose();
        System.exit(0);
    }

}
