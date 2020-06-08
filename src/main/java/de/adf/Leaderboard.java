package de.adf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class Leaderboard extends JFrame {

    ArrayList<LeaderboardEntry> entries;
    private  JLabel player_1;
    private  JLabel player_2;
    private  JLabel wins;;
    private  JButton back_btn;


    public Leaderboard() {
        entries = new ArrayList<>();
    }

    public Leaderboard(String path) {
        entries = loadEntries(path);
    }

    
    public void generateLayout(){
        setTitle("Leaderbord");
        setLayout(new GridBagLayout());
        setSize(Settings.SCREENWIDTH, Settings.SCREENHEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        Container pane = getContentPane();

        player_1 = new JLabel("Spieler 1");
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        pane.add(player_1, gbc);

        player_2 = new JLabel("Spieler 2");
        gbc.anchor = GridBagConstraints.FIRST_LINE_END;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 0;
        pane.add(player_2, gbc);

        wins = new JLabel("Anzahl der Siege:");
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 1;
        pane.add(wins, gbc);

        back_btn = new JButton("Zurück");
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 3;
        pane.add(back_btn, gbc);
        getContentPane().add(back_btn, gbc);
        back_btn.addActionListener(e -> returnClicked(e));
        setVisible(true);
    }

    private void returnClicked(ActionEvent e) {
        setVisible(false);
        new MainMenu();
    }


    public void addEntry(LeaderboardEntry e) {
        entries.add(e);
    }

    public void updateWins(String Playername, int wins) {
        for (LeaderboardEntry item : entries) {
            if(item.getPlayername().equals(Playername)) {
                item.wins = wins;
            }
        }
    }


    public void saveEntries(String path) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(entries);
            out.close();
            fileOut.close();
            System.out.printf("Leaderboard saved");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public ArrayList<LeaderboardEntry> loadEntries(String path) {
        ArrayList<LeaderboardEntry> loadedEntries = new ArrayList<>();
        try {
            File sfile = new File(path);
            FileInputStream fis = new FileInputStream(sfile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            loadedEntries = (ArrayList<LeaderboardEntry>) ois.readObject();
            fis.close();
            ois.close();
        } catch (IOException e) {
            System.out.println("Fehler bei der Deserialisierung");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loadedEntries;
    }


    /**
     * LeaderboardEntry
     */
    public class LeaderboardEntry implements java.io.Serializable {
        private String name;
        public int wins;

        public LeaderboardEntry(String Playername, int wins) {
            this.name = Playername;
            this.wins = wins;
        }

        public String getPlayername() {
            return name;
        }
    }

}