package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.Quadrant;
import com.thoughtworks.radar.domain.Ring;
import com.thoughtworks.radar.domain.UniqueBlipId;

import java.time.LocalDate;

public class RawBlip {

    private final UniqueBlipId id;
    private final String name;
    private final LocalDate date;
    private final Ring ring;
    private final String description;
    private final int radarId;
    private final Quadrant quadrant;

    public RawBlip(UniqueBlipId id, String name, LocalDate date, Ring ring, Quadrant quadrant, String description, int radarId) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.ring = ring;
        this.quadrant = quadrant;
        this.description = description;
        this.radarId = radarId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public Ring getRing() {
        return ring;
    }

    public UniqueBlipId getId() {
        return id;
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }

    public String getDescription() {
        return description;
    }

    public int getRadarId() {
        return radarId;
    }

}
