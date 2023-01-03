package com.thoughtworks.radar.repository;

import com.thoughtworks.radar.Parser;
import com.thoughtworks.radar.domain.Blip;
import com.thoughtworks.radar.domain.BlipEntry;
import com.thoughtworks.radar.domain.UniqueBlipId;
import com.thoughtworks.radar.domain.Volume;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

public class BlipRepository {
    private final SortedMap<UniqueBlipId, Blip> blips;

    public BlipRepository() {
        blips = new TreeMap<>();
    }

    public BlipRepository(List<Blip> list) {
        this.blips = new TreeMap<>();
        list.forEach(blip -> blips.put(blip.getId(), blip));
    }

    public List<Blip> getAll() {
        return new LinkedList<>(blips.values());
    }

    public Stream<Blip> stream() {
        return blips.values().stream();
    }

    public Blip get(UniqueBlipId blipId) {
        return blips.get(blipId);
    }

    public void add(Volume volume, Parser.RawBlip rawBlip) {
        UniqueBlipId uniqueBlipId = rawBlip.getId();

        // consolidate multiple entries for a blip into one blip with a history
        if (!blips.containsKey(uniqueBlipId)) {
            blips.put(uniqueBlipId, new Blip(uniqueBlipId, rawBlip.getName()));
        }
        Blip blip = blips.get(uniqueBlipId);

        BlipEntry blipEntry = new BlipEntry(uniqueBlipId, volume, rawBlip.getQuadrant(), rawBlip.getRing(), rawBlip.getDescription(), rawBlip.getRadarId());
        blip.addHistory(volume, blipEntry);
    }

    public void updateHistory(List<BlipEntry> blipEntries, VolumeRepository volumeRepository) {
        blipEntries.forEach(blipEntry -> {
            UniqueBlipId uniqueId = blipEntry.getUniqueId();
            Volume volume = volumeRepository.getVolumeFor(blipEntry.getDate());
            Blip blip = blips.get(uniqueId);
            blip.addHistory(volume, blipEntry);
        });
    }
}
