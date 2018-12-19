package com.thoughtworks.radar;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {

    private final String folder;

    private Main(String folder) {
        this.folder = folder;
    }

    public static void main(String[] args) throws IOException, ParseException {
        new Main("data").analyse();
    }

    private void analyse() throws IOException, ParseException {
        Radars radar = loadRadars();
        Analyser analyser = new Analyser(radar);

        // raw csv
        ResultsWriter writer = new ResultsWriter(Paths.get(folder,"output.csv"));
        List<BlipLifetime> results = analyser.lifeTimes();
        writer.write(results);

        counts(analyser);
        decays(analyser);
        halflives(analyser);
        newBlips(analyser);
        longestLived(radar, 10);
        cheatSheet(analyser);
        mostMoves(radar, 10);
    }

    private void cheatSheet(Analyser analyser) {
        //// radar presentation cheat sheet :-)
        ResultsWriter summaryWriter = new ResultsWriter(Paths.get("summaryText.csv"));
        summaryWriter.write(analyser.createSummaryText());
    }

    private void counts(Analyser analyser) {
        // total blip counts
        ResultsWriter countsWriter = getWriter("counts.csv");
        List<SimpleCSV> counts = analyser.counts();
        countsWriter.write(counts);

        // appearing blip counts
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

    private void halflives(Analyser analyser) {
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
        //// decays
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
