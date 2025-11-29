
package com.project;
import java.time.LocalDateTime;

public class FlightJourney extends Journey {
    public FlightJourney(String id, String origin, String destination, LocalDateTime dateTime, int seatCount) {
        super(id, origin, destination, dateTime, seatCount);
    }

    @Override
    public String getType() {
        return "Plane";
    }
}

