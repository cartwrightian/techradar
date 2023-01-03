package com.thoughtworks.radar.repository;

import com.thoughtworks.radar.domain.Volume;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class VolumeRepository {
    private final SortedMap<LocalDate, Volume> volumes;
    private final List<LocalDate> publicationDates;

    public VolumeRepository(Set<LocalDate> dates) {
        volumes = new TreeMap<>();
        publicationDates = dates.stream().sorted().collect(Collectors.toList());
        for (int volumeNumber = 1; volumeNumber <= publicationDates.size(); volumeNumber++) {
            LocalDate publicationDate = publicationDates.get(volumeNumber - 1);
            Volume volume = new Volume(volumeNumber, publicationDate);
            volumes.put(publicationDate, volume);
        }
    }

    public Volume getVolumeFor(LocalDate date) {
        if (!volumes.containsKey(date)) {
            throw new RuntimeException("Missing date " + date + " only have " + volumes);
        }
        return volumes.get(date);
    }

    public int size() {
        return volumes.size();
    }

    public LocalDate dateOfEdition(int volumeNumber) {
        return publicationDates.get(volumeNumber-1);
    }

    public List<Volume> getVolumes() {
        return new ArrayList<>(volumes.values());
    }
}
