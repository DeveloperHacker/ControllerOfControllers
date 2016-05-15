package com.bugInc.app;

import com.bugInc.core.Connector;

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ChatForm extends JFrame {
    private JButton sendButton;
    private JTextPane LogTextPane;
    private JTextField OutputTextPane;
    private JPanel rootPanel;

    ChatForm(String title, Connector connector) {
        super(title);
        setContentPane(rootPanel);
        pack();
        setBounds(100, 100, 500, 250);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        sendButton.addActionListener(e -> {
            String message = OutputTextPane.getText();
            try {
                LogTextPane.setText(LogTextPane.getText() + "> " + message + "\n");
                for (Byte b : message.getBytes()) connector.send(b);
            } catch (Exception error) {
                new MessageBox("Error", error.toString());
            }
        });

        OutputTextPane.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }


            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick();
                    OutputTextPane.setText("");
                }
            }
        });
    }

    void add(String message) {
        LogTextPane.setText(LogTextPane.getText() + "< " + message + "\n");
    }
}
