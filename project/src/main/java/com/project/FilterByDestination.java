package com.project;

import java.util.List;
import java.util.stream.Collectors;

public class FilterByDestination implements FilterStrategy {
    private String destination;

    public FilterByDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public List<Journey> filter(List<Journey> journeys) {
        return journeys.stream()
                .filter(j -> j.getDestination().equalsIgnoreCase(destination))
                .collect(Collectors.toList());
    }
}

