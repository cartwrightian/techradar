package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.BlipId;
import com.thoughtworks.radar.domain.Quadrant;
import com.thoughtworks.radar.domain.Ring;

import java.time.LocalDate;

import static java.lang.String.format;

public class BlipLifetime implements ToCSV {
    private final LocalDate appeared ;
    private final LocalDate lastSeen;
    private String name;
    private BlipId id;
    private int firstRadarNum;
    private int lastRadarNum;
    private Quadrant quadrant;
    private Ring finalRing;

    @Override
    public String toCSV() {
        long epochDay = appeared.toEpochDay();
        long lifetimeInDays = lastSeen.toEpochDay()- epochDay;
        return format("%s,\"%s\",%s,%s,%s,%s",id, name, quadrant, firstRadarNum, lastRadarNum, lifetimeInDays);
    }

    @Override
    public String getHeader() {
        return format("%s,%s,%s,%s,%s,%s", "id", "name", "quadrant", "firstRadarNum", "lastRadarNum", "lifetimeInDays");
    }

    public BlipLifetime(String name, BlipId id, Quadrant quadrant, LocalDate appeared, LocalDate lastSeen,
                        int firstRadarNum, int lastRadarNum, Ring finalRing) {
        this.name = name;
        this.id = id;
        this.quadrant = quadrant;
        this.appeared = appeared;
        this.lastSeen = lastSeen;
        this.firstRadarNum = firstRadarNum;
        this.lastRadarNum = lastRadarNum;
        this.finalRing = finalRing;
    }

    public LocalDate getAppearedDate() {
        return appeared;
    }

    public LocalDate getLastSeen() {
        return lastSeen;
    }

    public int getFirstRadarNum() {
        return firstRadarNum;
    }

    public int getLastRadarNum() {
        return lastRadarNum;
    }

    public String getName() {
        return name;
    }

    public BlipId getId() {
        return id;
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }

    public Ring getFinalRing() {
        return finalRing;
    }
}
