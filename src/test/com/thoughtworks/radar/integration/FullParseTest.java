package com.thoughtworks.radar.integration;

import com.thoughtworks.radar.*;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

public class FullParseTest {

    @Test
    public void shouldLoadRadarDataAndCreateAnalysis() throws IOException, ParseException {
        String folder = "data";
        Path path = Paths.get(folder,"blips.json");

        JsonFromFile jsonFromFile = new JsonFromFile(path);
        Parser parser = new Parser();

        Radars radar = new RadarFactory(jsonFromFile,parser).loadRadar();

        assertEquals(19, radar.numberOfRadars());
        assertEquals(LocalDate.of(2010,1,1),radar.dateOfEdition(1));
        LocalDate latestEditionReleaseDate = LocalDate.of(2018, 11, 1);
        assertEquals(latestEditionReleaseDate,radar.dateOfEdition(19));
        assertEquals(19,radar.getEditionFrom(latestEditionReleaseDate));

        // number blips appeared
        assertEquals(73,radar.blipCount(latestEditionReleaseDate, BlipFilter.All()));

        // aws appeared, faded, came back
        List<Blip> awsBlips = radar.getBlips().stream().filter(blip -> blip.getName().equals("AWS")).collect(Collectors.toList());
        assertEquals(1, awsBlips.size());

        Blip aws = awsBlips.get(0);
        assertFalse(aws.isCurrentlyFaded());
        Collection<BlipHistory> awsHistory = aws.getHistory();

        assertEquals(3, awsHistory.size());
        assertEquals(2, awsHistory.stream().filter(blipHistory -> blipHistory.isFaded()).count());

        // blips that faded twice?
        List<Blip> fadedAtSomePoint = radar.getBlips().stream().
                filter(blip -> (
                        blip.lastDate().toEpochDay() - blip.appearedDate().toEpochDay()) != blip.getDuration().toDays())
                .collect(Collectors.toList());
        assertEquals(292, fadedAtSomePoint.size());
    }
}
