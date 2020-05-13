package de.adf;
import java.awt.*;
import javax.swing.JButton;
import javax.swing.JFrame;
public class Oberflaeche {
    
    /**
     * Erzeugt das Hauptmenü des Spiels, mit allen Buttons.
     */
    public static void HauptmenueLayout(){
        JFrame menue = new JFrame("Schiffe versenken");
        GridBagConstraints constraints = new GridBagConstraints();
        menue.setLayout(new GridBagLayout());
        menue.setSize(600,500);
        menue.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        constraints.insets = new Insets(0,0,10,0);
        menue.setVisible(true);

        JButton btnStart = new JButton("Spielen");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        menue.getContentPane().add(btnStart, constraints);

        JButton btnBeenden = new JButton("Beenden");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        menue.getContentPane().add(btnBeenden, constraints);
        

    }

}