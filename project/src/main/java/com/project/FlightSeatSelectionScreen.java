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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class FlightSeatSelectionScreen extends JFrame {
    private User currentUser;
    private String journeyId;
    private List<JButton> seatButtons;
    private List<String> selectedSeats;
    private JLabel totalSeatsLabel;
    private SingletonUser singletonUser = SingletonUser.getInstance();
    private int rows; // Will be calculated based on total seats
    private String[] seatLetters = {"A", "B", "C", "D", "E", "F"}; // Standard 6-seat configuration

    public FlightSeatSelectionScreen( String journeyId) {
        this.currentUser = singletonUser.getCurrentUser();
        this.journeyId = journeyId;
        this.seatButtons = new ArrayList<>();
        this.selectedSeats = new ArrayList<>();
        
        // Calculate rows based on total seats (6 seats per row)
        int totalSeats = ReservationDatabase.getAvailableSeats(journeyId);
        this.rows = (int) Math.ceil(totalSeats / 6.0);
        
        setTitle("Flight Seat Selection - Journey " + journeyId);
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        setLayout(new BorderLayout());

        // Top panel for journey info
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.add(new JLabel("Select your seats (Click to select/deselect)"));
        add(infoPanel, BorderLayout.NORTH);

        // Center panel for seat layout
        JPanel seatPanel = new JPanel(new GridLayout(rows + 1, seatLetters.length + 1, 5, 5));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add column headers (seat letters)
        seatPanel.add(new JLabel("")); // Empty corner cell
        for (String letter : seatLetters) {
            JLabel label = new JLabel(letter, SwingConstants.CENTER);
            label.setPreferredSize(new Dimension(40, 30));
            seatPanel.add(label);
        }

        // Get reserved seats
        Set<Integer> reservedSeats = ReservationDatabase.getReservedSeats(journeyId);

        // Create seat buttons
        for (int row = 1; row <= rows; row++) {
            // Add row number
            JLabel rowLabel = new JLabel(String.valueOf(row), SwingConstants.CENTER);
            rowLabel.setPreferredSize(new Dimension(40, 30));
            seatPanel.add(rowLabel);

            // Add seats for this row
            for (String letter : seatLetters) {
                String seatNumber = row + letter;
                int seatCode = getSeatCode(seatNumber);
                JButton seatButton = createSeatButton(seatNumber, reservedSeats.contains(seatCode));
                seatButtons.add(seatButton);
                seatPanel.add(seatButton);
            }
        }

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
                "Confirm reservation for seats: " + getSelectedSeatNumbers() + "?",
                "Confirm Reservation",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Convert seat numbers to integers for the database
                List<Integer> seatCodes = new ArrayList<>();
                for (String seat : selectedSeats) {
                    seatCodes.add(getSeatCode(seat));
                }
                
                if (ReservationDatabase.makeFlightReservation(currentUser.getUsername(), journeyId, seatCodes)) {
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

    private JButton createSeatButton(String seatNumber, boolean isReserved) {
        JButton button = new JButton(seatNumber);
        button.setPreferredSize(new Dimension(40, 30));
        
        if (isReserved) {
            button.setBackground(new Color(255, 100, 100)); // Daha belirgin kırmızı
            button.setForeground(Color.WHITE); // Yazı rengini beyaz yap
            button.setEnabled(false);
            button.setToolTipText("Bu koltuk zaten rezerve edilmiş");
            button.setBorderPainted(false); // Kenarlığı kaldır
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorderPainted(true);

            // Add click action
            button.addActionListener(e -> {
                if (button.getBackground() == Color.WHITE) {
                    // Select seat
                    button.setBackground(new Color(100, 255, 100)); // Daha belirgin yeşil
                    button.setForeground(Color.BLACK);
                    selectedSeats.add(seatNumber);
                } else {
                    // Deselect seat
                    button.setBackground(Color.WHITE);
                    button.setForeground(Color.BLACK);
                    selectedSeats.remove(seatNumber);
                }
                updateTotalSeatsLabel();
            });
        }

        return button;
    }

    private void updateTotalSeatsLabel() {
        totalSeatsLabel.setText("Selected seats: " + selectedSeats.size());
    }

    private int getSeatCode(String seatText) {
        // Extract row number and column letter
        int row = Integer.parseInt(seatText.substring(0, seatText.length() - 1));
        char col = seatText.charAt(seatText.length() - 1);
        // Create a unique code: row * 100 + column index
        return (row - 1) * 100 + (col - 'A');
    }

    private void refreshSeats() {
        // Clear selected seats
        selectedSeats.clear();
        
        // Update seat buttons
        Set<Integer> reservedSeats = ReservationDatabase.getReservedSeats(journeyId);
        for (JButton seatButton : seatButtons) {
            String seatText = seatButton.getText();
            int seatCode = getSeatCode(seatText);
            if (reservedSeats.contains(seatCode)) {
                seatButton.setEnabled(false);
                seatButton.setBackground(new java.awt.Color(255, 100, 100));
                seatButton.setForeground(java.awt.Color.WHITE);
                seatButton.setToolTipText("Bu koltuk zaten rezerve edilmiş");
                seatButton.setBorderPainted(false);
            } else {
                seatButton.setEnabled(true);
                seatButton.setBackground(java.awt.Color.WHITE);
                seatButton.setForeground(java.awt.Color.BLACK);
                seatButton.setToolTipText(null);
                seatButton.setBorderPainted(true);
            }
        }
        
        // Update selected seats label
        updateSelectedSeatsLabel();
    }

    private void updateSelectedSeatsLabel() {
        totalSeatsLabel.setText("Selected seats: " + selectedSeats.size());
    }

    private String getSelectedSeatNumbers() {
        return selectedSeats.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
        });
    }
} 