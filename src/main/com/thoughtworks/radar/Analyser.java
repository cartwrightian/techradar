package com.thoughtworks.radar;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Analyser {
    private Radars radars;

    public Analyser(Radars radars) {
        this.radars = radars;
    }

    // pass in filter
    public List<BlipLifetime> lifeTimes() {

        List<BlipLifetime> results = new LinkedList<>();

        radars.getBlips().forEach(blip -> {
            LocalDate appearedDate = blip.appearedDate();
            LocalDate firstFaded = blip.firstFadedDate();
            BlipLifetime blipLifetime = new BlipLifetime(blip.getName(), blip.getId(), blip.getQuadrant(), appearedDate,
                    firstFaded, radars.getEditionFrom(appearedDate), radars.getEditionFrom(firstFaded), blip.fadedRing());
            results.add(blipLifetime);
        });

        return results;
    }

    public Map<Integer, List<Integer>> summaryOfDecay(BlipFilter blipFilter) {

        // edition -> number blips left ordred by edition
        HashMap<Integer, List<Integer>> result = new HashMap<>();

        int count = radars.numberOfRadars();

        List<BlipLifetime> lifeTimes = lifeTimes();

        radars.forEachEdition((edition, published) -> {
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

        // radars editions
        radars.forEachEdition((edition, published) -> {
            List<Integer> decaysForRadar = summary.get(edition);
            int index = edition - 1;
            int radarDays = Math.toIntExact(published.toEpochDay());
            Integer initialCount = decaysForRadar.get(index);

            Quartiles quartiles = new Quartiles();
            for (int quater=1; quater<=4; quater++) {
                int target = quater * initialCount;

                long halflife =  Integer.MAX_VALUE;
                for (int futureEditions = index; futureEditions < radarCount; futureEditions++) { // zero indexed
                    int current = decaysForRadar.get(futureEditions); // how many blips from radars left now?
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
        return radars.dateOfEdition(radarB).toEpochDay()- radars.dateOfEdition(radarA).toEpochDay();
    }

    // Integer -> Ratio new to existing
    public Map<Integer, Double> summaryOfNew(BlipFilter blipFilter) {
        TreeMap<Integer, Double> result = new TreeMap<>();

        List<BlipLifetime> filteredLifetimes = lifeTimes().stream().
                filter(blipFilter::filter)
                .collect(Collectors.toList());

        // indexes
        radars.forEachEdition((edition, published) -> {
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

        radars.forEachEdition((num, date) -> radars.blipsVisibleOn(num).forEach(blip -> {
            SummaryText summaryText = new SummaryText(blip.getName(), date, blip.ringFor(num), blip.getQuadrant(),
                    blip.getDescription(), blip.idOnRadar(num));
            sorted.add(summaryText);
        }));

        LinkedList<SummaryText> results = new LinkedList<>();
        results.addAll(sorted);
        return results;
    }

    public String allWordsFromDescriptions() {
        StringBuilder results = new StringBuilder();
        radars.getBlips().forEach(blip -> blip.getHistory().forEach(blipHistory ->
            {results.append(" ").append(blipHistory.getDescription());}));

        return results.toString();
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
                long count = radars.blipCount(new BlipFilter().allow(quadrant).allow(ring));
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

        radars.forEachEdition((number, date) -> {
            SimpleCSV line = new SimpleCSV();
            line.add(number.toString());

            for (Ring ring : Ring.values()) {
                BlipFilter filter = new BlipFilter().allow(ring).allow(Quadrant.values());
                Long count = radars.blipCount(date,filter);
                line.add(count.toString());
            }

            for (Quadrant quadrant : Quadrant.values()) {
                BlipFilter filter = new BlipFilter().allow(quadrant).allow(Ring.values());
                long count = radars.blipCount(date,filter);
                line.add(Long.toString(count));
            }

            results.add(line);
        });

        return results;
    }

}
