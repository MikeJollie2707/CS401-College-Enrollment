package client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.*;

import objects.BodyLogin;
import objects.ClientMsg;
import objects.ServerMsg;

public class GUI {
    private JFrame frame;
    private JPanel loginScreen;
    private JPanel optionsPanel;
    private JPanel mainPanel;
    private boolean loggedIn = false;
    private Socket socket;
    private ObjectOutputStream ostream;
    private ObjectInputStream istream;
    private String[] universities;
    private JComboBox<String> uniBox;
    private BodyLogin loggedUser;

    public GUI(Socket socket, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.socket = socket;
        this.ostream = ostream;
        this.istream = istream;
        frame = new JFrame("College Enrollment System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 750);
        // starts the gui at the center of the screen
        frame.setLocationRelativeTo(null);
        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new CardLayout());
        // saves the universities in case the user logs out to not rely on server sending uni names every time
        saveUniversities();
        // initializeLogin() handles user log in before reaching optionsPanel
        // example to test user log in for students: use uni_name: CSU East Bay,loginID: steve, password: iamsteve
        initializeLogin();
        frame.setVisible(true);
    }

    public void saveUniversities() {
        // universities need to be saved since user can logout and don't want server to
        // send again, select from uni menu instead of tediously typing out uni_name
        uniBox = new JComboBox<>();
        try {
            universities = (String[]) istream.readObject();
            for (int i = 0; i < universities.length; i++) {
                uniBox.addItem(universities[i]);
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void initializeLogin() {
        loginScreen = new JPanel();
        loginScreen.setLayout(new BoxLayout(loginScreen, BoxLayout.Y_AXIS));
        JLabel loginMessage = new JLabel("Please login first!");
        loginMessage.setFont(new Font("Arial", Font.BOLD, 30));
        loginMessage.setForeground(Color.BLUE);

        JLabel uni = new JLabel("University name: ");
        JLabel log = new JLabel("loginID: ");
        JLabel pass = new JLabel("password:");
        JTextField loginBox = new JTextField(20);
        JTextField passwordBox = new JTextField(20);

        JButton login = new JButton("LOGIN");
        login.setBackground(Color.GREEN);
        login.setForeground(Color.BLACK);

        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("BUTTON IS CLICKED");
                String uni_name = (String) uniBox.getSelectedItem();
                String loginID = loginBox.getText();
                String password = passwordBox.getText();

                BodyLogin user = new BodyLogin(uni_name, loginID, password);
                ClientMsg loginAttempt = new ClientMsg("CREATE", "login", user);
                try {
                    ostream.writeObject(loginAttempt);
                    ServerMsg serverMsg = (ServerMsg) istream.readObject();
                    if (serverMsg.isOk()) {
                        loggedIn = true;
                        mainPanel.remove(loginScreen);
                        frame.remove(mainPanel);
                        frame.revalidate();
                        frame.repaint();
                        loggedUser = user;
                        openMainGUI();
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "Login attempt did not work, please try again");
                    }

                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

        });
        loginScreen.add(loginMessage);
        // added spacing after loginMessage and boxes to make login look cleaner
        loginScreen.add(Box.createVerticalStrut(100));
        loginScreen.add(uni);
        loginScreen.add(uniBox);
        loginScreen.add(Box.createVerticalStrut(10));
        loginScreen.add(log);
        loginScreen.add(loginBox);
        loginScreen.add(Box.createVerticalStrut(10));
        loginScreen.add(pass);
        loginScreen.add(passwordBox);
        loginScreen.add(Box.createVerticalStrut(10));
        loginScreen.add(login);

        mainPanel.add(loginScreen);
        frame.add(mainPanel);
        frame.revalidate();
        frame.repaint();
    }
    // if logged in: access the college enrollment system GUI
    void openMainGUI() {
        mainPanel = new JPanel();
        JOptionPane.showMessageDialog(mainPanel, "Logged in!!!!!!!!!!!!!");
        JPanel logoutPan = new JPanel();
        JButton logout = new JButton("LOGOUT");
        logout.setBackground(Color.RED);
        logout.setForeground(Color.BLACK);
        initializeOptions();
        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loggedIn = false;
                mainPanel.removeAll();
                frame.getContentPane().removeAll();
                frame.revalidate();
                frame.repaint();
                ClientMsg logoutAttempt = new ClientMsg("CREATE", "logout", loggedUser);
                try {
                    ostream.writeObject(logoutAttempt);
                    ServerMsg serverMsg = (ServerMsg) istream.readObject();
                    System.out.println("SERVER MESSAGE LOGOUT STATUS: " + serverMsg.isOk());
                    if (serverMsg.isOk()) {
                        initializeLogin();
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        logoutPan.add(logout);
        mainPanel.add(logoutPan);
        frame.add(mainPanel);
        frame.revalidate();
        frame.repaint();
    }
    // the options and listeners of the main GUI after logging in
    void initializeOptions() {

    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

}