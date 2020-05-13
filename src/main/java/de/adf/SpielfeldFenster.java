package de.adf;
import java.awt.*;
import javax.swing.*;

public class SpielfeldFenster extends JFrame {
    public SpielfeldFenster() {
        setTitle("Schiffe versenken");
        setSize(600,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        setLayout(new BorderLayout());
        add(new Spiel(), BorderLayout.CENTER);
        JPanel right = new JPanel();
        right.add(new JLabel("test"));
        add(right, BorderLayout.EAST);
        add(new JPanel(), BorderLayout.WEST);
        validate();
        repaint();
    }
}