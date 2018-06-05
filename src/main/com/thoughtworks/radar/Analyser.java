package com.thoughtworks.radar;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Analyser {
    private Radars radar;

    public Analyser(Radars radar) {
        this.radar = radar;
    }

    // pass in filter
    public List<BlipLifetime> lifeTimes() {

        List<BlipLifetime> results = new LinkedList<>();

        radar.getBlips().forEach(blip -> {
            LocalDate appearedDate = blip.appearedDate();
            LocalDate lastDate = blip.lastDate();
            BlipLifetime blipLifetime = new BlipLifetime(blip.getName(), blip.getId(), blip.getQuadrant(), appearedDate,
                    lastDate, radar.getEditionFrom(appearedDate), radar.getEditionFrom(lastDate), blip.lastRing());
            results.add(blipLifetime);
        });

        return results;
    }

    public Map<Integer, List<Integer>> summaryOfDecay(BlipFilter blipFilter) {

        HashMap<Integer, List<Integer>> result = new HashMap<>();

        int count = radar.numberOfRadars();

        List<BlipLifetime> lifeTimes = lifeTimes();

        radar.forEachEdition((edition, published) -> {
            ArrayList<Integer> stillLeft = initList(count);
            lifeTimes.stream().
                    filter(time -> blipFilter.filter(time)).
                    filter(time -> (edition.equals(time.getFirstRadarNum()))).
                    map(BlipLifetime::getLastRadarNum).
                    forEach(lastRadar -> increment(stillLeft,edition, lastRadar));
            result.put(edition, stillLeft);
        });

        return result;
    }

    private void increment(List<Integer> items, int current, int last){
        for (int i = current; i <= last; i++) {
            int index = i-1;
            int total=items.get(index)+1;
            items.set(index, total);
        }
    }

    private ArrayList<Integer> initList(int count) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(0);
        }
        return result;
    }

    public Map<Integer, Integer> findHalfLife(BlipFilter blipFilter) {
        Map<Integer, Integer> results = new TreeMap<>();

        Map<Integer, List<Integer>> summary = summaryOfDecay(blipFilter);
        int count = summary.size();

        // radar editions
        radar.forEachEdition((edition,published) -> {
            List<Integer> decaysForRadar = summary.get(edition);
            int index = edition - 1;
            int radarDays = Math.toIntExact(published.toEpochDay());
            Integer initialCount = decaysForRadar.get(index);
            for (int j = index; j<count ; j++) { // zero indexed
                int current = decaysForRadar.get(j);
                if ((current+current)<initialCount) {
                    int decayedByNumber = j + 1;
                    long halflife = differenceInDays(edition, decayedByNumber);
                    results.put(radarDays, Math.toIntExact(halflife));
                    break;
                }
            }
            // not decayed yet
            if (!results.containsKey(radarDays)) {
                results.put(radarDays, Integer.MAX_VALUE);
            }
        });

        return results;

    }

    private long differenceInDays(int radarA, int radarB) {
        return radar.dateOfEdition(radarB).toEpochDay()-radar.dateOfEdition(radarA).toEpochDay();
    }

    public Map<Integer, Double> summaryOfNew(BlipFilter blipFilter) {
        TreeMap<Integer, Double> result = new TreeMap<>();

        List<BlipLifetime> filteredLifetimes = lifeTimes().stream().
                filter(item -> blipFilter.filter(item))
                .collect(Collectors.toList());

        // indexes
        radar.forEachEdition((edition,published) -> {
            Double countNew = Double.valueOf(filteredLifetimes.stream().
                    filter(item -> edition.equals(item.getFirstRadarNum())).
                    count());
            Double countAll = Double.valueOf(filteredLifetimes.stream().
                    filter(item -> (edition >= item.getFirstRadarNum() && edition<=item.getLastRadarNum())).
                    count());
            Double ratio = countNew/countAll;
            result.put(edition,ratio);

        });

        return result;
    }

    public List<SummaryText> createSummaryText() {
        List<SummaryText> results = new LinkedList();

        radar.forEachEdition((num, date) -> radar.getBlipForRadarOn(date).forEach(blip -> {
                results.add(new SummaryText(blip.getId(), date, blip.firstRing(), blip.getQuadrant(),
                        blip.getDescription()));
        }));

        return results;
    }
}
