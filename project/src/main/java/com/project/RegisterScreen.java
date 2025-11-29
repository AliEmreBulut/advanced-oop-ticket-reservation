package com.project;

import java.awt.GridLayout;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegisterScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public RegisterScreen() {
        setTitle("Online Reservation - Sign Up");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        // Username field
        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        // Email field
        add(new JLabel("E-mail:"));
        emailField = new JTextField();
        add(emailField);

        // Password field
        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        // Confirm password field
        add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        add(confirmPasswordField);

        // Register button
        JButton registerButton = new JButton("Register");
        add(registerButton);

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        add(cancelButton);

        // Register button action
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Basic validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!");
                return;
            }

            // Email format validation
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                return;
            }

            // User registration
            if (UserDatabase.registerUser(username, email, password)) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now sign in.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "This username is already taken!");
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }
} 