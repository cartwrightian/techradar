package com.thoughtworks.radar;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class Blip implements Comparable<Blip>, ToCSV {
    private final BlipId id;
    private final String name;
    private final String formatForCSV = "%s, %s, %s, %s, %s, %s, %s";
    private Quadrant quadrant;
    // date -> history, in date order
    private SortedMap<Integer,BlipHistory> history;
    private LocalDate appeared;
    private LocalDate lastDate;
    private Ring lastRing;
    private Ring firstRing;
    private String description;
    private int blipMoves;
    private int lastEdition = Integer.MIN_VALUE;

    public Blip(BlipId id, String name, Quadrant quadrant) {
        this.id = id;
        this.name = name;
        this.quadrant = quadrant;
        history = new TreeMap<>();
        appeared = LocalDate.MAX;
        lastDate = LocalDate.MIN;
        this.description = "";
        blipMoves = 0;
    }

    public String getName() {
        return name;
    }

    public BlipId getId() {
        return id;
    }

    @Override
    public int compareTo(Blip other) {
        return BlipId.compare(this.id, other.id);
    }

    public Collection<BlipHistory> getHistory() {
        return history.values();
    }

    public void addHistory(int edition, BlipHistory blipHistory) {
        history.put(edition, blipHistory);
        LocalDate date = blipHistory.getDate();
        if (date.isAfter(lastDate)) {
            lastDate = date;
            if (!blipHistory.getRing().equals(lastRing)) {
                blipMoves++;
            }
            lastRing = blipHistory.getRing();
            // use the latest version of description
            if (!blipHistory.getDescription().isEmpty()) {
                description = blipHistory.getDescription();
            }
        }
        if (date.isBefore(appeared)) {
            firstRing = blipHistory.getRing();
            appeared = date;
        }
        if (edition>lastEdition) {
            lastEdition = edition;
        }
        // TODO duration - not same as first appear and fade as some things came back on to radar later...
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blip blip = (Blip) o;
        return id == blip.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public LocalDate appearedDate() {
        return appeared;
    }

    public LocalDate lastDate() {
        return lastDate;
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }

    public Ring lastRing() {
        return lastRing;
    }

    public Ring firstRing() {
        return firstRing;
    }

    public String getDescription() {
        return description;
    }

    public boolean visibleOn(int edition) {
        if (!history.containsKey(edition)) {
            return false;
        }
        return history.containsKey(edition);
    }

    public int idOnRadar(int edition) {
        return history.get(edition).getIdOfBlipOnThisRadar();
    }

    public Duration getDuration() {
        Iterator<Map.Entry<Integer, BlipHistory>> iter = history.entrySet().iterator();

        Map.Entry<Integer, BlipHistory> first = iter.next();
        Integer previousEdition = first.getKey();
        LocalDate previousDate = first.getValue().getDate();

        long duration = 0L;
        while (iter.hasNext()) {
            Map.Entry<Integer, BlipHistory> current = iter.next();
            Integer currentEdition = current.getKey();
            LocalDate currentDate = current.getValue().getDate();

            // only count if present from one radar to the next
            if (currentEdition-previousEdition==1) {
                long gap = currentDate.toEpochDay()-previousDate.toEpochDay();
                duration = duration + gap;
            }
            previousEdition = currentEdition;
            previousDate = currentDate;
        }
        return Duration.ofDays(duration);
    }

    public LocalDate firstFadedDate() {
        Iterator<Map.Entry<Integer, BlipHistory>> iter = history.entrySet().iterator();
        Map.Entry<Integer, BlipHistory> first = iter.next();
        Integer previousEdition = first.getKey();
        LocalDate firstFadedDate = first.getValue().getDate();

        while (iter.hasNext()) {
            Map.Entry<Integer, BlipHistory> current = iter.next();
            Integer currentEdition = current.getKey();

            if (currentEdition-previousEdition==1) {
                firstFadedDate = current.getValue().getDate();
            } else {
                break;
            }
            previousEdition = currentEdition;
        }

        return firstFadedDate;
    }

    public Ring fadedRing() {
        Iterator<Map.Entry<Integer, BlipHistory>> iter = history.entrySet().iterator();
        Map.Entry<Integer, BlipHistory> first = iter.next();
        Integer previousEdition = first.getKey();
        Ring fadedRing = first.getValue().getRing();

        while (iter.hasNext()) {
            Map.Entry<Integer, BlipHistory> current = iter.next();
            Integer currentEdition = current.getKey();

            if (currentEdition-previousEdition==1) {
                fadedRing = current.getValue().getRing();
            } else {
                break;
            }
            previousEdition = currentEdition;
        }

        return fadedRing;
    }

    @Override
    public String toCSV() {
        return String.format(formatForCSV,
                id, name, quadrant, getDuration().toDays(), firstRing, lastRing, blipMoves);
    }

    @Override
    public String getHeader() {
        return String.format(formatForCSV,
                "id", "name", "quadrant", "duration", "firstRing", "lastRing", "blipMoves");
    }

    public Ring ringFor(int edition) {
        return history.get(edition).getRing();
    }


    public Integer getNumberBlipMoves() {
        return blipMoves;
    }
}
