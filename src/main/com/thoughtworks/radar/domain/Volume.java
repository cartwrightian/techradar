package com.thoughtworks.radar.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Volume implements Comparable<Volume> {
    private final int number;
    private final LocalDate publicationDate;

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
