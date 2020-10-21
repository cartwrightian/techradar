package com.thoughtworks.radar;

import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class RadarFactoryTest {

    @Test
    public void shouldCreateFromFile() throws IOException, ParseException {
        Path path = Paths.get("data","blips.json");

        JsonFromFile loader = new JsonFromFile(path);
        Parser parser = new Parser();
        RadarFactory factor = new RadarFactory(loader, parser);
        Radars result = factor.loadRadar();

        assertEquals(1097,result.getBlips().size());

        result.getBlips().forEach(blip -> assertTrue(blip.getHistory().size()>0));
    }

}