package com.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class BusSeatSelectionScreen extends JFrame {
    private User currentUser;
    private String journeyId;
    private List<JButton> seatButtons;
    private List<Integer> selectedSeats;
    private JLabel totalSeatsLabel;
    private SingletonUser singletonUser = SingletonUser.getInstance();
    private int rows; // Will be calculated based on total seats
    private int seatsPerRow = 4; // Standard bus configuration

    public BusSeatSelectionScreen( String journeyId) {
        this.currentUser = singletonUser.getCurrentUser();
        this.journeyId = journeyId;
        this.seatButtons = new ArrayList<>();
        this.selectedSeats = new ArrayList<>();
        
        // Calculate rows based on total seats (4 seats per row)
        int totalSeats = ReservationDatabase.getAvailableSeats(journeyId);
        this.rows = (int) Math.ceil(totalSeats / (double)seatsPerRow);
        
        setTitle("Bus Seat Selection - Journey " + journeyId);
        setSize(600, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        setLayout(new BorderLayout());

        // Top panel for journey info
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.add(new JLabel("Select your seats (Click to select/deselect)"));
        add(infoPanel, BorderLayout.NORTH);

        // Center panel for seat layout
        JPanel seatPanel = new JPanel(new GridLayout(rows, seatsPerRow, 10, 10));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Get reserved seats
        Set<Integer> reservedSeats = ReservationDatabase.getReservedSeats(journeyId);

        // Create seat buttons
        for (int i = 1; i <= totalSeats; i++) {
            JButton seatButton = createSeatButton(i, reservedSeats.contains(i));
            seatButtons.add(seatButton);
            seatPanel.add(seatButton);
        }

        // Add steering wheel and driver seat
        JPanel driverPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        driverPanel.add(new JLabel("🚌"));
        add(driverPanel, BorderLayout.CENTER);

        // Add seat panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(seatPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        totalSeatsLabel = new JLabel("Selected seats: 0");
        JButton confirmButton = new JButton("Confirm Selection");
        JButton backButton = new JButton("Back");
        JButton refreshButton = new JButton("Refresh Seats");
        
        controlPanel.add(totalSeatsLabel);
        controlPanel.add(confirmButton);
        controlPanel.add(refreshButton);
        controlPanel.add(backButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Refresh button action
        refreshButton.addActionListener(e -> {
            refreshSeats();
        });

        // Confirm button action
        confirmButton.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one seat!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm reservation for seats: " + selectedSeats.toString() + "?",
                "Confirm Reservation",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (ReservationDatabase.makeBusReservation(currentUser.getUsername(), journeyId, selectedSeats)) {
                    JOptionPane.showMessageDialog(this, "Reservation successful!");
                    dispose();
                    new JourneyListScreen();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to make reservation!\n" +
                        "Some seats might have been reserved by another user.\n" +
                        "Please try again with different seats.");
                    refreshSeats();
                }
            }
        });

        // Back button action
        backButton.addActionListener(e -> {
            dispose();
            new JourneyListScreen();
        });

        setVisible(true);
    }

    private JButton createSeatButton(int seatNumber, boolean isReserved) {
        JButton button = new JButton(String.valueOf(seatNumber));
        button.setPreferredSize(new Dimension(60, 60));
        
        if (isReserved) {
            button.setBackground(new Color(255, 100, 100));
            button.setForeground(Color.WHITE);
            button.setEnabled(false);
            button.setToolTipText("Bu koltuk zaten rezerve edilmiş");
            button.setBorderPainted(false);
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorderPainted(true);

            // Add click action
            button.addActionListener(e -> {
                if (button.getBackground() == Color.WHITE) {
                    // Select seat
                    button.setBackground(new Color(100, 255, 100));
                    button.setForeground(Color.BLACK);
                    selectedSeats.add(seatNumber);
                } else {
                    // Deselect seat
                    button.setBackground(Color.WHITE);
                    button.setForeground(Color.BLACK);
                    selectedSeats.remove(Integer.valueOf(seatNumber));
                }
                updateTotalSeatsLabel();
            });
        }

        return button;
    }

    private void updateTotalSeatsLabel() {
        totalSeatsLabel.setText("Selected seats: " + selectedSeats.size());
    }

    private void refreshSeats() {
        // Clear selected seats
        selectedSeats.clear();
        
        // Update seat buttons
        Set<Integer> reservedSeats = ReservationDatabase.getReservedSeats(journeyId);
        for (JButton seatButton : seatButtons) {
            int seatNumber = Integer.parseInt(seatButton.getText());
            if (reservedSeats.contains(seatNumber)) {
                seatButton.setEnabled(false);
                seatButton.setBackground(Color.RED);
                seatButton.setToolTipText("This seat is already reserved");
            } else {
                seatButton.setEnabled(true);
                seatButton.setBackground(Color.WHITE);
                seatButton.setToolTipText(null);
            }
        }
        
        // Update selected seats label
        updateSelectedSeatsLabel();
    }

    private void updateSelectedSeatsLabel() {
        totalSeatsLabel.setText("Selected seats: " + selectedSeats.size());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
        });
    }
} 