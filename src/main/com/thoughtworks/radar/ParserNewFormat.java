package com.thoughtworks.radar;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.radar.domain.Quadrant;
import com.thoughtworks.radar.domain.Ring;
import com.thoughtworks.radar.domain.UniqueBlipId;
import com.thoughtworks.radar.repository.BlipRepository;
import com.thoughtworks.radar.repository.VolumeRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;


public class ParserNewFormat implements RadarJsonParser {

    @Override
    public Radars parse(String rawJson) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        Set<LocalDate> dates = parseDates(objectMapper, rawJson);

        VolumeRepository volumeRepository = new VolumeRepository(dates);
        BlipRepository blipRepository = new BlipRepository();

        JsonNode jsonNode = objectMapper.readTree(rawJson);

        Radars radars = new Radars(volumeRepository, blipRepository);

        jsonNode.forEach(blipNode -> {
            RawBlip blip = parseItem(blipNode);
            radars.add(blip);
        });

//        jsonNode.forEach(radarNode -> {
//            JsonNode dateNode = radarNode.get("date");
//            String rawDate = dateNode.asText();
//            LocalDate date = parseDate(rawDate);
//
//            //Volume volume = volumeRepository.getVolumeFor(date);
//
//            JsonNode blipsNode = radarNode.get("blips");
//            for (JsonNode blipNode : blipsNode) {
//                RawBlip blip = parseItem(blipNode, date);
//                radars.add(blip);
//            }
//
//        });
        
        //radars.updateBlipHistories();

        return radars;
    }

    private Set<LocalDate> parseDates(ObjectMapper objectMapper, String rawJson) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(rawJson);

        Set<LocalDate> results = new HashSet<>();

        jsonNode.forEach(blipNode -> {
            JsonNode dateNode = blipNode.get("volume_date");
            String rawDate = dateNode.asText();
            LocalDate date = parseDate(rawDate);

            results.add(date);

        });

        return results;
    }

    @Override
    public LocalDate parseDate(String string) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(string+"-01", formatter);
    }

    private RawBlip parseItem(JsonNode jsonObject) {
        String name = jsonObject.get("name").asText();
        String rawRing = jsonObject.get("ring").asText();
        String rawQuadrant = jsonObject.get("quadrant").asText();
        UniqueBlipId id = UniqueBlipId.parse(jsonObject.get("blip_id").asText());
        String description = jsonObject.get("description").asText();
        String volumeDataTxt = jsonObject.get("volume_date").asText();
        LocalDate volumeDate = parseDate(volumeDataTxt);

        // format of radar ID was changed at one point
        int radarId = -1;
        if (jsonObject.has("radarId")) {
            radarId = jsonObject.get("radarId").asInt();
        }

        Ring ring = Ring.parse(rawRing);
        Quadrant quadrant = Quadrant.fromString(rawQuadrant);
        return new RawBlip(id, name, volumeDate, ring, quadrant,description, radarId);
    }

}
