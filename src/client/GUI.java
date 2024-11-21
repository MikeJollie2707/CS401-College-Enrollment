package client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
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

import objects.BodyCourseSearch;
import objects.BodyLogin;
import objects.ClientMsg;
import objects.Course;
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
    private JPanel searchClassPanel;
    private JPanel courseCatalogPanel;
    
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
        // saves the universities in case the user logs out
        saveUniversities();
        // initializeLogin() handles user log in before reaching optionsPanel
        initializeLogin();
        frame.setVisible(true);
    }

    public void saveUniversities() {
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
        mainPanel = new JPanel(new BorderLayout());

        JLabel templateStart = new JLabel("Please click an option to start");
        templateStart.setFont(new Font("Arial", Font.BOLD, 30));
        templateStart.setForeground(Color.BLUE);

        cards = new CardLayout();
        optionsPanel = new JPanel(cards);
        
        searchClassPanel = new JPanel();
        schedulePanel = new JPanel();
        courseCatalogPanel = new JPanel();
        
        searchClassPanel.setLayout(new BorderLayout());
        schedulePanel.setLayout(new BorderLayout());
        courseCatalogPanel.setLayout(new BorderLayout());

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
        JOptionPane.showMessageDialog(frame, "Logged in to " + loggedUser.getUniName() + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
        frame.revalidate();
        frame.repaint();
    }

    // the options and listeners of the main GUI after logging in
    void initializeOptions() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JButton logout = new JButton("LOGOUT");
        logout.setBackground(Color.RED);
        logout.setForeground(Color.BLACK);

        JLabel optionsLabel = new JLabel("" +loggedUser.getUniName() + " options:");
        optionsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        optionsLabel.setForeground(Color.MAGENTA);

        JButton scheduleButton = new JButton("My Schedule");
        JButton coursesButton = new JButton("Search Classes");
        JButton catalogButton = new JButton("Courses Catalog");
        scheduleButton.setBackground(Color.BLACK);
        coursesButton.setBackground(Color.BLACK);
        catalogButton.setBackground(Color.BLACK);
        
        
        scheduleButton.setForeground(Color.CYAN);
        coursesButton.setForeground(Color.CYAN);
        catalogButton.setForeground(Color.CYAN);
        
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
                searchClassPanel.removeAll();
                schedulePanel.removeAll();
                courseCatalogPanel.removeAll();
                frame.revalidate();
                frame.repaint();
                createSchedule();
                cards.show(optionsPanel, "schedule");
            }
        });

        coursesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchClassPanel.removeAll();
                schedulePanel.removeAll();
                courseCatalogPanel.removeAll();
                frame.revalidate();
                frame.repaint();
                createSearch();
                cards.show(optionsPanel, "search");
            }
        });

        catalogButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchClassPanel.removeAll();
                schedulePanel.removeAll();
                courseCatalogPanel.removeAll();
                frame.revalidate();
                frame.repaint();
                createCatalog();
                cards.show(optionsPanel, "catalog");
            }
        });
        mainPanel.add(buttonsPanel, BorderLayout.WEST);
        frame.revalidate();
        frame.repaint();
    }

    private void createSchedule() {
        ClientMsg scheduleAttempt = new ClientMsg("GET", "schedule", loggedUser);
        try {
            ostream.writeObject(scheduleAttempt);
            ServerMsg serverMsg = (ServerMsg) istream.readObject();
            if (serverMsg.isOk()) {
                Section[] myCourses = (Section[]) serverMsg.getBody();
                
                String[] titles = {"Course Prefix", "Course Number", "Status"};
                Object[][] data = new Object[myCourses.length][titles.length];

                for (int i = 0; i < myCourses.length; i++) {
                    Section section = myCourses[i];
                    data[i][0] = section.getCourse().getPrefix();
                    data[i][1] = section.getCourse().getNumber();
                }
                JTable scheduleTable = new JTable(data, titles);
                scheduleTable.setDefaultEditor(Object.class, null);
                JLabel scheduleText = new JLabel("My Schedule:");
                scheduleText.setFont(new Font("Arial", Font.BOLD, 20));
                scheduleText.setForeground(Color.BLUE);
                
                JPanel textPanel = new JPanel(new FlowLayout());
                JPanel tablePanel = new JPanel(new FlowLayout());
                JScrollPane scroll = new JScrollPane(scheduleTable);
                textPanel.add(scheduleText);
                tablePanel.add(scroll);
                
                schedulePanel.add(textPanel, BorderLayout.NORTH);
                schedulePanel.add(tablePanel, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void createSearch() {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel courseNameLabel = new JLabel("Course Name: ");
        JLabel courseNumberLabel = new JLabel("Course Number: ");
        JLabel coursePrefixLabel = new JLabel("Course Prefix: ");
        JLabel instructorTextLabel = new JLabel("Instructor Name: ");

        JTextField courseNameText = new JTextField(20);
        JTextField courseNumberText = new JTextField(20);
        JTextField coursePrefix = new JTextField(20);
        JTextField instructorText = new JTextField(20);
        JButton searchButton = new JButton("SEARCH");
        searchButton.setBackground(Color.DARK_GRAY);
        searchButton.setBackground(Color.PINK);
        
        textPanel.add(courseNameLabel);
        textPanel.add(courseNameText);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(courseNumberLabel);
        textPanel.add(courseNumberText);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(coursePrefixLabel);
        textPanel.add(coursePrefix);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(instructorTextLabel);
        textPanel.add(instructorText);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(searchButton);
        
        JPanel textCenter = new JPanel(new FlowLayout());
        textCenter.add(textPanel);
        
        searchClassPanel.add(textCenter, BorderLayout.NORTH);
        frame.revalidate();
        frame.repaint();
        
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String course_name_query = courseNameText.getText().toLowerCase();
                String course_number_query = courseNumberText.getText().toLowerCase();
                String course_prefix_query = coursePrefix.getText().toLowerCase();
                String instructor_query = instructorText.getText().toLowerCase();
                searchResults(course_name_query, course_number_query, course_prefix_query, instructor_query);
                frame.revalidate();
                frame.repaint();
            }
        });
    }

    private void searchResults(String courseName, String courseNumber, String coursePrefix, String instructorName) {
        // the search form is the first component I added, removing everything after that to reset
        if(searchClassPanel.getComponentCount() > 1){
            for(int i = 1;i < searchClassPanel.getComponentCount(); i++) {
                searchClassPanel.remove(i);
                frame.revalidate();
                frame.repaint();
            }
        }
        BodyCourseSearch searchBody = new BodyCourseSearch();
        searchBody.setCourseName(courseName);
        searchBody.setCoursePrefix(coursePrefix);
        searchBody.setCourseNumber(courseNumber);
        searchBody.setInstructorName(instructorName);

        ClientMsg searchAttempt = new ClientMsg("GET", "courses", searchBody);
        try {
            ostream.writeObject(searchAttempt);
            ServerMsg serverMsg = (ServerMsg) istream.readObject();

            if (serverMsg.isOk()) {
                Course[] courses = (Course[]) serverMsg.getBody();
                String[] columnNames = {"Course Prefix", "Course Number", "Status"};
                Object[][] data = new Object[courses.length][columnNames.length];
                
                for (int i = 0; i < courses.length; i++) {
                    Course course = courses[i];
                    data[i][0] = course.getPrefix();
                    data[i][1] = course.getNumber();
                    //TO DO: cannot simply add JButton to JTable
                }
                // second check to clean up if label and search results stay to clear
                if(searchClassPanel.getComponentCount() > 1){
                    for(int i = 1;i < searchClassPanel.getComponentCount(); i++) {
                        searchClassPanel.remove(i);
                        frame.revalidate();
                        frame.repaint();
                    }
                }
                JPanel searchLabel = new JPanel(new FlowLayout());
                if(courses.length < 1) {
                    JLabel notFound = new JLabel("No courses found, try other criteria");
                    notFound.setFont(new Font("Arial", Font.BOLD, 20));
                    notFound.setForeground(Color.BLUE);
                    searchLabel.add(notFound);
                    searchClassPanel.add(searchLabel, BorderLayout.CENTER);
                }else {
                    JLabel coursesFound = new JLabel("Courses found:");
                    coursesFound.setFont(new Font("Arial", Font.BOLD, 20));
                    coursesFound.setForeground(Color.BLUE);
                    
                    JTable courseTable = new JTable(data, columnNames);
                    courseTable.setDefaultEditor(Object.class, null);
                    JScrollPane scroll = new JScrollPane(courseTable);
                    searchLabel.add(coursesFound);
                    
                    JPanel tablePanel = new JPanel(new FlowLayout());
                    tablePanel.add(scroll);
                    searchClassPanel.add(searchLabel, BorderLayout.CENTER);
                    searchClassPanel.add(tablePanel, BorderLayout.SOUTH);
                    frame.revalidate();
                    frame.repaint();
                }
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    
    private void createCatalog() {
        courseCatalogPanel.removeAll();
        BodyCourseSearch searchBody = new BodyCourseSearch();
        // no filter, all courses (for now)
        searchBody.setCourseName("");
        searchBody.setCoursePrefix("");
        searchBody.setCourseNumber("");
        searchBody.setInstructorName("");
        
        ClientMsg searchAttempt = new ClientMsg("GET", "courses", searchBody);
        try {
            ostream.writeObject(searchAttempt);
            ServerMsg serverMsg = (ServerMsg) istream.readObject();

            if (serverMsg.isOk()) {
                Course[] courses = (Course[]) serverMsg.getBody();
                String[] columnNames = {"Course Prefix", "Course Number", "Course Prerequisites"};
                Object[][] data = new Object[courses.length][columnNames.length];

                for (int i = 0; i < courses.length; i++) {
                    Course course = courses[i];
                    data[i][0] = course.getPrefix();
                    data[i][1] = course.getNumber();
                }
                courseCatalogPanel.removeAll();
                if(courses.length < 1) {
                    JLabel notFound = new JLabel("No courses found in " + loggedUser.getUniName() +" :(");
                    notFound.setFont(new Font("Arial", Font.BOLD, 20));
                    notFound.setForeground(Color.BLUE);
                    courseCatalogPanel.add(notFound, BorderLayout.CENTER);
                }else {
                    JLabel coursesFound = new JLabel("Course Catalog of " + loggedUser.getUniName()+ ":");
                    coursesFound.setFont(new Font("Arial", Font.BOLD, 20));
                    coursesFound.setForeground(Color.BLUE);
                    
                    JTable catalogTable = new JTable(data, columnNames);
                    catalogTable.setDefaultEditor(Object.class, null);
                    JScrollPane scroll = new JScrollPane(catalogTable);
                    
                    JPanel textPanel = new JPanel(new FlowLayout());
                    JPanel catalogPanel = new JPanel(new FlowLayout());
                    textPanel.add(coursesFound);
                    catalogPanel.add(scroll);
                    
                    courseCatalogPanel.add(textPanel, BorderLayout.NORTH);
                    courseCatalogPanel.add(catalogPanel, BorderLayout.CENTER);
                    frame.revalidate();
                    frame.repaint();
                }
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    public boolean isLoggedIn() {
        return loggedIn;
    }

}