package de.adf;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class MainMenu extends JFrame   {
    
    /**
     * Erzeugt das Hauptmenü des Spiels, mit allen Buttons.
     */
    public MainMenu() {
        setTitle("Schiffe versenken");
        setLayout(new GridBagLayout());
        setSize(1000,650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String resource = "./docs/background.png"; //Pfad anpassen, wenn das Bild richtig gesetzt ist
        JLabel backgroundImage;
        ImageIcon iconShips;
        iconShips = new ImageIcon(getClass().getResource("background.png")); // an die resource anpassen, wenn diese funktioniert
        backgroundImage = new JLabel(iconShips);
        backgroundImage.setBounds(0, 0, 1000, 562);
        add(backgroundImage);

        setVisible(true);

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 0, 10, 0);

        //Buttons neu plazieren

        final JButton start_btn = new JButton("Play");
        //constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        getContentPane().add(start_btn, constraints);
        start_btn.addActionListener(e -> startClicked(e));

        final JButton close_btn = new JButton("Close");
        //constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        getContentPane().add(close_btn, constraints);
        close_btn.addActionListener(e -> closeClicked(e));
    }

    private void startClicked(ActionEvent e) {
        new LobbyWindow();
        setVisible(false);
    }

    private void closeClicked(ActionEvent e) {
        setVisible(false);
        dispose();
        System.exit(0);
    }

}
