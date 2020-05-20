package de.adf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GameBoard extends JPanel {
    public GameBoard() {
        setLayout(new GridLayout(10, 10));
        setPreferredSize(new Dimension(500, 500));

        generateBoard();
    }

    private void generateBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Cell cell = new Cell();
                cell.setPreferredSize(new Dimension(32, 32));
                add(cell);
            }
        }
    }

    private class Cell extends JButton {

        private Map<String, Color> colors = Map.of(
            "background", Color.white,
            "hit", Color.red
        );

        private boolean hasShip = Math.random() > 0.5;
        private boolean gotShot = false;

        public Cell() {
            super();
            setFocusable(false);

            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gotShot = true; //FIXME: demo only
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

            g2.setColor(colors.get("background"));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setStroke(new BasicStroke(4));


            if (gotShot) {
                g2.setColor(colors.get("hit"));
                if(hasShip) {
                    int padding = 10;
                    g2.drawLine(0 + padding, 0 + padding, getWidth() - padding, getHeight() - padding);
                    g2.drawLine(0+ padding, getHeight() - padding, getWidth() - padding, 0 + padding);
                } else {
                    int size = 10;
                    g2.fillArc(getWidth() / 2 - size / 2, getHeight() / 2 - size / 2, size, size, 0, 360);
                }
                
            }

            g2.dispose();
        }
    }
}
