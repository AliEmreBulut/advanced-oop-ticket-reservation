package com.project;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BusJourneyBuilderTest {

    @Test
    public void testBuildBusJourney() {
        // Arrange
        BusJourneyBuilder builder = new BusJourneyBuilder();
        String id = "B123";
        String origin = "Istanbul";
        String destination = "Ankara";
        LocalDateTime dateTime = LocalDateTime.of(2025, 6, 20, 14, 0);
        int seatCount = 40;

        // Act
        builder.setId(id);
        builder.setOrigin(origin);
        builder.setDestination(destination);
        builder.setDateTime(dateTime);
        builder.setSeatCount(seatCount);
        Journey journey = builder.build();

        // Assert
        assertTrue(journey instanceof BusJourney);
        assertEquals(id, journey.getId());
        assertEquals(origin, journey.getOrigin());
        assertEquals(destination, journey.getDestination());
        assertEquals(dateTime, journey.getDateTime());
        assertEquals(seatCount, journey.getSeatCount());
    }
}
