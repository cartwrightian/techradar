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

    public Radar parse(String rawJson) throws ParseException {

        JSONParser jsonParser = new JSONParser();

        JSONArray parsed = (JSONArray)jsonParser.parse(rawJson);

        List<JSONObject> radarJson = new LinkedList<>();
        parsed.forEach(raw -> {
            JSONObject item = (JSONObject) raw;
            if (item.containsKey("blips")) {
                radarJson.add(item);
            }
        });

        Radar radar = new Radar();
        radarJson.forEach(json -> {
            String rawDate = (String) json.get("date");
            LocalDate date = parseDate(rawDate);

            JSONArray blipsList = (JSONArray) json.get("blips");
            blipsList.forEach(blipJson -> {
                RawBlip blip = parseItem((JSONObject) blipJson, date);
                radar.add(blip);
            });
        });

        return radar;
    }

    public LocalDate parseDate(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(string+"-01", formatter);
    }

    private RawBlip parseItem(JSONObject jsonObject, LocalDate date) {
        String name = (String) jsonObject.get("name");
        String rawRing = (String) jsonObject.get("ring");
        int id = Integer.parseInt((String) jsonObject.get("id"));

        Ring ring = Ring.valueOf(rawRing);
        return new RawBlip(id, name, date, ring);
    }

    public class RawBlip {

        private final int id;
        private final String name;
        private final LocalDate date;
        private final Ring ring;

        public RawBlip(int id, String name, LocalDate date, Ring ring) {

            this.id = id;
            this.name = name;
            this.date = date;
            this.ring = ring;
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

        public int getId() {
            return id;
        }
    }

}
