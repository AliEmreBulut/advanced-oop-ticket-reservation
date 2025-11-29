package com.project;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class AdminPanel extends JFrame {
    private User currentUser;
    private JTable journeyTable;
    private JTable reservationTable;
    private DefaultTableModel journeyTableModel;
    private DefaultTableModel reservationTableModel;

    public AdminPanel(User user) {
        this.currentUser = user;
        
        setTitle("Admin Panel");
        setSize(1000, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        setLayout(new BorderLayout());

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Journey Management Tab
        JPanel journeyPanel = createJourneyPanel();
        tabbedPane.addTab("Journey Management", journeyPanel);
        refreshJourneyTable();

        // Reservation Management Tab
        JPanel reservationPanel = createReservationPanel();
        tabbedPane.addTab("Reservation Management", reservationPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom panel for logout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshJourneyTable();
        setVisible(true);
    }

    private JPanel createJourneyPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create journey table
        String[] columns = {"Journey ID", "Type", "Origin", "Destination", "Date & Time", "Available Seats"};
        journeyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        journeyTable = new JTable(journeyTableModel);
        JScrollPane journeyScrollPane = new JScrollPane(journeyTable);
        panel.add(journeyScrollPane, BorderLayout.CENTER);

        // Create journey form panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Bus", "Flight"});
        JTextField originField = new JTextField();
        JTextField destinationField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField timeField = new JTextField();
        JTextField seatsField = new JTextField();
        JLabel existingIdsLabel = new JLabel("Existing Journey IDs: " + String.join(", ", ReservationDatabase.getExistingJourneyIds()));

        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Origin:"));
        formPanel.add(originField);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destinationField);
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Time (HH:mm):"));
        formPanel.add(timeField);
        formPanel.add(new JLabel("Total Seats:"));
        formPanel.add(seatsField);
        formPanel.add(new JLabel(""));
        formPanel.add(existingIdsLabel);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createButton = new JButton("Create Journey");
        JButton cancelButton = new JButton("Cancel Selected Journey");
        JButton refreshButton = new JButton("Refresh");

        createButton.addActionListener(e -> {
            try {
                String type = (String) typeCombo.getSelectedItem();
                String origin = originField.getText().trim();
                String destination = destinationField.getText().trim();
                String date = dateField.getText().trim();
                String time = timeField.getText().trim();
                String seatsStr = seatsField.getText().trim();

                // Validate inputs with specific error messages
                StringBuilder errorMessage = new StringBuilder();
                
                if (origin.isEmpty()) {
                    errorMessage.append("• Origin field cannot be empty\n");
                }
                
                if (destination.isEmpty()) {
                    errorMessage.append("• Destination field cannot be empty\n");
                }

                // Check if origin and destination are different
                if (!origin.isEmpty() && !destination.isEmpty() && origin.equalsIgnoreCase(destination)) {
                    errorMessage.append("• Origin and destination cannot be the same\n");
                }
                
                if (date.isEmpty()) {
                    errorMessage.append("• Date field cannot be empty\n");
                } else if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    errorMessage.append("• Date must be in YYYY-MM-DD format\n");
                } else {
                    try {
                        // Parse the date and check if it's in the future
                        LocalDateTime journeyDateTime = LocalDateTime.parse(date + "T" + time + ":00");
                        LocalDateTime now = LocalDateTime.now();
                        
                        if (journeyDateTime.isBefore(now)) {
                            errorMessage.append("• Journey date and time must be in the future\n");
                        }
                    } catch (Exception ex) {
                        errorMessage.append("• Invalid date or time format\n");
                    }
                }
                
                if (time.isEmpty()) {
                    errorMessage.append("• Time field cannot be empty\n");
                } else if (!time.matches("\\d{2}:\\d{2}")) {
                    errorMessage.append("• Time must be in HH:mm format\n");
                } else {
                    try {
                        // Parse time and check if it's valid
                        String[] timeParts = time.split(":");
                        int hours = Integer.parseInt(timeParts[0]);
                        int minutes = Integer.parseInt(timeParts[1]);
                        
                        if (hours < 0 || hours > 23) {
                            errorMessage.append("• Hours must be between 00 and 23\n");
                        }
                        if (minutes < 0 || minutes > 59) {
                            errorMessage.append("• Minutes must be between 00 and 59\n");
                        }
                    } catch (Exception ex) {
                        errorMessage.append("• Invalid time format\n");
                    }
                }
                
                if (seatsStr.isEmpty()) {
                    errorMessage.append("• Seats field cannot be empty\n");
                } else {
                    try {
                        int seats = Integer.parseInt(seatsStr);
                        if (seats <= 0) {
                            errorMessage.append("• Number of seats must be positive\n");
                        } else if (type.equals("Bus") && seats % 4 != 0) {
                            errorMessage.append("• Bus journeys must have seats in multiples of 4\n");
                        } else if (type.equals("Flight") && seats % 6 != 0) {
                            errorMessage.append("• Flight journeys must have seats in multiples of 6\n");
                        }
                    } catch (NumberFormatException ex) {
                        errorMessage.append("• Seats must be a valid number\n");
                    }
                }

                if (errorMessage.length() > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Please correct the following errors:\n\n" + errorMessage.toString(),
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create journey ID (B001, F001, etc.)
                String prefix = type.equals("Bus") ? "B" : "F";
                int nextNumber = getNextJourneyNumber(type);
                String journeyId = prefix + String.format("%03d", nextNumber);

                // Add to database
                if (ReservationDatabase.addJourney(journeyId, type, origin, destination, date + " " + time, Integer.parseInt(seatsStr))) {
                    JOptionPane.showMessageDialog(this, 
                        String.format("Journey created successfully!\nJourney ID: %s", journeyId));
                    refreshJourneyTable();
                    clearForm(typeCombo, originField, destinationField, dateField, timeField, seatsField);
                    // Update existing IDs label
                    existingIdsLabel.setText("Existing Journey IDs: " + String.join(", ", ReservationDatabase.getExistingJourneyIds()));
                } else {
                    JOptionPane.showMessageDialog(this, 
                        String.format("Failed to create journey!\nJourney ID %s might already exist.", journeyId),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "An unexpected error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            int selectedRow = journeyTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a journey to cancel!");
                return;
            }

            String journeyId = (String) journeyTableModel.getValueAt(selectedRow, 0);
            String type = (String) journeyTableModel.getValueAt(selectedRow, 1);
            String origin = (String) journeyTableModel.getValueAt(selectedRow, 2);
            String destination = (String) journeyTableModel.getValueAt(selectedRow, 3);
            String dateTime = (String) journeyTableModel.getValueAt(selectedRow, 4);

            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this journey?\n\n" +
                "Journey ID: " + journeyId + "\n" +
                "Type: " + type + "\n" +
                "From: " + origin + "\n" +
                "To: " + destination + "\n" +
                "Date & Time: " + dateTime,
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                ReservationDatabase.JourneyCancellationResult result = ReservationDatabase.cancelJourney(journeyId);
                if (result.isSuccess()) {
                    StringBuilder message = new StringBuilder("Journey cancelled successfully!");
                    
                    // Add information about cancelled reservations
                    List<String> cancelledReservations = result.getCancelledReservations();
                    if (!cancelledReservations.isEmpty()) {
                        message.append("\n\nThe following reservations were also cancelled:\n");
                        for (String res : cancelledReservations) {
                            message.append("• ").append(res).append("\n");
                        }
                    }
                    
                    JOptionPane.showMessageDialog(this, message.toString());
                    refreshJourneyTable();
                    refreshReservationTable(); // Also refresh the reservation table
                    // Update existing IDs label
                    existingIdsLabel.setText("Existing Journey IDs: " + String.join(", ", ReservationDatabase.getExistingJourneyIds()));
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel journey!");
                }
            }
        });

        refreshButton.addActionListener(e -> {
            refreshJourneyTable();
            existingIdsLabel.setText("Existing Journey IDs: " + String.join(", ", ReservationDatabase.getExistingJourneyIds()));
        });

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        formPanel.add(buttonPanel);

        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create reservation table
        String[] columns = {"Reservation ID", "Username", "Journey ID", "Seats", "Status"};
        reservationTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(reservationTableModel);
        JScrollPane reservationScrollPane = new JScrollPane(reservationTable);
        panel.add(reservationScrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancelButton = new JButton("Cancel Selected Reservation");
        JButton refreshButton = new JButton("Refresh");

        cancelButton.addActionListener(e -> {
            int selectedRow = reservationTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a reservation to cancel!");
                return;
            }

            String reservationId = (String) reservationTableModel.getValueAt(selectedRow, 0);
            String username = (String) reservationTableModel.getValueAt(selectedRow, 1);
            String journeyId = (String) reservationTableModel.getValueAt(selectedRow, 2);
            String seats = (String) reservationTableModel.getValueAt(selectedRow, 3);

            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?\n" +
                "Username: " + username + "\n" +
                "Journey ID: " + journeyId + "\n" +
                "Seats: " + seats,
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (ReservationDatabase.cancelReservation(username, reservationId)) {
                    JOptionPane.showMessageDialog(this, "Reservation cancelled successfully!");
                    refreshReservationTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel reservation!");
                }
            }
        });

        refreshButton.addActionListener(e -> refreshReservationTable());

        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshReservationTable();
        return panel;
    }

    private void refreshJourneyTable() {
        journeyTableModel.setRowCount(0);
        List<ReservationDatabase.Journey> journeys = ReservationDatabase.getAllJourneys();
        for (ReservationDatabase.Journey journey : journeys) {
            Object[] row = {
                journey.getId(),
                journey.getType(),
                journey.getOrigin(),
                journey.getDestination(),
                journey.getDateTime(),
                journey.getAvailableSeats()
            };
            journeyTableModel.addRow(row);
        }
    }

    private void refreshReservationTable() {
        reservationTableModel.setRowCount(0);
        List<ReservationDatabase.Reservation> reservations = ReservationDatabase.getAllReservations();
        for (ReservationDatabase.Reservation res : reservations) {
            Object[] row = {
                res.getReservationId(),
                res.getUsername(),
                res.getJourneyId(),
                res.getSeatNumbersString(),
                "Active"
            };
            reservationTableModel.addRow(row);
        }
    }

    private void clearForm(JComboBox<String> typeCombo, JTextField... fields) {
        typeCombo.setSelectedIndex(0);
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    private int getNextJourneyNumber(String type) {
        String prefix = type.equals("Bus") ? "B" : "F";
        int nextNumber = 1;
        
        // Keep incrementing until we find an available ID
        while (ReservationDatabase.isJourneyIdExists(prefix + String.format("%03d", nextNumber))) {
            nextNumber++;
        }
        
        return nextNumber;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing purposes
            User adminUser = new AdminUser("admin", "admin");
            new AdminPanel(adminUser);
        });
    }
} 