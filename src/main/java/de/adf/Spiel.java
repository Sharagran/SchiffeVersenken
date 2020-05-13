package de.adf;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Spiel extends JPanel {
    public Spiel() {

        Cell btnStart = new Cell();
        add(btnStart);


        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                System.out.println(me.getPoint());
            }
        });
    }

    public void paintComponent(Graphics g) {
        drawGrid(32, 32);

        g.setColor(Color.ORANGE);
        g.fillRect(100, 100, 300, 150);
    }

    private void drawGrid(int width, int heigth) {
        // g.setColor(Color.black);

    }

    private Point bekommePositionImRaster() {
        return null;
    }

    private class Cell extends JButton {

        public Cell() {
            super();
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(new Point(0, 0), Color.RED, new Point(0, getHeight()), Color.BLUE));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.setPaint(Color.BLACK);
            g2.drawString(getText(), 30, 12);
            g2.dispose();

            // super.paintComponent(g);
        }
    }
}
