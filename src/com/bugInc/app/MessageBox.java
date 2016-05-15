package com.bugInc.app;

import javax.swing.*;

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

public class MessageBox extends JFrame {

    private JPanel rootPanel;
    private JTextPane textPane1;
    private JButton ОКButton;

    MessageBox(String title, String message) {
        super(title);
        setContentPane(rootPanel);
        pack();
        setBounds(200, 200, 300, 120);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        textPane1.setText(message);

        ОКButton.addActionListener( e -> MessageBox.this.dispose());
    }
}
