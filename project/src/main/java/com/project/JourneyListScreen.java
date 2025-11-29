package com.project;

import java.awt.BorderLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class JourneyListScreen extends JFrame {
    private JTable journeyTable;
    private DefaultTableModel tableModel;
    private User currentUser;
    private SingletonUser singletonUser;

    public JourneyListScreen() {
        singletonUser = SingletonUser.getInstance();
        this.currentUser = singletonUser.getCurrentUser();
        
        setTitle("Available Journeys");
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create table model with columns
        String[] columns = {"Journey ID", "Type", "Origin", "Destination", "Date & Time", "Available Seats"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        journeyTable = new JTable(tableModel);
        journeyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(journeyTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        JButton makeReservationButton = new JButton("Make Reservation");
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back to Main Menu");
        buttonPanel.add(makeReservationButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add sample data (you should replace this with actual data from your database)
        addSampleJourneys();

        // Make reservation button action
        makeReservationButton.addActionListener(e -> {
            int selectedRow = journeyTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a journey first!");
                return;
            }

            String journeyId = (String) tableModel.getValueAt(selectedRow, 0);
            String journeyType = (String) tableModel.getValueAt(selectedRow, 1);
            int availableSeats = (int) tableModel.getValueAt(selectedRow, 5);

            if (journeyType.equals("Bus")) {
                // Open bus seat selection screen
                dispose();
                new BusSeatSelectionScreen(journeyId);
            } else if (journeyType.equals("Flight")) {
                // Open flight seat selection screen
                dispose();
                new FlightSeatSelectionScreen(journeyId);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid journey type!");
            }
        });

        // Refresh button action
        refreshButton.addActionListener(e -> {
            refreshJourneys();
        });

        // Back button action
        backButton.addActionListener(e -> {
            dispose();
            new MainMenu();
        });

        setVisible(true);
    }

    private void addSampleJourneys() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Add sample bus journeys
        addJourneyToTable("B001", "Bus", "Istanbul", "Ankara", 
            LocalDateTime.now().plusDays(1), ReservationDatabase.getAvailableSeats("B001"));
        addJourneyToTable("B002", "Bus", "Ankara", "Izmir", 
            LocalDateTime.now().plusDays(2), ReservationDatabase.getAvailableSeats("B002"));
        
        // Add sample flight journeys
        addJourneyToTable("F001", "Flight", "Istanbul", "London", 
            LocalDateTime.now().plusDays(3), ReservationDatabase.getAvailableSeats("F001"));
        addJourneyToTable("F002", "Flight", "London", "Paris", 
            LocalDateTime.now().plusDays(4), ReservationDatabase.getAvailableSeats("F002"));
    }

    private void addJourneyToTable(String id, String type, String origin, 
                                 String destination, LocalDateTime dateTime, int seats) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = dateTime.format(formatter);
        
        Object[] row = {id, type, origin, destination, formattedDateTime, seats};
        tableModel.addRow(row);
    }

    private void refreshJourneys() {
        // Clear and reload the table data
        tableModel.setRowCount(0);
        addSampleJourneys();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
        });
    }
} 