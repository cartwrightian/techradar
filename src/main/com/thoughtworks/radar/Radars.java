package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.*;
import com.thoughtworks.radar.repository.BlipRepository;
import com.thoughtworks.radar.repository.VolumeRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Radars {
    // blip ID -> blip
    private final BlipRepository blipRepository;
    private final VolumeRepository volumeRepository;

    public Radars(VolumeRepository volumeRepository, BlipRepository blipRepository) {
        this.volumeRepository = volumeRepository;
        this.blipRepository = blipRepository;
    }

    // warning, stateful class, see add() and updateBlipHistories()
    public List<Blip> getBlips() {
        return blipRepository.getAll();
    }

    public List<Blip> getBlips(BlipFilter blipFilter) {
        return blipRepository.stream().filter(blipFilter::filter).collect(Collectors.toList());
    }

    public void add(Parser.RawBlip rawBlip) {
        LocalDate blipDate = rawBlip.getDate();
        Volume volume = volumeRepository.getVolumeFor(blipDate);
        blipRepository.add(volume, rawBlip);
    }

    public int numberOfRadars() {
        return volumeRepository.size();
    }

    public Volume getEditionFrom(LocalDate date) {
        return volumeRepository.getVolumeFor(date);
    }

    public List<Blip> blipsVisibleOn(Volume volume) {
        return blipRepository.stream().filter(blip -> blip.visibleOn(volume)).collect(Collectors.toList());
    }

    public LocalDate dateOfEdition(int editionNumber) {
        return volumeRepository.dateOfEdition(editionNumber);
    }

    public void forEachEdition(EachEdition eachEdition) {
        List<Volume> volumes = volumeRepository.getVolumes();
        volumes.forEach(eachEdition::edition);
    }

    public long blipCount(BlipFilter blipFilters) {
        return blipRepository.stream().filter(blipFilters::filter).count();
    }

    public long blipCount(Volume volume, BlipFilter blipFilter) {
        return blipRepository.stream().
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
        return blipRepository.stream().
                filter(blipFilter::filter).
                sorted(comparator).
                limit(limit).
                collect(Collectors.toList());
    }

    public List<Blip> nonMovers(BlipFilter blipFilter) {
        return blipRepository.stream().
                filter(blipFilter::filter).
                filter(blip->(blip.getNumberBlipMoves()==0)).collect(Collectors.toList());
    }

    public VolumeRepository getVolumeRepository() {
        return volumeRepository;
    }

    public Blip getBlip(UniqueBlipId blipId) {
        return blipRepository.get(blipId);
    }

    public List<BlipEntry> getBlipEntries() {
        return blipRepository.stream().flatMap(blip -> blip.getHistory().stream()).collect(Collectors.toList());
    }

    public interface EachEdition {
        void edition(Volume volume);
    }

    public List<Blip> everInAdoptToHold() {

        List<Blip> endedOnHold = blipRepository.stream().filter(blip -> blip.lastRing()==Ring.Hold).collect(Collectors.toList());

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
