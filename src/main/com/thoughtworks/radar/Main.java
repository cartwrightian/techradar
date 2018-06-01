package com.thoughtworks.radar;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        Path path = Paths.get("data","blips.json");

        JsonFromFile jsonFromFile = new JsonFromFile(path);
        Parser parser = new Parser();

        Radar radar = new RadarFactory(jsonFromFile,parser).loadRadar();

        Analyser analyser = new Analyser(radar);

        ResultsWriter writer = new ResultsWriter(Paths.get("data","output.csv"));
        List<BlipLifetime> results = analyser.lifeTimes();
        writer.write(results);

        ResultsWriter decayWriter = new ResultsWriter(Paths.get("data","decays.csv"));
        Map<Integer, List<Integer>> decays = analyser.summaryOfDecay(Quadrant.values());
        decayWriter.write(decays);
        
        for(Quadrant quadrant : Quadrant.values()) {
            ResultsWriter quadWriter = new ResultsWriter(Paths.get("data","decays-"+quadrant.toString()+".csv"));
            quadWriter.write(analyser.summaryOfDecay(quadrant));
        }

        Map<Integer, Integer> halfLives = analyser.findHalfLife(Quadrant.values());
        ResultsWriter halflifeWriter = new ResultsWriter(Paths.get("data", "halflife.csv"));
        halflifeWriter.writeSummary(halfLives);

        for(Quadrant quadrant : Quadrant.values()) {
            ResultsWriter quadWriter = new ResultsWriter(Paths.get("data","halflife-"+quadrant.toString()+".csv"));
            quadWriter.writeSummary(analyser.findHalfLife(quadrant));
        }

    }
}
