package com.thoughtworks.radar;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Parser {

    public Radars parse(String rawJson) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(rawJson);

        Radars radars = new Radars();

        jsonNode.forEach(radarNode -> {
            JsonNode dateNode = radarNode.get("date");
            String rawDate = dateNode.asText();
            LocalDate date = parseDate(rawDate);

            JsonNode blipsNode = radarNode.get("blips");
            for (JsonNode blipNode : blipsNode) {
                RawBlip blip = parseItem(blipNode, date);
                radars.add(blip);
            }

        });
        
        radars.updateBlipHistories();

        return radars;
    }

    public LocalDate parseDate(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(string+"-01", formatter);
    }

    private RawBlip parseItem(JsonNode jsonObject, LocalDate date) {
        String name = jsonObject.get("name").asText();
        String rawRing = jsonObject.get("ring").asText();
        String rawQuadrant = jsonObject.get("quadrant").asText();
        BlipId id = BlipId.parse(jsonObject.get("id").asText());
        String description = jsonObject.get("description").asText();

        // format of radar ID was changed at one point
        int radarId = -1;
        if (jsonObject.has("radarId")) {
            radarId = jsonObject.get("radarId").asInt();
        }

        Ring ring = Ring.valueOf(rawRing);
        Quadrant quadrant = Quadrant.fromString(rawQuadrant);
        return new RawBlip(id, name, date, ring, quadrant,description, radarId);
    }

    public static class RawBlip {

        private final BlipId id;
        private final String name;
        private final LocalDate date;
        private final Ring ring;
        private final String description;
        private final int radarId;
        private final Quadrant quadrant;

        public RawBlip(BlipId id, String name, LocalDate date, Ring ring, Quadrant quadrant, String description, int radarId) {
            this.id = id;
            this.name = name;
            this.date = date;
            this.ring = ring;
            this.quadrant = quadrant;
            this.description = description;
            this.radarId = radarId;
        }

        public String getName() {
            return name;
        }

        public LocalDate getDate() {
            return date;
        }

        public Ring getRing() {
            return ring;
        }

        public BlipId getId() {
            return id;
        }

        public Quadrant getQuadrant() {
            return quadrant;
        }

        public String getDescription() {
            return description;
        }

        public int getRadarId() {
            return radarId;
        }

    }

}
