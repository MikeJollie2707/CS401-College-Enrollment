package client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.*;

import objects.BodyLogin;
import objects.ClientMsg;
import objects.Section;
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
    private CardLayout cards;
    private JPanel schedulePanel;
    
    public GUI(Socket socket, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.socket = socket;
        this.ostream = ostream;
        this.istream = istream;
        frame = new JFrame("College Enrollment System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    socket.close();
                } catch (IOException err) {
                    System.err.println("Failed to close socket.");
                    err.printStackTrace();
                }
            }
        });
        frame.setSize(1000, 750);
        // starts the gui at the center of the screen
        frame.setLocationRelativeTo(null);
        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());
        // saves the universities in case the user logs out to not rely on server
        // sending uni names every time
        saveUniversities();
        // initializeLogin() handles user log in before reaching optionsPanel
        // example to test user log in for students: use uni_name: CSU East Bay,loginID:
        // steve, password: iamsteve
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
                        mainPanel.removeAll();
                        frame.getContentPane().removeAll();
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
        mainPanel = new JPanel(new FlowLayout()); 
        JOptionPane.showMessageDialog(mainPanel, "Logged in!!!!!!!!!!!!!");

        JLabel templateStart = new JLabel("Please click an option to start");
        templateStart.setFont(new Font("Arial", Font.BOLD, 30));
        templateStart.setForeground(Color.BLUE);
        
        cards = new CardLayout();
        optionsPanel = new JPanel(cards);
        
       

        schedulePanel = new JPanel(new BorderLayout());
        
        
        JPanel searchClassPanel = new JPanel();
        searchClassPanel.setLayout(new BoxLayout(searchClassPanel, BoxLayout.Y_AXIS));
        
        JLabel courseNameLabel = new JLabel("Course Name: ");
        JLabel courseNumberLabel = new JLabel("Course Number: ");
        JLabel coursePrefixLabel = new JLabel("Course Prefix: ");
        JLabel instructorTextLabel =  new JLabel("Instructor Name: ");
        
        
        JTextField courseNameText = new JTextField(20);
        JTextField courseNumberText = new JTextField(20);
        JTextField coursePrefix = new JTextField(20);
        JTextField instructorText = new JTextField(20);
        
        JButton searchButton = new JButton ("SEARCH");
        
        searchClassPanel.add(courseNameLabel);
        searchClassPanel.add(courseNameText);
        searchClassPanel.add(Box.createVerticalStrut(10));
        searchClassPanel.add(courseNumberLabel);
        searchClassPanel.add(courseNumberText);
        searchClassPanel.add(Box.createVerticalStrut(10));
        searchClassPanel.add(coursePrefixLabel);
        searchClassPanel.add(coursePrefix);
        searchClassPanel.add(Box.createVerticalStrut(10));
        searchClassPanel.add(instructorTextLabel);
        searchClassPanel.add(instructorText);
        searchClassPanel.add(Box.createVerticalStrut(10));
        searchClassPanel.add(searchButton);
        
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String course_name_query = courseNameText.getText();
                String course_number_query = courseNumberText.getText();
                String course_prefix_query = coursePrefix.getText();
                String instructor_query = instructorText.getText();
                
                
            }
        });
        JPanel courseCatalogPanel = new JPanel();
        //courses endpoint not done yet
        String[] titles = {"Course Prefix", "Course Number", "Course Description", "Course Prerequisites", "Course Credits"};
        Object[][] data = { {} };
        JTable courses = new JTable(data, titles);
        
        
        
        
        optionsPanel.add(templateStart, "start");
        optionsPanel.add(schedulePanel, "schedule");
        optionsPanel.add(searchClassPanel, "search");
        optionsPanel.add(courseCatalogPanel, "catalog");
        
        cards.show(optionsPanel, "start");
        
        initializeOptions();
        mainPanel.add(optionsPanel);
        
        frame.add(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

    // the options and listeners of the main GUI after logging in
    void initializeOptions() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        
        JButton logout = new JButton("LOGOUT");
        logout.setBackground(Color.RED);
        logout.setForeground(Color.WHITE);

        JLabel optionsLabel = new JLabel("Options: ");
        optionsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        optionsLabel.setForeground(Color.MAGENTA);
        
        JButton scheduleButton = new JButton("My Schedule");
        JButton coursesButton = new JButton("Search Classes");
        JButton catalogButton = new JButton("Courses Catalog");
        
        buttonsPanel.add(Box.createVerticalStrut(50));
        buttonsPanel.add(optionsLabel);
        buttonsPanel.add(Box.createVerticalStrut(100));
        buttonsPanel.add(scheduleButton);
        buttonsPanel.add(Box.createVerticalStrut(100));
        buttonsPanel.add(coursesButton);
        buttonsPanel.add(Box.createVerticalStrut(100));
        buttonsPanel.add(catalogButton);
        buttonsPanel.add(Box.createVerticalStrut(100));
        buttonsPanel.add(logout);
        
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

        scheduleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createSchedule();
                cards.show(optionsPanel, "schedule");
            }
        });
        
        coursesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cards.show(optionsPanel, "search");
            }
        });
        
        catalogButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cards.show(optionsPanel, "catalog");
            }
        });
        mainPanel.add(buttonsPanel);
    }
    private void createSchedule() {
        schedulePanel.removeAll();
        ClientMsg scheduleAttempt = new ClientMsg("GET", "schedule", loggedUser);
        try {
            ostream.writeObject(scheduleAttempt);
            ServerMsg serverMsg = (ServerMsg) istream.readObject();
            if (serverMsg.isOk()) {
                Section[] enrolledSections = (Section[]) serverMsg.getBody();
                
                String[] titles = {"Course", "Section", "Description", "Instructor"};
                Object[][] data = new Object[enrolledSections.length][titles.length];
                
                for (int i = 0; i < enrolledSections.length; i++) {
                    Section section = enrolledSections[i];
                    data[i][0] = section.getCourse().getPrefix() + section.getCourse();
                    data[i][1] = section.getCourse().getNumber();
                    data[i][2] = section.getCourse().getDescription();
                    data[i][3] = section.getInstructor();
                }
                JTable scheduleTable = new JTable(data, titles);
                schedulePanel.add(new JScrollPane(scheduleTable));
                frame.repaint();
                frame.revalidate();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    
    public boolean isLoggedIn() {
        return loggedIn;
    }

}