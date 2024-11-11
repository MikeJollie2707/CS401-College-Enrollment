package client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.*;

public class GUI {
    private JFrame frame;
    private JPanel loginScreen;
    private JPanel optionsPanel;
    private JPanel mainPanel;
    private boolean loggedIn = false;
    private Socket socket;
    private ObjectOutputStream ostream;
    private ObjectInputStream istream;

    public GUI(Socket socket, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.socket = socket;
        this.ostream = ostream;
        this.istream = istream;
        frame = new JFrame("College Enrollment System");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 750);
        mainPanel = new JPanel();
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new CardLayout());
        loginScreen = new JPanel();
        loginScreen.setLayout(new BoxLayout(loginScreen, BoxLayout.Y_AXIS));

        JLabel loginMessage = new JLabel("Please login!");
        loginMessage.setSize(400, 400);
        loginMessage.setForeground(Color.BLUE);
        JLabel uni = new JLabel("University name: ");
        JLabel log = new JLabel("loginID: ");
        JLabel pass = new JLabel("password:");
        JTextField uniBox = new JTextField(20);
        JTextField loginBox = new JTextField(20);
        JTextField passwordBox = new JTextField(20);
        loginScreen.add(uni);
        loginScreen.add(uniBox);
        loginScreen.add(log);
        loginScreen.add(loginBox);
        loginScreen.add(pass);
        loginScreen.add(passwordBox);

        JButton login = new JButton("LOGIN");
        login.setBackground(Color.GREEN);
        login.setForeground(Color.BLACK);
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String uni_name = uniBox.getText();
                String loginID = loginBox.getText();
                String password = passwordBox.getText();
                /*
                 * if(uni_name, loginID, password) { loggedIn = true;
                 * mainPanel.remove(loginScreen); }
                 */
            }
        });
        loginScreen.add(login);
        loginScreen.add(loginMessage);
        mainPanel.add(loginScreen);
        frame.add(mainPanel);
        frame.setVisible(true);

        // logged in:

        // frame.add(optionsPanel);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

}
