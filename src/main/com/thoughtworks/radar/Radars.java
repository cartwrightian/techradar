package com.thoughtworks.radar;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Radars {
    // blip ID to blip
    private SortedMap<Integer,Blip> blips;

    SortedSet<LocalDate> dates;

    public Radars() {
        blips = new TreeMap();
        dates = new TreeSet<>();
    }

    public List<Blip> getBlips() {
        List<Blip> result = new LinkedList<>();
        result.addAll(blips.values());
        return result;
    }

    public void add(Parser.RawBlip rawBlip) {
        int id = rawBlip.getId();

        // consolidate multiple entries for a blip into one blip with a history
        Blip blip;
        if (!blips.containsKey(id)) {
            // todo Which version of blip to take description from? First? Last?
            blip = new Blip(id, rawBlip.getName(), rawBlip.getQuadrant());
            blips.put(id, blip);
        } else {
            blip = blips.get(id);
        }

        LocalDate blipDate = rawBlip.getDate();
        blip.addHistory(new BlipHistory(blipDate, rawBlip.getRing(), rawBlip.getDescription(), rawBlip.getRadarId()));
        dates.add(blipDate);

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

    public List<Blip> blipsVisibleOn(LocalDate date) {
        return blips.values().stream().filter(blip -> blip.visibleOn(date)).collect(Collectors.toList());
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

    public interface EachEdition {
        void edition(Integer number, LocalDate published);
    }
}
