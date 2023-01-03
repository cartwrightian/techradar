package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.UniqueBlipId;
import com.thoughtworks.radar.domain.Quadrant;
import com.thoughtworks.radar.domain.Ring;
import com.thoughtworks.radar.domain.Volume;

import java.time.LocalDate;

import static java.lang.String.format;

public class BlipLifetime implements ToCSV {
    //private final LocalDate appeared;
    private final LocalDate lastSeen;
    private final String name;
    private final UniqueBlipId id;
    private final Volume firstVolume;
    private final Volume lastRadarNum;
    private final Quadrant quadrant;
    private final Ring finalRing;

    @Override
    public String toCSV() {
        long epochDay = firstVolume.getPublicationDate().toEpochDay();
        long lifetimeInDays = lastSeen.toEpochDay()- epochDay;
        return format("%s,\"%s\",%s,%s,%s,%s",id, name, quadrant, firstVolume, lastRadarNum, lifetimeInDays);
    }

    @Override
    public String getHeader() {
        return format("%s,%s,%s,%s,%s,%s", "id", "name", "quadrant", "firstRadarNum", "lastRadarNum", "lifetimeInDays");
    }

    public BlipLifetime(String name, UniqueBlipId id, Quadrant quadrant, LocalDate appeared, LocalDate lastSeen,
                        Volume firstVolume, Volume lastRadarNum, Ring finalRing) {
        this.name = name;
        this.id = id;
        this.quadrant = quadrant;
        //this.appeared = appeared;
        this.lastSeen = lastSeen;
        this.firstVolume = firstVolume;
        this.lastRadarNum = lastRadarNum;
        this.finalRing = finalRing;
    }

    public LocalDate getAppearedDate() {
        return firstVolume.getPublicationDate();
    }

    public LocalDate getLastSeen() {
        return lastSeen;
    }

    public Volume getFirstVolume() {
        return firstVolume;
    }

    public Volume getLastRadarNum() {
        return lastRadarNum;
    }

    public String getName() {
        return name;
    }

    public UniqueBlipId getId() {
        return id;
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }

    public Ring getFinalRing() {
        return finalRing;
    }

}
