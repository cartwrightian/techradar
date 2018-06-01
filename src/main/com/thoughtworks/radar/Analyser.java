package com.thoughtworks.radar;

import java.time.LocalDate;
import java.util.*;

public class Analyser {
    private Radar radar;
    private Map<Integer,LocalDate> numberToDate;
    private Map<LocalDate,Integer> dateToNumber;

    public Analyser(Radar radar) {
        this.radar = radar;
        numberToDate = new TreeMap<>();
        dateToNumber = new TreeMap<>();
        indexRadars();
    }

    public List<BlipLifetime> lifeTimes() {

        List<BlipLifetime> results = new LinkedList<>();

        radar.getBlips().forEach(blip -> {
            LocalDate appearedDate = blip.appearedDate();
            LocalDate lastDate = blip.lastDate();
            BlipLifetime blipLifetime = new BlipLifetime(blip.getName(), blip.getId(), blip.getQuadrant(), appearedDate, lastDate,
                    dateToNumber.get(appearedDate), dateToNumber.get(lastDate));
            results.add(blipLifetime);
        });

        return results;
    }

    private void indexRadars() {
        List<LocalDate> dates = new LinkedList<>();

        radar.getBlips().forEach(blip -> blip.getHistory().forEach(item -> {
            if (!dates.contains(item.getDate())) {
                dates.add(item.getDate());
            }
        }));

        Collections.sort(dates);

        for (int i = 0; i < dates.size(); i++) {
            int radarNumber = i + 1;
            LocalDate radarDate = dates.get(i);
            dateToNumber.put(radarDate, radarNumber);
            numberToDate.put(radarNumber, radarDate);
        }

    }

    public Map<Integer, List<Integer>> summaryOfDecay(Quadrant...quadrants) {
        List<Quadrant> filter =  Arrays.asList(quadrants);

        HashMap<Integer, List<Integer>> result = new HashMap<>();

        int count = dateToNumber.size();

        List<BlipLifetime> lifeTimes = lifeTimes();

        dateToNumber.forEach((date,index) -> {
            ArrayList<Integer> stillLeft = initList(count);
            lifeTimes.stream().
                    filter(time -> filter.contains(time.getQuadrant())).
                    filter(time -> (index.equals(time.getFirst()))).
                    map(BlipLifetime::getLast).
                    forEach(lastRadar -> increment(stillLeft,index, lastRadar));
            result.put(index, stillLeft);
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

    public Map<Integer, Integer> findHalfLife(Quadrant...quadrants) {
        Map<Integer, Integer> results = new TreeMap<>();

        Map<Integer, List<Integer>> summary = summaryOfDecay(quadrants);
        int count = summary.size();

        for (int radarNumber = 1; radarNumber <=count; radarNumber++) {
            List<Integer> decaysForRadar = summary.get(radarNumber);
            int index = radarNumber - 1;
            int radarDays = Math.toIntExact(numberToDate.get(radarNumber).toEpochDay());
            Integer initialCount = decaysForRadar.get(index);
            for (int j = index; j<count ; j++) { // zero indexed
                int current = decaysForRadar.get(j);
                if ((current+current)<initialCount) {
                    int decayedByNumber = j + 1;
                    long halflife = differenceInDays(radarNumber, decayedByNumber);
                    results.put(radarDays, Math.toIntExact(halflife));
                    break;
                }
            }
            // not decayed yet
            if (!results.containsKey(radarDays)) {
                results.put(radarDays, Integer.MAX_VALUE);
            }
        }

        return results;

    }

    private long differenceInDays(int radarA, int radarB) {
        return numberToDate.get(radarB).toEpochDay()-numberToDate.get(radarA).toEpochDay();
    }

    public Map<LocalDate, Integer> getDateToNumberIndex() {
        return dateToNumber;
    }
}
