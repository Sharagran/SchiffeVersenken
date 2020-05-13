package de.adf;

import javax.swing.*;
import java.awt.*;

/**
 * EntdeckerMenue
 */
public class EntdeckerMenue extends JFrame {

    public EntdeckerMenue() {
        initUI();
        setVisible(true);
    }

    /**
     * initialisiere das Nutzerinterface.
     */
    private void initUI() {
        setSize(400, 300);
        setTitle("EntdeckerMenue");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        Container pane = getContentPane();

        final DefaultListModel lst = new DefaultListModel();
        lst.add(0,"platzhalter1");
        lst.add(1,"platzhalter2");
        lst.add(2,"platzhalter3");

        JList jl = new JList(lst);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 0;
        pane.add(jl, gbc);

        JButton host_btn = new JButton("Host");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LAST_LINE_START;
        pane.add(host_btn, gbc);

        JButton refresh_btn = new JButton(Character.toString(128472));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.PAGE_END;
        pane.add(refresh_btn, gbc);

        JButton join_btn = new JButton("Join");
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LAST_LINE_END;
        pane.add(join_btn, gbc);
    }    
}