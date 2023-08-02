package com.thoughtworks.radar.integration;

import com.thoughtworks.radar.*;
import com.thoughtworks.radar.domain.Blip;
import com.thoughtworks.radar.domain.BlipEntry;
import com.thoughtworks.radar.domain.UniqueBlipId;
import com.thoughtworks.radar.domain.Volume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class ParserTest {

    private Radars radars;

    @BeforeEach
    public void onceBeforeEachTestRuns() throws IOException {
        String folder = "data";
        Path path = Paths.get(folder,"blips.json");

        JsonFromFile jsonFromFile = new JsonFromFile(path);
        JsonParser parser = new JsonParser();

        radars = new RadarFactory(jsonFromFile,parser).loadRadar();
    }

    @Test
    public void shouldLoadRadarDataAndCreateAnalysis() {

        assertEquals(28, radars.numberOfRadars());
        assertEquals(LocalDate.of(2010,1,1), radars.dateOfEdition(1));

        assertEquals(1460, radars.getBlips().size());

        LocalDate specificEdition = LocalDate.of(2018, 11, 1);

        assertEquals(specificEdition, radars.dateOfEdition(19));
        assertEquals(19, radars.getEditionFrom(specificEdition).getNumber());

        radars.forEachVolume(System.out::println);

        // number blips appeared in latest
        Volume latestVolume = radars.getEditionFrom(specificEdition);
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

    @Test
    public void shouldFineBlipByName() {
        List<Blip> blips = new ArrayList<>(radars.findByName("wallaby.js"));

        assertFalse(blips.isEmpty());
        assertEquals(1, blips.size());
        Blip found = blips.get(0);
        assertEquals("Wallaby.js", found.getName());
        assertEquals(UniqueBlipId.from("1284"), found.getId());
        Volume expectedVolume = new Volume(19, LocalDate.of(2018, 11,1));
        assertEquals(expectedVolume, found.getFirstVolume());
    }
}
