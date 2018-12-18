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

        // edition -> number blips left ordred by edition
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

    public Map<Integer, Quartiles> findHalfLife(BlipFilter blipFilter) {
        Map<Integer, Quartiles> results = new TreeMap<>();

        Map<Integer, List<Integer>> summary = summaryOfDecay(blipFilter);
        int radarCount = summary.size();

        // radar editions
        radar.forEachEdition((edition,published) -> {
            List<Integer> decaysForRadar = summary.get(edition);
            int index = edition - 1;
            int radarDays = Math.toIntExact(published.toEpochDay());
            Integer initialCount = decaysForRadar.get(index);

            Quartiles quartiles = new Quartiles();
            for (int quater=1; quater<=4; quater++) {
                int target = quater * initialCount;

                long halflife =  Integer.MAX_VALUE;
                for (int futureEditions = index; futureEditions < radarCount; futureEditions++) { // zero indexed
                    int current = decaysForRadar.get(futureEditions); // how many blips from radar left now?
                    if ((4*current)  < target) {
                        int decayedByNumber = futureEditions + 1;
                        halflife = differenceInDays(edition, decayedByNumber);
                        break;
                    }
                }
                quartiles.add(quater,halflife);
            }
            results.put(radarDays, quartiles);

        });

        return results;

    }

    private long differenceInDays(int radarA, int radarB) {
        return radar.dateOfEdition(radarB).toEpochDay()-radar.dateOfEdition(radarA).toEpochDay();
    }

    public Map<Integer, Double> summaryOfNew(BlipFilter blipFilter) {
        TreeMap<Integer, Double> result = new TreeMap<>();

        List<BlipLifetime> filteredLifetimes = lifeTimes().stream().
                filter(blipFilter::filter)
                .collect(Collectors.toList());

        // indexes
        radar.forEachEdition((edition,published) -> {
            Double countNew = (double) filteredLifetimes.stream().
                    filter(item -> edition.equals(item.getFirstRadarNum())).
                    count();
            Double countAll = (double) filteredLifetimes.stream().
                    filter(item -> (edition >= item.getFirstRadarNum() && edition <= item.getLastRadarNum())).
                    count();
            Double ratio = countNew/countAll;
            result.put(edition,ratio);

        });

        return result;
    }

    public LinkedList<SummaryText> createSummaryText() {
        SortedSet<SummaryText> sorted = new TreeSet<>();

        radar.forEachEdition((num, date) -> radar.blipsVisibleOn(num).forEach(blip -> {
            SummaryText summaryText = new SummaryText(blip.getName(), date, blip.ringFor(num), blip.getQuadrant(),
                    blip.getDescription(), blip.idOnRadar(num));
            sorted.add(summaryText);
        }));

        LinkedList<SummaryText> results = new LinkedList<>();
        results.addAll(sorted);
        return results;
    }

    public List<SimpleCSV> counts() {
        List<SimpleCSV> counts = new LinkedList<>();
        SimpleCSV header = new SimpleCSV();

        header.add("Quadrant");
        for (Ring ring : Ring.values()) {
            header.add(ring.toString());
        }
        counts.add(header);

        for (Quadrant quadrant : Quadrant.values()) {
            SimpleCSV line = new SimpleCSV();
            line.add(quadrant.toString());
            for (Ring ring : Ring.values()) {
                long count = radar.blipCount(new BlipFilter().allow(quadrant).allow(ring));
                line.add(Long.toString(count));
            }
            counts.add(line);
        }

        return counts;
    }

    public List<SimpleCSV> countByRadar() {
        List<SimpleCSV> results = new LinkedList<>();

        SimpleCSV header = new SimpleCSV();
        header.add("Edition");
        for (Ring ring : Ring.values()) {
            header.add(ring.toString());
        }
        for (Quadrant quadrant : Quadrant.values()) {
            header.add(quadrant.toString());
        }
        results.add(header);

        radar.forEachEdition((number,date) -> {
            SimpleCSV line = new SimpleCSV();
            line.add(number.toString());

            for (Ring ring : Ring.values()) {
                BlipFilter filter = new BlipFilter().allow(ring).allow(Quadrant.values());
                Long count = radar.blipCount(date,filter);
                line.add(count.toString());
            }

            for (Quadrant quadrant : Quadrant.values()) {
                BlipFilter filter = new BlipFilter().allow(quadrant).allow(Ring.values());
                long count = radar.blipCount(date,filter);
                line.add(Long.toString(count));
            }

            results.add(line);
        });

        return results;
    }
}
