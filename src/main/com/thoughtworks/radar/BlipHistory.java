package com.thoughtworks.radar;

import java.time.LocalDate;

public class BlipHistory {
    private final LocalDate date;
    private final Ring ring;
    private String description;
    private int radarId;

    public BlipHistory(LocalDate date, Ring ring, String description, int radarId) {
        this.date = date;
        this.ring = ring;
        this.description = description;
        this.radarId = radarId;
    }

    public Ring getRing() {
        return ring;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getRadarId() {
        return radarId;
    }
}
