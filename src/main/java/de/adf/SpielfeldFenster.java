package de.adf;
import java.awt.*;
import javax.swing.*;

public class SpielfeldFenster extends JFrame {
    public SpielfeldFenster() {
        setTitle("Schiffe versenken");
        setSize(600,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        this.setContentPane(new Spiel());
    }
}