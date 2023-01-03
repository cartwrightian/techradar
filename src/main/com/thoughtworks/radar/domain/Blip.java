package com.thoughtworks.radar.domain;

import com.thoughtworks.radar.ToCSV;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Blip implements Comparable<Blip>, ToCSV {
    private final BlipId id;
    private final String name;
    private final String formatForCSV = "%s, \"%s\", %s, %s, %s, %s, %s, %s";

    // date -> history, in volume order
    private final SortedMap<Volume, BlipHistory> history;

    private Volume lastEdition;

    public Blip(BlipId id, String name) {
        this.id = id;
        this.name = name;
        history = new TreeMap<>();
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

    public void addHistory(Volume volume, BlipHistory blipHistory) {
        history.put(volume, blipHistory);

        if (lastEdition==null) {
            lastEdition = volume;
        } else if (volume.getPublicationDate().isAfter(lastEdition.getPublicationDate())) {
            lastEdition = volume;
        }
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
        return getFirstEntry().getDate();
    }

    public LocalDate lastDate() {
        return getLastEntry().getDate();
    }

    public Quadrant getFirstQuadrant() {
        return getFirstEntry().getQuadrant();
    }

    private BlipHistory getLastEntry() {
        Volume lastVolume = history.lastKey();
        return history.get(lastVolume);
    }

    public Ring lastRing() {
        return getLastEntry().getRing();
    }

    private BlipHistory getFirstEntry() {
        Volume firstVolume = history.firstKey();
        return history.get(firstVolume);
    }

    public Ring firstRing() {
        return getFirstEntry().getRing();
    }

    public String getDescription() {
        // description can be blank, so use the last none empty one we see
        List<String> descriptions = history.values().
                stream().map(BlipHistory::getDescription).
                filter(text -> !text.isEmpty()).
                collect(Collectors.toList());
        if (descriptions.isEmpty()) {
            throw new RuntimeException("No validate descriptions found for " + this);
        }
        return descriptions.get(descriptions.size()-1);
    }

    public boolean visibleOn(Volume volume) {
        return history.containsKey(volume);
    }

    public int idOnRadar(Volume volume) {
        if (!history.containsKey(volume)) {
            throw new RuntimeException("Could not find blip history for volume " + volume + " on blip " + this);
        }
        return history.get(volume).getIdOfBlipOnThisRadar();
    }

    public Duration getDuration() {
        Iterator<Map.Entry<Volume, BlipHistory>> iter = history.entrySet().iterator();

        Map.Entry<Volume, BlipHistory> first = iter.next();
        Volume previousVolume = first.getKey();
        LocalDate previousDate = previousVolume.getPublicationDate();

        long duration = 0L;
        while (iter.hasNext()) {
            Map.Entry<Volume, BlipHistory> current = iter.next();
            Volume currentVolume = current.getKey();
            LocalDate currentDate = current.getValue().getDate();

            // only count if present from one radar to the next
            if (currentVolume.getNumber()-previousVolume.getNumber()==1) {
                long gap = currentDate.toEpochDay()-previousDate.toEpochDay();
                duration = duration + gap;
            }
            previousVolume = currentVolume;
            previousDate = currentDate;
        }
        return Duration.ofDays(duration);
    }

    public LocalDate firstFadedDate() {
        Iterator<Map.Entry<Volume, BlipHistory>> iter = history.entrySet().iterator();
        Map.Entry<Volume, BlipHistory> first = iter.next();
        Volume previousEdition = first.getKey();
        LocalDate firstFadedDate = previousEdition.getPublicationDate();

        while (iter.hasNext()) {
            Map.Entry<Volume, BlipHistory> current = iter.next();
            Volume currentEdition = current.getKey();

            if (currentEdition.getNumber()-previousEdition.getNumber()==1) {
                firstFadedDate = current.getValue().getDate();
            } else {
                break;
            }
            previousEdition = currentEdition;
        }

        return firstFadedDate;
    }

    public Ring fadedRing() {
        Iterator<Map.Entry<Volume, BlipHistory>> iter = history.entrySet().iterator();
        Map.Entry<Volume, BlipHistory> first = iter.next();
        Volume previousEdition = first.getKey();
        Ring fadedRing = first.getValue().getRing();

        while (iter.hasNext()) {
            Map.Entry<Volume, BlipHistory> current = iter.next();
            Volume currentEdition = current.getKey();

            if (currentEdition.getNumber()-previousEdition.getNumber()==1) {
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
                id, name, getFirstQuadrant(), getDuration().toDays(), firstRing(), lastRing(), getNumberBlipMoves(), getNumberEditions());
    }

    private int getNumberEditions() {
        return history.size();
    }

    @Override
    public String getHeader() {
        return String.format(formatForCSV,
                "id", "name", "quadrant", "duration", "firstRing", "lastRing", "blipMoves", "editions");
    }

    public Ring ringFor(Volume volume) {
        return history.get(volume).getRing();
    }

    public Integer getNumberBlipMoves() {
        int moves = 0;
        List<Ring> rings = history.values().stream().map(BlipHistory::getRing).collect(Collectors.toList());
        Ring previous = rings.get(0);
        for (int i = 1; i < rings.size(); i++) {
            Ring current = rings.get(i);
            if (current!=previous) {
                moves = moves + 1;
            }
            previous = current;
        }
        return moves;
    }

    @Override
    public String toString() {
        return "Blip{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", formatForCSV='" + formatForCSV + '\'' +
                ", history=" + history +
                ", lastEdition=" + lastEdition +
                '}';
    }

    public Quadrant quadrantFor(Volume volume) {
        return history.get(volume).getQuadrant();
    }
}
