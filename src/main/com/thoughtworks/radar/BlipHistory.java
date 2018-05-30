package com.thoughtworks.radar;

import java.time.LocalDate;

public class BlipHistory {
    private final LocalDate date;
    private final Ring ring;

    public BlipHistory(LocalDate date, Ring ring) {
        this.date = date;
        this.ring = ring;
    }

    public Ring getRing() {
        return ring;
    }

    public LocalDate getDate() {
        return date;
    }
}
