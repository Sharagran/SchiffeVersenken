package de.adf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GameBoard extends JPanel {

    public GameBoard() {
        setLayout(new GridLayout(11, 11));
        setPreferredSize(new Dimension(500, 500));

        generateBoard();
    }

    private void generateBoard() {
        // Empty top left corner
        JLabel empty = new JLabel();
        add(empty);
        for (int i = 1; i <= 10; i++) {
            // Label Y axis (numbers)
            JLabel number = new JLabel(Integer.toString(i));
            number.setHorizontalAlignment(SwingConstants.CENTER);
            add(number);
        }
        

        for (int i = 1; i < 11; i++) {
            // Label X axis (letters)
            JLabel letter = new JLabel(Character.toString(i + 64));
            letter.setVerticalAlignment(SwingConstants.CENTER);
            add(letter);

            // Cells
            for (int j = 1; j < 11; j++) {
                Cell cell = new Cell(i-1, j-1); 
                cell.setPreferredSize(new Dimension(32, 32));
                add(cell);
            }
        }
    }

    private class Cell extends JButton {

        private final Map<String, Color> colors = Map.of("background", Color.white, "hit", Color.red);

        private boolean hasShip = Math.random() > 0.5;
        private boolean gotShot = false;
        int x_coordinate;
        int y_coordinate;

        public Cell(int x, int y) {
            super();
            setFocusable(false);

            x_coordinate = x;
            y_coordinate = y;

            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gotShot = true;
                    try {
                        //hasShip = gm.shoot(x, y); //TODO: muss verändert werden
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
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
                if (hasShip) {
                    int padding = 10;
                    g2.drawLine(0 + padding, 0 + padding, getWidth() - padding, getHeight() - padding);
                    g2.drawLine(0 + padding, getHeight() - padding, getWidth() - padding, 0 + padding);
                } else {
                    int size = 10;
                    g2.fillArc(getWidth() / 2 - size / 2, getHeight() / 2 - size / 2, size, size, 0, 360);
                }

            }

            g2.dispose();
        }
    }
}
