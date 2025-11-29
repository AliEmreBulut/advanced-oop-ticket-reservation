package com.project;

import java.util.List;

public interface FilterStrategy {
    List<Journey> filter(List<Journey> journeys);
}
