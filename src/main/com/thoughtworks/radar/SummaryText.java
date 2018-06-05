package com.thoughtworks.radar;

import java.time.LocalDate;

public class SummaryText {

    private Integer id;
    private LocalDate date;
    private Ring ring;
    private Quadrant quadrant;
    private String description;

    public SummaryText(int id, LocalDate date, Ring ring, Quadrant quadrant, String description) {

        this.id = id;
        this.date = date;
        this.ring = ring;
        this.quadrant = quadrant;
        this.description = description;
    }

    public String getId() {
        return id.toString();
    }

    public LocalDate getDate() {
        return date;
    }

    public String getRing() {
        return ring.toString();
    }

    public String getQuadrant() {
        return quadrant.toString();
    }

    public String getDescription() {
        return description;
    }
}
