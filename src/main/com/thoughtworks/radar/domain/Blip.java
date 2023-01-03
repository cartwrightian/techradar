package com.thoughtworks.radar.domain;

import com.thoughtworks.radar.ToCSV;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Blip implements Comparable<Blip>, ToCSV {
    private final UniqueBlipId id;
    private final String name;
    private final String formatForCSV = "%s, \"%s\", %s, %s, %s, %s, %s, %s";

    // date -> history, in volume order
    private final SortedMap<Volume, BlipEntry> history;

    public Blip(UniqueBlipId id, String name) {
        this.id = id;
        this.name = name;
        history = new TreeMap<>();
    }

    public String getName() {
        return name;
    }

    public UniqueBlipId getId() {
        return id;
    }

    @Override
    public int compareTo(Blip other) {
        return UniqueBlipId.compare(this.id, other.id);
    }

    public Collection<BlipEntry> getHistory() {
        return history.values();
    }

    public void addHistory(Volume volume, BlipEntry blipEntry) {
        if (!volume.getPublicationDate().equals(blipEntry.getDate())) {
            throw new RuntimeException("Date mismatch on volume and history for " + volume.getPublicationDate() + " and " + blipEntry.getDate());
        }
        history.put(volume, blipEntry);
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

    private BlipEntry getLastEntry() {
        Volume lastVolume = history.lastKey();
        return history.get(lastVolume);
    }

    public Ring lastRing() {
        return getLastEntry().getRing();
    }

    private BlipEntry getFirstEntry() {
        Volume firstVolume = history.firstKey();
        return history.get(firstVolume);
    }

    public Ring firstRing() {
        return getFirstEntry().getRing();
    }

    public String getDescription() {
        // description can be blank, so use the last none empty one we see
        List<String> descriptions = history.values().
                stream().map(BlipEntry::getDescription).
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
        return history.get(volume).getIdOnThisRadar();
    }

    public Duration getDuration() {
        Iterator<Map.Entry<Volume, BlipEntry>> iter = history.entrySet().iterator();

        Map.Entry<Volume, BlipEntry> first = iter.next();
        Volume previousVolume = first.getKey();
        LocalDate previousDate = previousVolume.getPublicationDate();

        long duration = 0L;
        while (iter.hasNext()) {
            Map.Entry<Volume, BlipEntry> current = iter.next();
            Volume currentVolume = current.getKey();
            LocalDate currentDate = current.getValue().getDate();

            // only count if present from one radar to the next
            if (currentVolume.getNumber() - previousVolume.getNumber() == 1) {
                long days = currentDate.toEpochDay()-previousDate.toEpochDay();
                duration = duration + days;
            }
            previousVolume = currentVolume;
            previousDate = currentDate;
        }
        return Duration.ofDays(duration);
    }

    private Volume firstFadedVolume() {
        List<Volume> volumesInOrder = new ArrayList<>(history.keySet());
        Volume firstFaded = volumesInOrder.get(0);

        for (int i = 1; i < volumesInOrder.size(); i++) {
            Volume current = volumesInOrder.get(i);
            if (current.getNumber()-firstFaded.getNumber() > 1) {
                return current;
            }
            firstFaded = current;
        }
        return firstFaded;
    }

    private BlipEntry firstFaded() {
        return history.get(firstFadedVolume());
    }

    public LocalDate firstFadedDate() {
        return firstFadedVolume().getPublicationDate();
    }

    public Ring fadedRing() {
        return firstFaded().getRing();
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
        List<Ring> rings = history.values().stream().map(BlipEntry::getRing).collect(Collectors.toList());
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
                '}';
    }

    public Quadrant quadrantFor(Volume volume) {
        return history.get(volume).getQuadrant();
    }
}
