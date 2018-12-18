package com.thoughtworks.radar;

import java.time.LocalDate;
import java.util.Objects;

public class BlipHistory {
    private final LocalDate date;
    private final Ring ring;
    private final String description;
    private final int idOfBlipOnThisRadar;
    private final boolean faded;
    private final BlipId blipId;

    public BlipHistory(BlipId blipId, LocalDate date, Ring ring, String description, int rawRadarId, boolean faded) {
        this.blipId = blipId;
        this.date = date;
        this.ring = ring;
        this.description = description;
        this.idOfBlipOnThisRadar = rawRadarId;
        this.faded = faded;
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

    public boolean isFaded() {
        return faded;
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

}
