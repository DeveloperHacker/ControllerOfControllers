package com.bugInc.app;

import com.bugInc.core.Connector;

import javax.swing.*;

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
                byte b = Byte.valueOf(message);
                LogTextPane.setText(LogTextPane.getText() + "> " + message + "\n");
                connector.send(b);
            } catch (Exception ignore) {
                LogTextPane.setText(LogTextPane.getText() + "> Message " + message + " is not byte\n");
            }
        });
    }

    void add(String message) {
        LogTextPane.setText(LogTextPane.getText() + "< " + message + "\n");
    }
}
