package com.thoughtworks.radar;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        String folder = "data";
        Path path = Paths.get(folder,"blips.json");

        JsonFromFile jsonFromFile = new JsonFromFile(path);
        Parser parser = new Parser();

        Radars radar = new RadarFactory(jsonFromFile,parser).loadRadar();

        Analyser analyser = new Analyser(radar);

        // total blip counts
        ResultsWriter countsWriter = new ResultsWriter(getPath(folder,"counts.csv"));
        List<SimpleCSV> counts = analyser.counts();
        countsWriter.write(counts);

        // appearing blip counts
        ResultsWriter trendsWriter = new ResultsWriter(getPath(folder,"countsByEdition.csv"));
        List<SimpleCSV> countByRadar = analyser.countByRadar();
        trendsWriter.write(countByRadar);

        // raw csv
        ResultsWriter writer = new ResultsWriter(Paths.get(folder,"output.csv"));
        List<BlipLifetime> results = analyser.lifeTimes();
        writer.write(results);

        //// decays
        ResultsWriter decayWriter = new ResultsWriter(Paths.get(folder,"decays.csv"));
        Map<Integer, List<Integer>> decays = analyser.summaryOfDecay(BlipFilter.All());
        decayWriter.write(decays);

        for(Quadrant quadrant : Quadrant.values()) {
            ResultsWriter quadWriter = new ResultsWriter(Paths.get(folder,"decays-"+quadrant.toString()+".csv"));
            quadWriter.write(analyser.summaryOfDecay(filterByQuad(quadrant)));
        }

        for(Ring ring : Ring.values()) {
            ResultsWriter ringWriter = new ResultsWriter(Paths.get(folder,"decays-"+ring.toString()+".csv"));
            ringWriter.write(analyser.summaryOfDecay(filterByRing(ring)));
        }

        //// Half lifes
        Map<Integer, Quartiles> halfLives = analyser.findHalfLife(BlipFilter.All());
        ResultsWriter halflifeWriter = new ResultsWriter(Paths.get(folder, "halflife.csv"));
        halflifeWriter.writeSummary(halfLives);

        for(Quadrant quadrant : Quadrant.values()) {
            ResultsWriter quadWriter = new ResultsWriter(Paths.get(folder,"halflife-"+quadrant.toString()+".csv"));
            quadWriter.writeSummary(analyser.findHalfLife(filterByQuad(quadrant)));
        }

        for(Ring ring : Ring.values()) {
            ResultsWriter ringWriter = new ResultsWriter(Paths.get(folder,"halflife-"+ring.toString()+".csv"));
            ringWriter.writeSummary(analyser.findHalfLife(filterByRing(ring)));
        }

        //// summary of new
        ResultsWriter newBlipsWriter = new ResultsWriter(Paths.get(folder, "newblips.csv"));
        newBlipsWriter.writeFigures(analyser.summaryOfNew(BlipFilter.All()));

        for(Quadrant quadrant : Quadrant.values()) {
            ResultsWriter quadWriter = new ResultsWriter(Paths.get(folder,"newblips-"+quadrant.toString()+".csv"));
            quadWriter.writeFigures((analyser.summaryOfNew(filterByQuad(quadrant))));
        }

        //// radar cheat sheet text
        ResultsWriter summaryWriter = new ResultsWriter(Paths.get(folder, "summaryText.csv"));
        summaryWriter.write(analyser.createSummaryText());
    }

    private static Path getPath(String folder, String filename) {
        return Paths.get(folder,filename);
    }


    private static BlipFilter filterByRing(Ring ring) {
        return new BlipFilter().allow(Quadrant.values()).allow(ring);
    }

    private static BlipFilter filterByQuad(Quadrant quadrant) {
        return new BlipFilter().allow(quadrant).allow(Ring.values());
    }
}
