package com.thoughtworks.radar.integration;

import com.thoughtworks.radar.*;
import com.thoughtworks.radar.domain.Blip;
import com.thoughtworks.radar.domain.BlipEntry;
import com.thoughtworks.radar.domain.Volume;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FullParseTest {

    @Test
    public void shouldLoadRadarDataAndCreateAnalysis() throws IOException {
        String folder = "data";
        Path path = Paths.get(folder,"blips.json");

        JsonFromFile jsonFromFile = new JsonFromFile(path);
        Parser parser = new Parser();

        Radars radars = new RadarFactory(jsonFromFile,parser).loadRadar();

        assertEquals(28, radars.numberOfRadars());
        assertEquals(LocalDate.of(2010,1,1), radars.dateOfEdition(1));

        LocalDate latestEditionReleaseDate = LocalDate.of(2018, 11, 1);

        assertEquals(latestEditionReleaseDate, radars.dateOfEdition(19));
        assertEquals(19, radars.getEditionFrom(latestEditionReleaseDate).getNumber());

        radars.forEachVolume(System.out::println);

        // number blips appeared in latest
        Volume latestVolume = radars.getEditionFrom(latestEditionReleaseDate);
        assertEquals(19, latestVolume.getNumber());
        assertEquals(73, radars.blipCount(latestVolume, BlipFilters.All()));

        // aws appeared, faded, came back
        List<Blip> awsBlips = radars.getBlips().stream().filter(blip -> blip.getName().equals("AWS")).collect(Collectors.toList());
        assertEquals(1, awsBlips.size());

        Blip aws = awsBlips.get(0);
        Collection<BlipEntry> awsHistory = aws.getHistory();

        assertEquals(3, awsHistory.size());

        // azure, radar, cameback
        List<Blip> azureBlips = radars.getBlips().stream().filter(blip -> blip.getName().equals("Azure")).collect(Collectors.toList());
        assertEquals(1, azureBlips.size());

        Blip azure = azureBlips.get(0);
        Duration duration = azure.getDuration();
        assertEquals(671,duration.toDays());

        List<Blip> fadedAtSomePoint = radars.getBlips().stream().
                filter(blip -> (
                        blip.lastDate().toEpochDay() - blip.appearedDate().toEpochDay()) != blip.getDuration().toDays())
                .collect(Collectors.toList());
        assertEquals(171, fadedAtSomePoint.size());

        fadedAtSomePoint.forEach(blip -> System.out.println(blip.getName()));


    }
}
