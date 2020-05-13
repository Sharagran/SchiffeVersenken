package de.adf;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Spiel extends JPanel {
    public void paintComponent(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillRect(100, 100, 300, 150);
    }
}