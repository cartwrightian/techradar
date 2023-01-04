package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.BlipLifetime;
import com.thoughtworks.radar.domain.Quadrant;
import com.thoughtworks.radar.domain.Ring;
import com.thoughtworks.radar.domain.Volume;

import java.util.*;
import java.util.stream.Collectors;

public class Analyser {
    private final Radars radars;

    public Analyser(Radars radars) {
        this.radars = radars;
    }

    // pass in filter
    public List<BlipLifetime> lifeTimes() {
        return radars.getBlips().stream().
                map(BlipLifetime::new).collect(Collectors.toList());
    }

    public Map<Volume, List<Integer>> summaryOfDecay(BlipLifeTimeFilter blipFilters) {

        // edition -> number blips left ordered by edition
        HashMap<Volume, List<Integer>> result = new HashMap<>();

        int count = radars.numberOfRadars();

        List<BlipLifetime> lifeTimes = lifeTimes();

        radars.forEachVolume(volume -> {
            int volumeNumber = volume.getNumber();
            ArrayList<Integer> stillLeft = initList(count);
            lifeTimes.stream().
                    filter(blipFilters::filter).
                    filter(blipLifetime -> (volume.equals(blipLifetime.getFirstVolume()))).
                    map(BlipLifetime::getFirstFadedVolume).
                    forEach(lastRadar -> increment(stillLeft, volumeNumber, lastRadar));
            result.put(volume, stillLeft);
        });

        return result;
    }

    private void increment(List<Integer> items, int current, Volume last){
        for (int i = current; i <= last.getNumber(); i++) {
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

    public Map<Integer, Quartiles> findHalfLife(BlipLifeTimeFilter blipFilters) {
        Map<Integer, Quartiles> results = new TreeMap<>();

        Map<Volume, List<Integer>> summary = summaryOfDecay(blipFilters);
        int radarCount = summary.size();

        // radars editions
        radars.forEachVolume((volume -> {
            List<Integer> decaysForRadar = summary.get(volume);
            int volumeNumber = volume.getNumber();
            int index = volumeNumber - 1;
            int radarDays = Math.toIntExact(volume.getPublicationDate().toEpochDay());
            Integer initialCount = decaysForRadar.get(index);

            Quartiles quartiles = new Quartiles();
            for (int quater=1; quater<=4; quater++) {
                int target = quater * initialCount;

                long halflife =  Integer.MAX_VALUE;
                for (int futureEditions = index; futureEditions < radarCount; futureEditions++) { // zero indexed
                    int current = decaysForRadar.get(futureEditions); // how many blips from radars left now?
                    if ((4*current)  < target) {
                        int decayedByNumber = futureEditions + 1;
                        halflife = differenceInDays(volumeNumber, decayedByNumber);
                        break;
                    }
                }
                quartiles.add(quater,halflife);
            }
            results.put(radarDays, quartiles);

        }));

        return results;

    }

    private long differenceInDays(int radarA, int radarB) {
        return radars.dateOfEdition(radarB).toEpochDay()- radars.dateOfEdition(radarA).toEpochDay();
    }

    // Integer -> Ratio new to existing
    public Map<Integer, Double> summaryOfNew(BlipLifeTimeFilter blipFilters) {
        TreeMap<Integer, Double> result = new TreeMap<>();

        List<BlipLifetime> filteredLifetimes = lifeTimes().stream().
                filter(blipFilters::filter)
                .collect(Collectors.toList());

        // indexes
        radars.forEachVolume((volume) -> {
            final int volumeNumber = volume.getNumber();

            Double countNew = (double) filteredLifetimes.stream().
                    filter(item -> volume.equals(item.getFirstVolume())).
                    count();

            Double countAll = (double) filteredLifetimes.stream().
                    filter(item -> Volume.greaterOrEquals(volume, item.getFirstVolume()) &&
                        Volume.lessThanOrEquals(volume, item.getFirstFadedVolume())).
                    count();

            Double ratio = countNew/countAll;
            result.put(volumeNumber,ratio);

        });

        return result;
    }

    public LinkedList<SummaryText> createSummaryText() {
        SortedSet<SummaryText> sorted = new TreeSet<>();

        radars.forEachVolume(volume -> radars.blipsVisibleOn(volume).forEach(blip -> {
            SummaryText summaryText = new SummaryText(blip.getName(), volume.getPublicationDate(), blip.ringFor(volume), blip.quadrantFor(volume),
                    blip.getDescription(), blip.idOnRadar(volume));
            sorted.add(summaryText);
        }));

        return new LinkedList<>(sorted);
    }

    public String allWordsFromDescriptions(BlipFilters blipFilters) {
        StringBuilder results = new StringBuilder();
        radars.getBlips().stream().filter(blipFilters::filter).forEach(blip -> blip.getHistory().forEach(blipHistory ->
            {results.append(" ").append(filterText(blipHistory.getDescription()));}));

        return results.toString();
    }

    private String filterText(String rawText) {
        StringBuilder filtered = new StringBuilder();
        String[] words = rawText.split(" ");
        List<String> listOfWords = Arrays.asList(words);
        listOfWords.stream().
                filter(word -> !word.contains("<")).
                filter(word -> !word.contains(">")).
                map(word -> word + " ").forEach(filtered::append);
        return filtered.toString();
    }


    public List<SimpleCSV> counts(boolean firstRing) {
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
                long count = radars.blipCount(new BlipFilters(firstRing).allow(quadrant).allow(ring));
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

        radars.forEachVolume(volume -> {
            SimpleCSV line = new SimpleCSV();
            line.add(Integer.toString(volume.getNumber()));

            for (Ring ring : Ring.values()) {
                BlipFilters filter = new BlipFilters(true).allow(ring).allow(Quadrant.values());
                long count = radars.blipCount(volume, filter);
                line.add(Long.toString(count));
            }

            for (Quadrant quadrant : Quadrant.values()) {
                BlipFilters filter = new BlipFilters(true).allow(quadrant).allow(Ring.values());
                long count = radars.blipCount(volume, filter);
                line.add(Long.toString(count));
            }

            results.add(line);
        });

        return results;
    }

}
