package com.thoughtworks.radar;

import java.time.LocalDate;

public class BlipHistory {
    private final LocalDate date;
    private final Ring ring;
    private String description;

    public BlipHistory(LocalDate date, Ring ring, String description) {
        this.date = date;
        this.ring = ring;
        this.description = description;
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
}
