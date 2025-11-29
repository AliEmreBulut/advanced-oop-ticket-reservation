package com.project;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class CancelReservationScreen extends JFrame {
    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private User currentUser;
    private SingletonUser singletonUser = SingletonUser.getInstance();

    public CancelReservationScreen() {
        this.currentUser = singletonUser.getCurrentUser();
        
        setTitle("My Reservations");
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create table model with columns
        String[] columns = {"Reservation ID", "Journey ID", "Seats", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Create table
        reservationTable = new JTable(tableModel);
        reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton("Cancel Reservation");
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back to Main Menu");
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load reservations
        loadReservations();

        // Cancel button action
        cancelButton.addActionListener(e -> {
            int selectedRow = reservationTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a reservation to cancel!");
                return;
            }

            String reservationId = (String) tableModel.getValueAt(selectedRow, 0);
            String journeyId = (String) tableModel.getValueAt(selectedRow, 1);
            String seats = (String) tableModel.getValueAt(selectedRow, 2);

            // Confirm cancellation
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?\n" +
                "Journey ID: " + journeyId + "\n" +
                "Seats: " + seats,
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (ReservationDatabase.cancelReservation(currentUser.getUsername(), reservationId)) {
                    JOptionPane.showMessageDialog(this, "Reservation cancelled successfully!");
                    loadReservations(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel reservation. Please try again.");
                }
            }
        });

        // Refresh button action
        refreshButton.addActionListener(e -> {
            loadReservations();
        });

        // Back button action
        backButton.addActionListener(e -> {
            dispose();
            new MainMenu();
        });

        setVisible(true);
    }

    private void loadReservations() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get user's reservations
        List<ReservationDatabase.Reservation> reservations = 
            ReservationDatabase.getUserReservations(currentUser.getUsername());

        // Add reservations to table
        for (ReservationDatabase.Reservation res : reservations) {
            Object[] row = {
                res.getReservationId(),
                res.getJourneyId(),
                res.getSeatNumbersString(),
                "Active"
            };
            tableModel.addRow(row);
        }

        // Show message if no reservations
        if (reservations.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no active reservations.");
        }
    }

    public static void main(String[] args) {
        
    }
} 