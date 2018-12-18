package com.thoughtworks.radar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class Parser {

    public Radars parse(String rawJson) throws ParseException {

        JSONParser jsonParser = new JSONParser();

        JSONArray parsed = (JSONArray)jsonParser.parse(rawJson);

        List<JSONObject> radarJson = new LinkedList<>();
        parsed.forEach(raw -> {
            JSONObject item = (JSONObject) raw;
            if (item.containsKey("blips")) {
                radarJson.add(item);
            }
        });

        Radars radars = new Radars();
        radarJson.forEach(json -> {
            String rawDate = (String) json.get("date");
            LocalDate date = parseDate(rawDate);

            JSONArray blipsList = (JSONArray) json.get("blips");
            blipsList.forEach(blipJson -> {
                RawBlip blip = parseItem((JSONObject) blipJson, date);
                radars.add(blip);
            });
        });
        radars.updateBlipHistories();

        return radars;
    }

    public LocalDate parseDate(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(string+"-01", formatter);
    }

    private RawBlip parseItem(JSONObject jsonObject, LocalDate date) {
        String name = (String) jsonObject.get("name");
        String rawRing = (String) jsonObject.get("ring");
        String rawQuadrant = (String) jsonObject.get("quadrant");
        BlipId id = BlipId.parse((String)jsonObject.get("id"));
        String description = (String) jsonObject.get("description");

        // format of radar ID was changed at one point
        int radarId = -1;
        Object maybeRadarId = jsonObject.get("radarId");
        if (maybeRadarId!=null) {
            if (maybeRadarId instanceof Long) {
                radarId = Math.toIntExact((Long) maybeRadarId);
            } else {
                radarId = Integer.parseInt((String) maybeRadarId);
            }
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
        private Quadrant quadrant;

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
