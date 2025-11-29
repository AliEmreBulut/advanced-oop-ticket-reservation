
package com.project;
import java.time.LocalDateTime;

public class BusJourneyBuilder implements JourneyBuilder {
    private String id, origin, destination;
    private LocalDateTime dateTime;
    private int seatCount;

    public String getOrigin() {
        return origin;
    }

    public int getSeatCount() {
        return seatCount;
    }

    @Override public void setId(String id) { this.id = id; }
    @Override public void setOrigin(String origin) { this.origin = origin; }
    @Override public void setDestination(String destination) { this.destination = destination; }
    @Override public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    @Override public void setSeatCount(int seatCount) { this.seatCount = seatCount; }

    @Override
    public Journey build() {
        return new BusJourney(id, origin, destination, dateTime, seatCount);
    }
}
