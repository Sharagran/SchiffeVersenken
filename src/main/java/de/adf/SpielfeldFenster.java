package de.adf;
import java.awt.*;
import javax.swing.*;

public class SpielfeldFenster extends JFrame {
    public SpielfeldFenster() {
        setTitle("Schiffe versenken");
        setSize(600,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(new GridBagLayout());
        constraints.insets = new Insets(0,0,10,0);
        

        getContentPane().add(new Spiel(), constraints);
    }
}