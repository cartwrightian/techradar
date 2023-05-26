package com.thoughtworks.radar.domain;

import com.thoughtworks.radar.ToCSV;

import java.time.LocalDate;

import static java.lang.String.format;

public class BlipLifetime implements ToCSV {
    private final String name;
    private final UniqueBlipId id;
    private final Volume firstVolume;
    private final Volume firstFadedVolume;
    private final Quadrant quadrant;
    private final Ring finalRing;

    public BlipLifetime(Blip blip) {
        this(blip.getName(), blip.getId(), blip.getFirstQuadrant(), blip.getFirstVolume(),
                blip.firstFadedVolume(), blip.fadedRing());
    }

    public BlipLifetime(String name, UniqueBlipId id, Quadrant quadrant,
                        Volume firstVolume, Volume firstFadeVolume, Ring finalRing) {
        this.name = name;
        this.id = id;
        this.quadrant = quadrant;
        this.firstVolume = firstVolume;
        this.firstFadedVolume = firstFadeVolume;
        this.finalRing = finalRing;
    }

    @Override
    public String toCSV() {
        long epochDay = firstVolume.getPublicationDate().toEpochDay();
        long lifetimeInDays = firstFadedVolume.getPublicationDate().toEpochDay() - epochDay;
        return format("%s,\"%s\",%s,%s,%s,%s",id, name, quadrant, firstVolume, firstFadedVolume, lifetimeInDays);
    }

    @Override
    public String getHeader() {
        return format("%s,%s,%s,%s,%s,%s", "id", "name", "quadrant", "firstRadarNum", "lastRadarNum", "lifetimeInDays");
    }

    public LocalDate getAppearedDate() {
        return firstVolume.getPublicationDate();
    }

    public LocalDate getLastSeen() {
        return firstFadedVolume.getPublicationDate();
    }

    public Volume getFirstVolume() {
        return firstVolume;
    }

    public Volume getFirstFadedVolume() {
        return firstFadedVolume;
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
