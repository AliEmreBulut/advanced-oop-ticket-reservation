package com.project;
import java.time.LocalDateTime;

public interface JourneyBuilder {
    void setId(String id);
    void setOrigin(String origin);
    void setDestination(String destination);
    void setDateTime(LocalDateTime dateTime);
    void setSeatCount(int seatCount);
    Journey build();
}

