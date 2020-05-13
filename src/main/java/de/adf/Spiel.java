package de.adf;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Spiel extends JPanel {
    public Spiel() {
        setLayout(new GridLayout(10,10));
        setBackground(Color.pink);
        setPreferredSize(new Dimension(500,500));

        generateBoard();
    }

    private void generateBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Cell cell = new Cell();
                cell.setPreferredSize(new Dimension(32,32));
                add(cell);
            }
        }
    }

    private class Cell extends JButton {

        Map<String, Color> colors = Map.of(
            "background", Color.white,
            "hit", Color.red.brighter(),
            "stroke", new Color(218, 218, 255)


        );

        public Cell() {
            super();
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(249,250,252));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setStroke(new BasicStroke(4));
            g2.setColor(colors.get("stroke"));
            g2.drawRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
