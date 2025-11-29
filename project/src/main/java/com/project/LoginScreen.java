package com.project;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private SingletonUser singletonUser;
    public LoginScreen() {
        singletonUser = SingletonUser.getInstance();
        setTitle("Online Reservation - Sign In");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("Sign In");
        JButton registerButton = new JButton("Sign Up");
        add(loginButton);
        add(registerButton);

        loginButton.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            if (UserDatabase.validateUser(user, pass)) {
                User userObj = UserDatabase.getUser(user);
                singletonUser.login(userObj);
                dispose();
                if (userObj instanceof AdminUser) {
                    new AdminPanel(userObj);
                } else {
                    new MainMenu();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
        });

        registerButton.addActionListener(e -> {
            new RegisterScreen();
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}
