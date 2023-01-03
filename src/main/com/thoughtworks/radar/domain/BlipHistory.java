package com.thoughtworks.radar.domain;

import java.time.LocalDate;
import java.util.Objects;

public class BlipHistory {
    private final LocalDate date;
    private final Ring ring;
    private final String description;
    private final int idOnThisRadar;
    private final UniqueBlipId blipId;
    private final Quadrant quadrant;

    public BlipHistory(UniqueBlipId uniqueId, LocalDate date, Quadrant quadrant, Ring ring, String description, int idOnThisRadar) {
        this.blipId = uniqueId;
        this.date = date;
        this.quadrant = quadrant;
        this.ring = ring;
        this.description = description;
        this.idOnThisRadar = idOnThisRadar;
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

    public int getIdOnThisRadar() {
        return idOnThisRadar;
    }

    public UniqueBlipId getUniqueId() {
        return blipId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlipHistory that = (BlipHistory) o;
        return idOnThisRadar == that.idOnThisRadar &&
                Objects.equals(blipId, that.blipId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOnThisRadar, blipId);
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }
}
