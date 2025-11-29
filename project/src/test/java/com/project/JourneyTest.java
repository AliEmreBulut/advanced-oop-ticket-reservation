package com.project;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JourneyTest {

    @Test
    void testJourneyToString() {
        Journey journey = new TestJourney("2", "Antalya", "Adana", LocalDateTime.of(2025, 7, 10, 12, 0), 50);

        String output = journey.toString();
        assertTrue(output.contains("Antalya"));
        assertTrue(output.contains("Adana"));
        assertTrue(output.contains("Koltuk: 50"));
    }

    static class TestJourney extends Journey {
        public TestJourney(String id, String origin, String destination, LocalDateTime dateTime, int seatCount) {
            super(id, origin, destination, dateTime, seatCount);
        }

        @Override
        public String getType() {
            return "TEST";
        }
    }
}
