package com.project;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class MainMenu extends JFrame {
    private User currentUser;
    private SingletonUser singletonUser;

    public MainMenu() {
        singletonUser = SingletonUser.getInstance();
        this.currentUser = singletonUser.getCurrentUser();
        
        setTitle("Online Reservation - Main Menu");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(welcomeLabel);

        // View journeys button
        JButton viewJourneysButton = new JButton("View Available Journeys");
        add(viewJourneysButton);

        // Cancel reservation button
        JButton cancelReservationButton = new JButton("Cancel Reservation");
        add(cancelReservationButton);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        add(logoutButton);

        // View journeys button action
        viewJourneysButton.addActionListener(e -> {
            new JourneyListScreen();
        });

        // Cancel reservation button action
        cancelReservationButton.addActionListener(e -> {
            new CancelReservationScreen();
        });

        // Logout button action
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        setVisible(true);
    }
} 