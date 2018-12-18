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
                rawBlip.getRadarId(), rawBlip.isFaded());
        historyToAdd.add(blipHistory);
        dates.add(blipDate);
    }


    public void updateBlipHistories() {
        if (historyToAdd.isEmpty()) {
            throw new RuntimeException("Call after add()'ing all of the blips");
        }
        historyToAdd.forEach(blipHistory -> {
            int edition = getEditionFrom(blipHistory.getDate());
            blips.get(blipHistory.getBlipId()).addHistory(edition, blipHistory);
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
        return blips.values().stream().filter(blip -> blipFilter.filter(blip)).count();
    }

    public long blipCount(LocalDate date, BlipFilter blipFilter) {
        return blips.values().stream().
                filter(blip -> blip.appearedDate().isEqual(date)).
                filter(blip->blipFilter.filter(blip)).
                count();
    }

    public List<Blip> longestOnRadar(BlipFilter blipFilter, long limit) {
        Comparator<? super Blip> comparitor = (Comparator<Blip>) (blipA, blipB) -> blipB.getDuration().compareTo(blipA.getDuration());

        return blips.values().stream().
                filter(blip -> blipFilter.filter(blip)).
                sorted(comparitor).
                limit(limit).
                collect(Collectors.toList());
    }


    public interface EachEdition {
        void edition(Integer number, LocalDate published);
    }
}
