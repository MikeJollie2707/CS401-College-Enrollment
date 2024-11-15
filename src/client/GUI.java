package client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
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
        // initializeLogin() handles user log in before reaching optionsPanel
        // example to test user log in for students: use uni_name: CSU East Bay, loginID: steve, password: iamsteve
        initializeLogin();
        mainPanel.add(loginScreen);
        frame.add(mainPanel);
        frame.setVisible(true);
        // if logged in:
        // add optionsPanel stuff
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
        // select from uni menu instead of tediously typing out uni_name
        JComboBox<String> uniBox = new JComboBox<>();
        String[] universities;
        try {
            universities = (String[]) istream.readObject();
            for (int i = 0;i < universities.length;i++) {
                uniBox.addItem(universities[i]);
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      
        JButton login = new JButton("LOGIN");
        login.setBackground(Color.GREEN);
        login.setForeground(Color.BLACK);
        
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("BUTTON IS CLICKED");
                String uni_name = (String) uniBox.getSelectedItem();
                String loginID = loginBox.getText();
                String password = passwordBox.getText();
                System.out.println("USER INFO REACHED FOR EXAMPLE loginID = " + loginID);
                
                BodyLogin user = new BodyLogin(uni_name, loginID, password);
                ClientMsg loginAttempt = new ClientMsg("CREATE", "login", loginID, user);
                System.out.println("Reached the user and loginAttempt ");
                try {
                    ostream.writeObject(loginAttempt);
                    System.out.println("BEFORE SERVER MSG");
                    ServerMsg serverMsg = (ServerMsg) istream.readObject();
                    System.out.println("AFTER SERVER MSG");
                    System.out.println("SERVER MESSAGE RETURNED STATUS: " + serverMsg.isOk());
                    
                    if (serverMsg.isOk()) {
                        loggedIn = true;
                        frame.remove(mainPanel);
                        frame.add(optionsPanel);
                        optionsPanel.setBackground(Color.GREEN);
                        // frame needs to revalidate to show the correct panels on frame after those changes
                        frame.revalidate();
                        JOptionPane.showMessageDialog(optionsPanel, "Logged in!!!!!!!!!!!!!");
                    } else {
                        System.out.println("server response is not OK");
                        JOptionPane.showMessageDialog(mainPanel, "Login attempt did not work, please try again");
                    }

                } catch (Exception err) {
                    System.out.println("couldnt send to server");
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
        
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

}