package de.adf;
import java.awt.*;
import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Schiffe versenken");
        setSize(600,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 50, 0, 50);

        add(new GameBoard(), gbc);
        add(new GameBoard(), gbc);
        
        validate();
        repaint();
    }
}