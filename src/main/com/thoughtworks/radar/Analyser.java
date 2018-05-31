package com.thoughtworks.radar;

import java.time.LocalDate;
import java.util.*;

public class Analyser {
    private Radar radar;

    public Analyser(Radar radar) {
        this.radar = radar;
    }

    public List<BlipLifetime> lifeTimes() {

        List<BlipLifetime> results = new LinkedList<>();

        Map<LocalDate, Integer> radarNumbers = countRadars();

        radar.getBlips().forEach(blip -> {
            LocalDate appearedDate = blip.appearedDate();
            LocalDate lastDate = blip.lastDate();
            BlipLifetime blipLifetime = new BlipLifetime(blip.getName(), blip.getId(), blip.getQuadrant(), appearedDate, lastDate,
                    radarNumbers.get(appearedDate), radarNumbers.get(lastDate));
            results.add(blipLifetime);
        });

        return results;
    }

    public Map<LocalDate, Integer> countRadars() {
        List<LocalDate> dates = new LinkedList<>();

        radar.getBlips().forEach(blip -> blip.getHistory().forEach(item -> {
            if (!dates.contains(item.getDate())) {
                dates.add(item.getDate());
            }
        }));

        Collections.sort(dates);

        Map<LocalDate, Integer> result = new TreeMap<>();
        for (int i = 0; i < dates.size(); i++) {
            result.put(dates.get(i),i+1);
        }

        return result;
    }

    public Map<Integer, List<Integer>> summaryOfDecay(Quadrant...quadrants) {
        List<Quadrant> filter =  Arrays.asList(quadrants);

        HashMap<Integer, List<Integer>> result = new HashMap<>();

        Map<LocalDate, Integer> radarIndex = countRadars();
        int count = radarIndex.size();

        List<BlipLifetime> lifeTimes = lifeTimes();

        radarIndex.forEach((date,index) -> {
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
}
