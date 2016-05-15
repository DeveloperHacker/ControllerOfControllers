package com.bugInc.app;

import com.bugInc.core.*;
import com.bugInc.math.Figure;
import com.bugInc.math.Vector;
import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.util.Objects;

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
    private JButton chatButton;
    private JComboBox<String> ParityComboBox;
    private JComboBox<Integer> StopBitsComboBox;

    private ChatForm chat = null;

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

        StopBitsComboBox.addItem(1);
        StopBitsComboBox.addItem(2);
        StopBitsComboBox.setSelectedIndex(1);

        ParityComboBox.addItem("none");
        ParityComboBox.addItem("odd");
        ParityComboBox.addItem("even");
        ParityComboBox.addItem("mark");
        ParityComboBox.addItem("space");
        ParityComboBox.setSelectedIndex(2);

        final SerialPort[][] ports = {SerialPort.getCommPorts()};
        for (SerialPort port : ports[0]) PortComboBox.addItem(port.getSystemPortName());
        updateButton.addActionListener(e -> {
            ports[0] = SerialPort.getCommPorts();
            PortComboBox.removeAllItems();
            for (SerialPort port : ports[0]) PortComboBox.addItem(port.getSystemPortName());
        });

        Connector connector = new Connector(letter -> {
            String message = "ID: " + Character.toString((char) letter.getID())
                    + "\nCOMMAND: " + Character.toString((char) letter.getCOMMAND())
                    + "\nDATA: " + Character.toString((char) letter.getDATA());
            return new MessageBox("Input", message);
        }, b -> {
            if (chat != null) chat.add(Character.toString((char) b.byteValue()));
            return true;
        });
        final SerialPort[] openPort = {null};
        connectButton.addActionListener(e -> {
            if (ports[0].length != 0) {
                if (openPort[0] == null || !openPort[0].isOpen()) {
                    openPort[0] = SerialPort.getCommPort(PortComboBox.getSelectedItem().toString());
                    openPort[0].setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                    openPort[0].setBaudRate((int) baudRateComboBox.getSelectedItem());
                    openPort[0].setNumStopBits((int) StopBitsComboBox.getSelectedItem());
                    openPort[0].setParity(getParity());
                    if (openPort[0].openPort()) {
                        connectButton.setText("Disconnect");
                        PortComboBox.setEnabled(false);
                        baudRateComboBox.setEnabled(false);
                        updateButton.setEnabled(false);
                        ParityComboBox.setEnabled(false);
                        StopBitsComboBox.setEnabled(false);
                        sendButton.setEnabled(true);
                        chatButton.setEnabled(true);
                        connector.run(openPort[0]);
                    }
                } else {
                    connectButton.setText("Connect");
                    PortComboBox.setEnabled(true);
                    baudRateComboBox.setEnabled(true);
                    updateButton.setEnabled(true);
                    ParityComboBox.setEnabled(true);
                    StopBitsComboBox.setEnabled(true);
                    sendButton.setEnabled(false);
                    chatButton.setEnabled(false);
                    if (chat != null) chat.dispose();
                    openPort[0].closePort();
                    connector.stop();
                }
            }
        });

        connector.addController((byte) 0, "test", (byte) 0, new Figure(new Vector(0.0, 0.0), 10.0, 10.0, 0.01));
        sendButton.setEnabled(false);
        sendButton.addActionListener(e -> {
            try {
                Byte id = IDTextField.getText().getBytes()[0];
                Byte command = CommandTextField.getText().getBytes()[0];
                Byte data = DataTextField.getText().getBytes()[0];
                connector.send(new Letter(id, command, data));
            } catch (Exception error) {
                new MessageBox("Error", error.toString());
            }
        });

        chatButton.setEnabled(false);
        chatButton.addActionListener(e -> {
            if (chat != null) chat.dispose();
            chat = new ChatForm("Chat", connector);
        });
    }

    private int getParity() {
        Object item = ParityComboBox.getSelectedItem();
        if (Objects.equals(item, "odd")) return SerialPort.ODD_PARITY;
        else if (Objects.equals(item, "even")) return SerialPort.EVEN_PARITY;
        else if (Objects.equals(item, "mark")) return SerialPort.MARK_PARITY;
        else if (Objects.equals(item, "space")) return SerialPort.SPACE_PARITY;
        else return SerialPort.NO_PARITY;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainForm("Sender"));
    }
}
