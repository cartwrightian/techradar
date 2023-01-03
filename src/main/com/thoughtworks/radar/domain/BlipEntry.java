package com.thoughtworks.radar.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.thoughtworks.radar.Database.UniqueBlipIdPersister;

import java.time.LocalDate;
import java.util.Objects;

@DatabaseTable(tableName = "blip_history")
public class BlipEntry {

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh=true)
    private Volume volume;

    @DatabaseField(canBeNull = false)
    private Ring ring;

    @DatabaseField(canBeNull = false)
    private String description;

    @DatabaseField(canBeNull = false)
    private int idOnThisRadar;

    @DatabaseField(canBeNull = false, persisterClass = UniqueBlipIdPersister.class)
    private UniqueBlipId blipId;

    @DatabaseField(canBeNull = false)
    private Quadrant quadrant;

    public BlipEntry(UniqueBlipId uniqueId, Volume volume, Quadrant quadrant, Ring ring, String description, int idOnThisRadar) {
        this.blipId = uniqueId;
        this.volume = volume;
        this.quadrant = quadrant;
        this.ring = ring;
        this.description = description;
        this.idOnThisRadar = idOnThisRadar;
    }

    // db support
    BlipEntry() {

    }

    public Ring getRing() {
        return ring;
    }

    public LocalDate getDate() {
        return volume.getPublicationDate();
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
        BlipEntry blipEntry = (BlipEntry) o;
        return volume.equals(blipEntry.volume) && blipId.equals(blipEntry.blipId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(volume, blipId);
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }

    @Override
    public String toString() {
        return "BlipHistory{" +
                "volume=" + volume +
                ", ring=" + ring +
                ", description='" + description + '\'' +
                ", idOnThisRadar=" + idOnThisRadar +
                ", blipId=" + blipId +
                ", quadrant=" + quadrant +
                '}';
    }
}
