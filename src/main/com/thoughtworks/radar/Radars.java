package com.thoughtworks.radar;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Radars {
    // blip ID -> blip
    private SortedMap<BlipId,Blip> blips;
    private SortedSet<LocalDate> dates;
    private List<BlipHistory> historyToAdd;

    public Radars() {
        blips = new TreeMap<>();
        dates = new TreeSet<>();
        historyToAdd = new LinkedList<>();
    }

    public List<Blip> getBlips() {
        List<Blip> result = new LinkedList<>();
        result.addAll(blips.values());
        return result;
    }

    public void add(Parser.RawBlip rawBlip) {
        BlipId id = rawBlip.getId();

        // consolidate multiple entries for a blip into one blip with a history
        Blip blip;
        if (!blips.containsKey(id)) {
            // todo Which version of blip to take description from? First? Last?
            blip = new Blip(id, rawBlip.getName(), rawBlip.getQuadrant());
            blips.put(id, blip);
        }

        LocalDate blipDate = rawBlip.getDate();
        BlipHistory blipHistory = new BlipHistory(id, blipDate, rawBlip.getRing(), rawBlip.getDescription(),
                rawBlip.getRadarId());
        historyToAdd.add(blipHistory);
        dates.add(blipDate);
    }


    public void updateBlipHistories() {
        // edition -> things that happened in the edition
        Map<Integer, List<BlipHistory>> historyByEdition = new HashMap<>();
        if (historyToAdd.isEmpty()) {
            throw new RuntimeException("Call after add()'ing all of the blips");
        }
        historyToAdd.forEach(blipHistory -> {
            int edition = getEditionFrom(blipHistory.getDate());
            if (!historyByEdition.containsKey(edition)) {
                historyByEdition.put(edition, new LinkedList<>());
            }
            historyByEdition.get(edition).add(blipHistory);
        });
        historyByEdition.forEach((edition,historyList)-> {
            historyList.forEach(history -> { blips.get(history.getBlipId()).addHistory(edition, history);});
        });
        historyToAdd.clear();
    }

    public int numberOfRadars() {
        return dates.size();
    }

    public int getEditionFrom(LocalDate date) {
        if (dates.contains(date)) {
            return dates.headSet(date).size()+1;
        } else {
            throw new IndexOutOfBoundsException("No radar with date "+date);
        }
    }

    public List<Blip> blipsVisibleOn(int edition) {
        return blips.values().stream().filter(blip -> blip.visibleOn(edition)).collect(Collectors.toList());
    }

    public LocalDate dateOfEdition(int editionNumber) {
        if (editionNumber>dates.size()) {
            throw new IndexOutOfBoundsException("No radar with edition number "+editionNumber);
        }

        Iterator<LocalDate> iterator = dates.iterator();
        LocalDate next = iterator.next();
        for (int i = 1; i < editionNumber; i++) {
            next = iterator.next();
        }
        return next;

    }

    public void forEachEdition(EachEdition eachEdition) {
        AtomicInteger count = new AtomicInteger(0);
        dates.forEach(date -> {
            count.incrementAndGet();
            eachEdition.edition(count.get(),date);
        });
    }

    public long blipCount(BlipFilter blipFilter) {
        return blips.values().stream().filter(blipFilter::filter).count();
    }

    public long blipCount(LocalDate date, BlipFilter blipFilter) {
        return blips.values().stream().
                filter(blip -> blip.appearedDate().isEqual(date)).
                filter(blipFilter::filter).
                count();
    }

    public List<Blip> longestOnRadar(BlipFilter blipFilter, int limit) {
        Comparator<? super Blip> comparitor = (Comparator<Blip>) (blipA, blipB) -> blipB.getDuration().compareTo(blipA.getDuration());
        return filterAndSort(blipFilter, comparitor, limit);
    }

    public List<Blip> mostMoves(BlipFilter blipFilter, int limit) {
        Comparator<? super Blip> comparitor =
                (Comparator<Blip>) (blipA, blipB) -> blipB.getNumberBlipMoves().compareTo(blipA.getNumberBlipMoves());
        return filterAndSort(blipFilter, comparitor, limit);
    }

    private List<Blip> filterAndSort(BlipFilter blipFilter, Comparator<? super Blip> comparitor, int limit) {
        return blips.values().stream().
                filter(blipFilter::filter).
                sorted(comparitor).
                limit(limit).
                collect(Collectors.toList());
    }

    public List<Blip> nonMovers() {
        return blips.values().stream().filter(blip->(blip.getNumberBlipMoves()==0)).collect(Collectors.toList());
    }

    public interface EachEdition {
        void edition(Integer number, LocalDate published);
    }
}
