package com.thoughtworks.radar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SummaryText implements ToCSV, Comparable<SummaryText> {

    private final Integer id;
    private final LocalDate date;
    private final Ring ring;
    private final Quadrant quadrant;
    private final String description;
    private final int radarId;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

    public SummaryText(int id, LocalDate date, Ring ring, Quadrant quadrant, String description, int radarId) {

        this.id = id;
        this.date = date;
        this.ring = ring;
        this.quadrant = quadrant;
        this.description = removeAnyMarkup(description);
        this.radarId = radarId;
    }

    private String removeAnyMarkup(String text) {
        StringBuilder output = new StringBuilder();

        boolean inside = false;
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current=='<') {
                inside = true;
            } else if (current=='>') {
                inside=false;
            } else if (!inside) {
                output.append(current);
            }
        }

        return output.toString();
    }

    public String getId() {
        return id.toString();
    }

    public String getDate() {
        return date.format(formatter);
    }

    public String getRing() {
        return ring.toString();
    }

    public String getQuadrant() {
        return quadrant.toString();
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toCSV() {
        return String.format("%s,%s,%s,%s,\"%s\"",
                getDate(), getRadarId(),  getQuadrant(), getRing(), getDescription());
    }

    public String getRadarId() {
        if (radarId==-1) {
            return "\"\"";
        }
        return Integer.toString(radarId);
    }

    // order of precedence: date, radarId, quadrant, ring, description
    @Override
    public int compareTo(SummaryText other) {
        int byDate = date.compareTo(other.date);
        if (byDate!=0) {
            return byDate;
        }
        // same date, try radarId
        int byRadarId = 0;
        if ((this.radarId>0) && (other.radarId>0)) {
            byRadarId = Integer.compare(this.radarId, other.radarId);
        }
        if (byRadarId!=0) {
            return byRadarId;
        }
        // same (or missing) radar id, use quadrant
        int byQuadrant = Integer.compare(this.quadrant.ordinal(), other.quadrant.ordinal());
        if (byQuadrant!=0) {
            return byQuadrant;
        }
        // same quadrant, use ring
        int byRing = Integer.compare(this.ring.ordinal(), other.ring.ordinal());
        if (byRing!=0) {
            return byRing;
        }
        // final fallback is description
        return description.compareTo(other.description);
    }
}
