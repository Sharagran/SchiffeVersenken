package de.adf;
import java.awt.*;
import javax.swing.*;
public class MainMenu extends JFrame {
    
    /**
     * Erzeugt das Hauptmenü des Spiels, mit allen Buttons.
     */
    public MainMenu() {
        setTitle("Schiffe versenken");
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(new GridBagLayout());
        setSize(600,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        constraints.insets = new Insets(0,0,10,0);
        setVisible(true);

        JButton btnStart = new JButton("Spielen");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        getContentPane().add(btnStart, constraints);

        JButton btnExit = new JButton("Beenden");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        getContentPane().add(btnExit, constraints);
    }
    

}