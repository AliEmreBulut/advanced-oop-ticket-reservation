
package com.project;
import java.time.LocalDateTime;
public class BusJourney extends Journey {
    public BusJourney(String id, String origin, String destination, LocalDateTime dateTime, int seatCount) {
        super(id, origin, destination, dateTime, seatCount);
    }

    @Override
    public String getOrigin() {
        return origin;
    }


    @Override
    public int getSeatCount() {
        return seatCount;
    }



    @Override
    public String getType() {
        return "Bus";
    }
}
