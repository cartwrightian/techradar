package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Radars {
    // blip ID -> blip
    private final SortedMap<UniqueBlipId, Blip> blips;
    private final VolumeRepository volumeRepository;

    public Radars(VolumeRepository volumeRepository) {
        this.volumeRepository = volumeRepository;
        blips = new TreeMap<>();
    }

    // warning, stateful class, see add() and updateBlipHistories()
    public List<Blip> getBlips() {
        return new LinkedList<>(blips.values());
    }

    public List<Blip> getBlips(BlipFilter blipFilter) {
        return blips.values().stream().filter(blipFilter::filter).collect(Collectors.toList());
    }

    // add raw blips, then afterwards call updateBlipHistories()
    // stateful and a bit yuk, but there are circular dependencies between blips and blip history
    public void add(Parser.RawBlip rawBlip) {
        UniqueBlipId blipId = rawBlip.getId();

        // consolidate multiple entries for a blip into one blip with a history
        if (!blips.containsKey(blipId)) {
            blips.put(blipId, new Blip(blipId, rawBlip.getName()));
        }
        Blip blip = blips.get(blipId);

        LocalDate blipDate = rawBlip.getDate();
        BlipEntry blipEntry = new BlipEntry(blipId, blipDate, rawBlip.getQuadrant(), rawBlip.getRing(), rawBlip.getDescription(), rawBlip.getRadarId());
        Volume volume = volumeRepository.getVolumeFor(blipDate);
        blip.addHistory(volume, blipEntry);
    }

    public int numberOfRadars() {
        return volumeRepository.size();
    }

    public Volume getEditionFrom(LocalDate date) {
        return volumeRepository.getVolumeFor(date);
    }

    public List<Blip> blipsVisibleOn(Volume volume) {
        return blips.values().stream().filter(blip -> blip.visibleOn(volume)).collect(Collectors.toList());
    }

    public LocalDate dateOfEdition(int editionNumber) {
        return volumeRepository.dateOfEdition(editionNumber);
    }

    public void forEachEdition(EachEdition eachEdition) {
        List<Volume> volumes = volumeRepository.getVolumes();
        volumes.forEach(eachEdition::edition);
    }

    public long blipCount(BlipFilter blipFilters) {
        return blips.values().stream().filter(blipFilters::filter).count();
    }

    public long blipCount(Volume volume, BlipFilter blipFilter) {
        return blips.values().stream().
                filter(blip -> blip.appearedDate().isEqual(volume.getPublicationDate())).
                filter(blipFilter::filter).
                count();
    }

    public List<Blip> longestOnRadar(BlipFilter blipFilter, int limit) {
        Comparator<? super Blip> comparator = (Comparator<Blip>) (blipA, blipB) -> blipB.getDuration().compareTo(blipA.getDuration());
        return filterAndSort(blipFilter, comparator, limit);
    }

    public List<Blip> mostMoves(BlipFilter blipFilter, int limit) {
        Comparator<? super Blip> comparitor =
                (Comparator<Blip>) (blipA, blipB) -> blipB.getNumberBlipMoves().compareTo(blipA.getNumberBlipMoves());
        return filterAndSort(blipFilter, comparitor, limit);
    }

    private List<Blip> filterAndSort(BlipFilter blipFilter, Comparator<? super Blip> comparator, int limit) {
        return blips.values().stream().
                filter(blipFilter::filter).
                sorted(comparator).
                limit(limit).
                collect(Collectors.toList());
    }

    public List<Blip> nonMovers(BlipFilter blipFilter) {
        return blips.values().stream().
                filter(blipFilter::filter).
                filter(blip->(blip.getNumberBlipMoves()==0)).collect(Collectors.toList());
    }

    public VolumeRepository getVolumeRepository() {
        return volumeRepository;
    }

    public Blip getBlip(UniqueBlipId blipId) {
        return blips.get(blipId);
    }

    public interface EachEdition {
        void edition(Volume volume);
    }

    public List<Blip> everInAdoptToHold() {

        List<Blip> endedOnHold = blips.values().stream().filter(blip -> blip.lastRing()==Ring.Hold).collect(Collectors.toList());

        Stream<Blip> everInAdopt = endedOnHold.stream().filter(this::wasEverInAdopt);

        return everInAdopt.collect(Collectors.toList());

    }

    private boolean wasEverInAdopt(Blip blip) {
        long touchedAdopt = blip.getHistory().stream().
                filter(blipHistory -> blipHistory.getRing() == Ring.Adopt).
                count();
        return touchedAdopt > 0;
    }
}
