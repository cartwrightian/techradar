package com.thoughtworks.radar;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Blip implements Comparable<Blip> {
    private final int id;
    private String name;
    private Quadrant quadrant;
    private List<BlipHistory> historyList;
    private LocalDate appeared;
    private LocalDate lastDate;
    private Ring lastRing;
    private Ring firstRing;
    private String description;

    public Blip(int id, String name, Quadrant quadrant) {
        this.id = id;
        this.name = name;
        this.quadrant = quadrant;
        historyList = new LinkedList<>();
        appeared = LocalDate.MAX;
        lastDate = LocalDate.MIN;
        this.description = "";
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Blip other) {
        return Integer.compare(this.id, other.id);
    }

    public List<BlipHistory> getHistory() {
        return historyList;
    }

    public void addHistory(BlipHistory blipHistory) {
        historyList.add(blipHistory);
        LocalDate date = blipHistory.getDate();
        if (date.isAfter(lastDate)) {
            lastDate = date;
            lastRing = blipHistory.getRing();
        }
        if (date.isBefore(appeared)) {
            firstRing = blipHistory.getRing();
            appeared = date;
            description = blipHistory.getDescription();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blip blip = (Blip) o;
        return id == blip.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public LocalDate appearedDate() {
        return appeared;
    }

    public LocalDate lastDate() {
        return lastDate;
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }

    public Ring lastRing() {
        return lastRing;
    }

    public Ring firstRing() {
        return firstRing;
    }

    public String getDescription() {
        return description;
    }
}
