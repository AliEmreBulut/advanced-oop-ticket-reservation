package com.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReservationDatabase {
    private static Map<String, List<Reservation>> userReservations = new HashMap<>();
    private static Map<String, Integer> journeySeats = new HashMap<>();
    private static Map<String, Set<Integer>> reservedSeats = new HashMap<>();
    private static List<Journey> journeys = new ArrayList<>();

    static {
        // Initialize sample journey seats
        journeySeats.put("B001", 48);
        journeySeats.put("B002", 32);
        journeySeats.put("F001", 120);
        journeySeats.put("F002", 150);

        // Initialize reserved seats for each journey
        for (String journeyId : journeySeats.keySet()) {
            reservedSeats.put(journeyId, new HashSet<>());
        }

        // Add sample journeys
        addJourney("B001", "Bus", "Istanbul", "Ankara", "2024-03-20 10:00", 48);
        addJourney("B002", "Bus", "Ankara", "Izmir", "2024-03-21 14:00", 32);
        addJourney("F001", "Flight", "Istanbul", "London", "2024-03-22 09:00", 120);
        addJourney("F002", "Flight", "London", "Paris", "2024-03-23 11:00", 150);
    }

    public static boolean addJourney(String id, String type, String origin, String destination, String dateTime, int seats) {
        // Check if journey ID already exists (only if not in static initialization)
        if (journeySeats.containsKey(id) && !Thread.currentThread().getStackTrace()[2].getMethodName().equals("<clinit>")) {
            return false;
        }

        // Validate journey type
        if (!type.equals("Bus") && !type.equals("Flight")) {
            return false;
        }

        // Validate seats based on journey type
        if (type.equals("Bus") && seats % 4 != 0) {
            return false;
        } else if (type.equals("Flight") && seats % 6 != 0) {
            return false;
        }

        // Add journey
        journeys.add(new Journey(id, type, origin, destination, dateTime, seats));
        journeySeats.put(id, seats);
        reservedSeats.put(id, new HashSet<>());
        return true;
    }

    public static List<String> getExistingJourneyIds() {
        return new ArrayList<>(journeySeats.keySet());
    }

    public static boolean isJourneyIdExists(String id) {
        return journeySeats.containsKey(id);
    }

    public static List<Journey> getAllJourneys() {
        return new ArrayList<>(journeys);
    }

    public static List<Reservation> getAllReservations() {
        List<Reservation> allReservations = new ArrayList<>();
        for (List<Reservation> userRes : userReservations.values()) {
            allReservations.addAll(userRes);
        }
        return allReservations;
    }

    public static boolean makeReservation(String username, String journeyId, int seats) {
        // Check if journey exists and has enough seats
        if (!journeySeats.containsKey(journeyId)) {
            return false;
        }

        int availableSeats = journeySeats.get(journeyId);
        if (seats > availableSeats) {
            return false;
        }

        // Create new reservation
        Reservation reservation = new Reservation(username, journeyId, seats);

        // Add to user's reservations
        userReservations.computeIfAbsent(username, k -> new ArrayList<>()).add(reservation);

        // Update available seats
        journeySeats.put(journeyId, availableSeats - seats);

        return true;
    }

    public static boolean makeBusReservation(String username, String journeyId, List<Integer> seatNumbers) {
        // Check if journey exists
        if (!journeySeats.containsKey(journeyId)) {
            return false;
        }

        // Check if any of the selected seats are already reserved
        Set<Integer> journeyReservedSeats = reservedSeats.get(journeyId);
        for (Integer seatNumber : seatNumbers) {
            if (journeyReservedSeats.contains(seatNumber)) {
                return false;
            }
        }

        // Create new reservation
        Reservation reservation = new Reservation(username, journeyId, seatNumbers);

        // Add to user's reservations
        userReservations.computeIfAbsent(username, k -> new ArrayList<>()).add(reservation);

        // Mark seats as reserved
        journeyReservedSeats.addAll(seatNumbers);

        // Update available seats
        int currentSeats = journeySeats.get(journeyId);
        journeySeats.put(journeyId, currentSeats - seatNumbers.size());

        return true;
    }

    public static boolean makeFlightReservation(String username, String journeyId, List<Integer> seatNumbers) {
        // Check if journey exists
        if (!journeySeats.containsKey(journeyId)) {
            return false;
        }

        // Check if any of the selected seats are already reserved
        Set<Integer> journeyReservedSeats = reservedSeats.get(journeyId);
        for (Integer seatNumber : seatNumbers) {
            if (journeyReservedSeats.contains(seatNumber)) {
                return false;
            }
        }

        // Create new reservation
        Reservation reservation = new Reservation(username, journeyId, seatNumbers);

        // Add to user's reservations
        userReservations.computeIfAbsent(username, k -> new ArrayList<>()).add(reservation);

        // Mark seats as reserved
        journeyReservedSeats.addAll(seatNumbers);

        // Update available seats
        int currentSeats = journeySeats.get(journeyId);
        journeySeats.put(journeyId, currentSeats - seatNumbers.size());

        return true;
    }

    public static boolean cancelReservation(String username, String reservationId) {
        List<Reservation> userRes = userReservations.get(username);
        if (userRes == null) {
            return false;
        }

        // Find the reservation to cancel
        Reservation reservationToCancel = null;
        for (Reservation res : userRes) {
            if (res.getReservationId().equals(reservationId)) {
                reservationToCancel = res;
                break;
            }
        }

        if (reservationToCancel == null) {
            return false;
        }

        // Return seats to journey
        String journeyId = reservationToCancel.getJourneyId();
        int currentSeats = journeySeats.getOrDefault(journeyId, 0);
        journeySeats.put(journeyId, currentSeats + reservationToCancel.getSeats());

        // Remove reservation
        userRes.remove(reservationToCancel);
        return true;
    }

    public static List<Reservation> getUserReservations(String username) {
        return userReservations.getOrDefault(username, new ArrayList<>());
    }

    public static int getAvailableSeats(String journeyId) {
        return journeySeats.getOrDefault(journeyId, 0);
    }

    public static Set<Integer> getReservedSeats(String journeyId) {
        return reservedSeats.getOrDefault(journeyId, new HashSet<>());
    }

    public static class JourneyCancellationResult {
        private boolean success;
        private List<String> cancelledReservations;

        public JourneyCancellationResult(boolean success, List<String> cancelledReservations) {
            this.success = success;
            this.cancelledReservations = cancelledReservations;
        }

        public boolean isSuccess() {
            return success;
        }

        public List<String> getCancelledReservations() {
            return cancelledReservations;
        }
    }

    public static JourneyCancellationResult cancelJourney(String journeyId) {
        // Check if journey exists
        if (!journeySeats.containsKey(journeyId)) {
            return new JourneyCancellationResult(false, new ArrayList<>());
        }

        // Collect all reservations for this journey
        List<String> cancelledReservations = new ArrayList<>();
        for (Map.Entry<String, List<Reservation>> entry : userReservations.entrySet()) {
            String username = entry.getKey();
            List<Reservation> userRes = entry.getValue();
            
            // Find and collect reservations for this journey
            List<Reservation> journeyReservations = userRes.stream()
                .filter(res -> res.getJourneyId().equals(journeyId))
                .collect(Collectors.toList());
            
            // Add to cancelled reservations list
            for (Reservation res : journeyReservations) {
                cancelledReservations.add(String.format("%s (User: %s, Seats: %s)", 
                    res.getReservationId(), username, res.getSeatNumbersString()));
            }
            
            // Remove these reservations
            userRes.removeAll(journeyReservations);
        }

        // Remove journey from all collections
        journeys.removeIf(j -> j.getId().equals(journeyId));
        journeySeats.remove(journeyId);
        reservedSeats.remove(journeyId);

        return new JourneyCancellationResult(true, cancelledReservations);
    }

    public static class Reservation {
        private String username;
        private String journeyId;
        private int seats;
        private String reservationId;
        private List<Integer> seatNumbers;
        private String type; // "Bus" or "Flight"

        public Reservation(String username, String journeyId, int seats) {
            this.username = username;
            this.journeyId = journeyId;
            this.seats = seats;
            this.reservationId = generateReservationId();
            this.seatNumbers = new ArrayList<>();
            this.type = journeyId.startsWith("F") ? "Flight" : "Bus";
        }

        public Reservation(String username, String journeyId, List<Integer> seatNumbers) {
            this.username = username;
            this.journeyId = journeyId;
            this.seats = seatNumbers.size();
            this.reservationId = generateReservationId();
            this.seatNumbers = new ArrayList<>(seatNumbers);
            this.type = journeyId.startsWith("F") ? "Flight" : "Bus";
        }

        private String generateReservationId() {
            return "RES" + System.currentTimeMillis();
        }

        public String getReservationId() {
            return reservationId;
        }

        public String getJourneyId() {
            return journeyId;
        }

        public String getUsername() {
            return username;
        }

        public int getSeats() {
            return seats;
        }

        public List<Integer> getSeatNumbers() {
            return seatNumbers;
        }

        public String getSeatNumbersString() {
            if (seatNumbers.isEmpty()) {
                return String.valueOf(seats);
            }

            if (type.equals("Flight")) {
                // Convert hash codes back to seat numbers (e.g., 10A, 10B, etc.)
                List<String> formattedSeats = new ArrayList<>();
                for (Integer seatCode : seatNumbers) {
                    int row = (seatCode / 100) + 1;
                    int col = seatCode % 100;
                    String letter = String.valueOf((char)('A' + col));
                    formattedSeats.add(row + letter);
                }
                return formattedSeats.toString();
            } else {
                // For bus, just return the seat numbers
                return seatNumbers.toString();
            }
        }
    }

    public static class Journey {
        private String id;
        private String type;
        private String origin;
        private String destination;
        private String dateTime;
        private int availableSeats;

        public Journey(String id, String type, String origin, String destination, String dateTime, int availableSeats) {
            this.id = id;
            this.type = type;
            this.origin = origin;
            this.destination = destination;
            this.dateTime = dateTime;
            this.availableSeats = availableSeats;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getOrigin() {
            return origin;
        }

        public String getDestination() {
            return destination;
        }

        public String getDateTime() {
            return dateTime;
        }

        public int getAvailableSeats() {
            return availableSeats;
        }
    }
} 