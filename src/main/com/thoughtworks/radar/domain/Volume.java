package com.thoughtworks.radar.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.thoughtworks.radar.Database.LocalDatePersister;

import java.time.LocalDate;
import java.util.Objects;

@DatabaseTable(tableName = "volumes")
public class Volume implements Comparable<Volume> {

    @DatabaseField(canBeNull = false, id = true)
    private int number;

    @DatabaseField(canBeNull = false, persisterClass = LocalDatePersister.class)
    private LocalDate publicationDate;

    Volume() {
        // for db
    }

    public Volume(int number, LocalDate publicationDate) {
        this.number = number;
        this.publicationDate = publicationDate;
    }

    public static boolean greaterOrEquals(Volume volumeA, Volume volumeB) {
        return volumeA.number >= volumeB.number;
    }

    public static boolean lessThanOrEquals(Volume volumeA, Volume volumeB) {
        return volumeA.number <= volumeB.number;
    }

    @Override
    public int compareTo(Volume other) {
        return Integer.compare(number, other.number);
    }

    public int getNumber() {
        return number;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    @Override
    public String toString() {
        return "Volume{" +
                "number=" + number +
                ", publicationDate=" + publicationDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Volume volume = (Volume) o;
        return number == volume.number && publicationDate.equals(volume.publicationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, publicationDate);
    }
}
