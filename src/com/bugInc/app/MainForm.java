package com.bugInc.app;

import com.bugInc.core.*;
import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;

//** ** Created by DeveloperHacker ** **//
//* https://github.com/DeveloperHacker *//

public class MainForm extends JFrame {

    private JPanel rootPanel;
    private JButton sendButton;
    private JButton connectButton;
    private JTextField CommandTextField;
    private JTextField DataTextField;
    private JTextField FlagTextField;
    private JTextField IDTextField;
    private JComboBox<String> PortComboBox;
    private JButton updateButton;
    private JComboBox<Integer> baudRateComboBox;

    private MainForm(String title) {
        super(title);
        setContentPane(rootPanel);
        pack();
        setBounds(30, 30, 500, 250);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        baudRateComboBox.addItem(300);
        baudRateComboBox.addItem(1200);
        baudRateComboBox.addItem(2400);
        baudRateComboBox.addItem(4800);
        baudRateComboBox.addItem(9600);
        baudRateComboBox.addItem(19200);
        baudRateComboBox.addItem(38400);
        baudRateComboBox.addItem(57600);
        baudRateComboBox.addItem(74880);
        baudRateComboBox.addItem(115200);
        baudRateComboBox.addItem(230400);
        baudRateComboBox.addItem(250000);
        baudRateComboBox.setSelectedIndex(4);

        final SerialPort[][] ports = {SerialPort.getCommPorts()};
        for (SerialPort port : ports[0]) PortComboBox.addItem(port.getSystemPortName());
        updateButton.addActionListener(e -> {
            ports[0] = SerialPort.getCommPorts();
            PortComboBox.removeAllItems();
            for (SerialPort port : ports[0]) PortComboBox.addItem(port.getSystemPortName());
        });

        Connector connector = new Connector(letter -> {
            new MessageBox("Output", "ID: " + letter.getID()
                    + "\nCOMMAND: " + letter.getCOMMAND()
                    + "\nDATA: " + letter.getDATA()
                    + "\nFLAG: " + letter.getFLAG()).setVisible(true);
            return true;
        }, letter -> {
            new MessageBox("Input", "ID: " + letter.getID()
                    + "\nCOMMAND: " + letter.getCOMMAND()
                    + "\nDATA: " + letter.getDATA()
                    + "\nFLAG: " + letter.getFLAG()).setVisible(true);
            return true;
        });
        final SerialPort[] openPort = {null};
        connectButton.addActionListener(e -> {
            if (ports[0].length != 0) {
                if (openPort[0] == null || !openPort[0].isOpen()) {
                    openPort[0] = SerialPort.getCommPort(PortComboBox.getSelectedItem().toString());
                    openPort[0].setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                    openPort[0].setBaudRate((int) baudRateComboBox.getSelectedItem());
                    if (openPort[0].openPort()) {
                        connectButton.setText("Disconnect");
                        PortComboBox.setEnabled(false);
                        baudRateComboBox.setEnabled(false);
                        updateButton.setEnabled(false);
                        sendButton.setEnabled(true);
                        connector.run(openPort[0]);
                    }
                } else {
                    connectButton.setText("Connect");
                    PortComboBox.setEnabled(true);
                    baudRateComboBox.setEnabled(true);
                    updateButton.setEnabled(true);
                    sendButton.setEnabled(false);
                    openPort[0].closePort();
                    connector.stop();
                }
            }
        });

        connector.addController((byte) 0, "test", (byte) 0, new Geometry(new Vector(0.0, 0.0), 10.0, 10.0));
        sendButton.setEnabled(false);
        sendButton.addActionListener(e -> {
            Byte id = Byte.valueOf(IDTextField.getText());
            Byte command = Byte.valueOf(CommandTextField.getText());
            Byte data = Byte.valueOf(DataTextField.getText());
            Byte flag = Byte.valueOf(FlagTextField.getText());
            connector.send(new Letter(id, command, data, flag));
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainForm("Sender"));
    }
}
