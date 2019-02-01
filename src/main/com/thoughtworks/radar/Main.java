package com.thoughtworks.radar;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    //// looks for blips jason at data/blips.json, this file is checked in
    //// from inside TW can get blips json from https://www.thoughtworks.com/internal/api/radar/blips

    // TODO first, last, touched ring
    // TODO N at random, on or off vote
    // TODO export into google spreadsheet, up/down voting....??
    // TODO filter based on history, i.e. match on initial and then on history of blip

    private final String folder;

    private Main(String folder) {
        this.folder = folder;
    }

    public static void main(String[] args) throws IOException, ParseException {
        new Main("data").analyse();
    }

    private void analyse() throws IOException, ParseException {
        Radars radars = loadRadars();
        Analyser analyser = new Analyser(radars);

        // raw csv
        ResultsWriter writer = new ResultsWriter(Paths.get(folder,"output.csv"));
        List<BlipLifetime> results = analyser.lifeTimes();
        writer.write(results);

        counts(analyser);
        decays(analyser);
        halflives(analyser);
        newBlips(analyser);
        longestLived(radars, 10);
        cheatSheet(analyser);
        mostMoves(radars, 30);
        allMoves(radars);
        nonMovers(radars);
        movedToHold(radars);
    }

    private void nonMovers(Radars radars) {
        // appeared, never moved
        List nonMovers = radars.nonMovers(BlipFilter.All());
        ResultsWriter summaryWriter = getWriter("nonMovers.csv");
        summaryWriter.write(nonMovers);

        Ring.foreach(ring -> {
            ResultsWriter ringWriter = getRingWriter("nonMovers", ring);
            ringWriter.write(radars.nonMovers(filterByRing(ring)));
        });
    }

    private void movedToHold(Radars radars) {

        BlipFilterOverHistory onHold = new BlipFilterOverHistory().allow(Ring.Hold);

        Ring.foreach(ring -> {
            if (ring!=Ring.Hold) {
                List<Blip> ringBlips = radars.getBlips(filterByRing(ring));
                ResultsWriter ringWriter = getRingWriter("movedToHoldFrom", ring);
                ringWriter.write(ringBlips.stream().filter(onHold::filter).collect(Collectors.toList()));
            }
        });
    }

    private void cheatSheet(Analyser analyser) {
        //// radar presentation cheat sheet :-)
        ResultsWriter summaryWriter = getWriter("summaryText.csv");
        summaryWriter.write(analyser.createSummaryText());
    }

    private void counts(Analyser analyser) {
        //// total blip counts
        ResultsWriter countsWriter = getWriter("counts.csv");
        List<SimpleCSV> counts = analyser.counts();
        countsWriter.write(counts);

        //// appearing blip counts
        ResultsWriter trendsWriter = getWriter("countsByEdition.csv");
        List<SimpleCSV> countByRadar = analyser.countByRadar();
        trendsWriter.write(countByRadar);
    }

    private void newBlips(Analyser analyser) {
        //// summary of new blips
        ResultsWriter newBlipsWriter = getWriter("newblips.csv");
        newBlipsWriter.writeFigures(analyser.summaryOfNew(BlipFilter.All()));

        Quadrant.foreach(quadrant -> {
            ResultsWriter quadWriter = getQuadrantWriter("newblips", quadrant);
            quadWriter.writeFigures((analyser.summaryOfNew(filterByQuad(quadrant))));
        });
    }

    private void longestLived(Radars radar, int limit) {
        //// longest lived, all, then by ring and quadrnt
        String baseName = "longestLived";

        ResultsWriter longestLivedWriter = getWriter(baseName +".csv");
        longestLivedWriter.write(radar.longestOnRadar(BlipFilter.All(), limit));

        Ring.foreach(ring -> {
            ResultsWriter ringWriter = getRingWriter(baseName, ring);
            ringWriter.write(radar.longestOnRadar(filterByRing(ring), limit));
        });

        Quadrant.foreach(quadrant -> {
            ResultsWriter quadrantWriter = getQuadrantWriter(baseName, quadrant);
            quadrantWriter.write(radar.longestOnRadar(filterByQuad(quadrant), limit));
        });
    }

    private void mostMoves(Radars radar, int limit) {
        //// most moves; all, then by ring and quadrnt
        String baseName = "mostMoves";

        ResultsWriter longestLivedWriter = getWriter(baseName +".csv");
        longestLivedWriter.write(radar.mostMoves(BlipFilter.All(), limit));

        Ring.foreach(ring -> {
            ResultsWriter ringWriter = getRingWriter(baseName, ring);
            ringWriter.write(radar.mostMoves(filterByRing(ring), limit));
        });

        Quadrant.foreach(quadrant -> {
            ResultsWriter quadrantWriter = getQuadrantWriter(baseName, quadrant);
            quadrantWriter.write(radar.mostMoves(filterByQuad(quadrant), limit));
        });
    }

    private void allMoves(Radars radars) {
        //// all blips that moved
        String baseName = "allMoves";

        ResultsWriter longestLivedWriter = getWriter(baseName +".csv");
        int limit = radars.getBlips().size() + 1;
        longestLivedWriter.write(radars.mostMoves(BlipFilter.All(), limit));

    }

    private void halflives(Analyser analyser) {
        /// time for 50% of blips from an edition to disappear
        Map<Integer, Quartiles> halfLives = analyser.findHalfLife(BlipFilter.All());
        ResultsWriter halflifeWriter = new ResultsWriter(Paths.get(folder, "halflife.csv"));
        halflifeWriter.writeSummary(halfLives);

        Quadrant.foreach(quadrant -> {
            ResultsWriter quadWriter = getQuadrantWriter("halflife", quadrant);
            quadWriter.writeSummary(analyser.findHalfLife(filterByQuad(quadrant)));
        });

        Ring.foreach(ring ->  {
            ResultsWriter ringWriter = getRingWriter("halflife", ring);
            ringWriter.writeSummary(analyser.findHalfLife(filterByRing(ring)));
        });
    }

    private void decays(Analyser analyser) {
        //// decays, how long it takes blips to disappear
        ResultsWriter decayWriter = getWriter("decays.csv");
        Map<Integer, List<Integer>> decays = analyser.summaryOfDecay(BlipFilter.All());
        decayWriter.write(decays);

        Quadrant.foreach(quadrant ->  {
            ResultsWriter quadWriter = getQuadrantWriter("decays", quadrant);
            quadWriter.write(analyser.summaryOfDecay(filterByQuad(quadrant)));
        });

        Ring.foreach(ring ->  {
            ResultsWriter ringWriter = getRingWriter("decays", ring);
            ringWriter.write(analyser.summaryOfDecay(filterByRing(ring)));
        });
    }

    private Radars loadRadars() throws IOException, ParseException {
        Path path = Paths.get(folder,"blips.json");

        JsonFromFile jsonFromFile = new JsonFromFile(path);
        Parser parser = new Parser();

        return new RadarFactory(jsonFromFile,parser).loadRadar();
    }

    private ResultsWriter getWriter(String filename) {
        return new ResultsWriter(getPath(folder, filename));
    }

    private ResultsWriter getRingWriter(String prefix, Ring ring) {
        return getWriter(String.format("%s-%s.csv", prefix, ring.toString()));
    }

    private ResultsWriter getQuadrantWriter(String prefix, Quadrant quadrant) {
        return getWriter(String.format("%s-%s.csv", prefix, quadrant.toString()));
    }

    private Path getPath(String folder, String filename) {
        return Paths.get(folder,filename);
    }

    private BlipFilter filterByRing(Ring ring) {
        return new BlipFilter().allow(Quadrant.values()).allow(ring);
    }

    private BlipFilter filterByQuad(Quadrant quadrant) {
        return new BlipFilter().allow(quadrant).allow(Ring.values());
    }
}
