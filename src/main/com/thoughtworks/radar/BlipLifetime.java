package com.thoughtworks.radar;

import java.time.LocalDate;

import static java.lang.String.format;

public class BlipLifetime implements ToCSV {
    private final LocalDate appeared ;
    private final LocalDate lastSeen;
    private String name;
    private int id;
    private int first;
    private int last;
    private Quadrant quadrant;

    @Override
    public String toCSV() {
        long epochDay = appeared.toEpochDay();
        long lasted = lastSeen.toEpochDay()- epochDay;
        return format("%s,\"%s\",%s,%s,%s,%s",id, name, quadrant, first, last, lasted);
    }

    public BlipLifetime(String name, int id, Quadrant quadrant, LocalDate appeared, LocalDate lastSeen, int first, int last) {
        this.name = name;
        this.id = id;
        this.quadrant = quadrant;
        this.appeared = appeared;
        this.lastSeen = lastSeen;
        this.first = first;
        this.last = last;
    }

    public LocalDate getAppearedDate() {
        return appeared;
    }

    public LocalDate getLastSeen() {
        return lastSeen;
    }

    public int getFirst() {
        return first;
    }

    public int getLast() {
        return last;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }
}
