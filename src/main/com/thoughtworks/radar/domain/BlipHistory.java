package com.thoughtworks.radar.domain;

import java.time.LocalDate;
import java.util.Objects;

public class BlipHistory {
    private final LocalDate date;
    private final Ring ring;
    private final String description;
    private final int idOfBlipOnThisRadar;
    private final BlipId blipId;
    private final Quadrant quadrant;

    public BlipHistory(BlipId blipId, LocalDate date, Quadrant quadrant, Ring ring, String description, int rawRadarId) {
        this.blipId = blipId;
        this.date = date;
        this.quadrant = quadrant;
        this.ring = ring;
        this.description = description;
        this.idOfBlipOnThisRadar = rawRadarId;
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

    public int getIdOfBlipOnThisRadar() {
        return idOfBlipOnThisRadar;
    }

    public BlipId getBlipId() {
        return blipId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlipHistory that = (BlipHistory) o;
        return idOfBlipOnThisRadar == that.idOfBlipOnThisRadar &&
                Objects.equals(blipId, that.blipId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfBlipOnThisRadar, blipId);
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }
}
