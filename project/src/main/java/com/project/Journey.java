package com.project;
import java.time.LocalDateTime;

public abstract class Journey {
    protected String id;
    protected String origin;
    protected String destination;
    protected LocalDateTime dateTime;
    protected int seatCount;

    public Journey(String id, String origin, String destination, LocalDateTime dateTime, int seatCount) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.dateTime = dateTime;
        this.seatCount = seatCount;
    }

    public String getOrigin() {
        return origin;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public String getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public abstract String getType();

    @Override
    public String toString() {
        return "[" + getType() + "] " + origin + " → " + destination + " | " + dateTime + " | Koltuk: " + seatCount;
    }

}
