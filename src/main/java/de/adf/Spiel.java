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

        private Map<String, Color> colors = Map.of(
            "background", Color.white,
            "hit", Color.red,
            "stroke", new Color(218, 218, 255)


        );

        private boolean hasShip = false;
        private boolean gotShot = true;

        public Cell() {
            super();
            setVisible(true);
            setFocusable(false);
            // setBorderPainted(false);
            // setRolloverEnabled(false);
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
            g2.setColor(colors.get("stroke"));
            g2.drawRect(0, 0, getWidth(), getHeight());

            if(hasShip) {

            }

            if(gotShot) {
                g2.setColor(colors.get("hit"));
                g2.drawLine(0, 0, this.getWidth(), this.getHeight());
                g2.drawLine(0, this.getHeight(), this.getWidth(), 0);
            }


            g2.dispose();
        }

        // @Override
        // public void paint(Graphics g) {
        //     super.paint(g);
        // }

        // @Override
        // public void paintChildren(Graphics g) {

        // }
    }
}
