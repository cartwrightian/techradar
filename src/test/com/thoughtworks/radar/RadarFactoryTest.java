package com.thoughtworks.radar;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RadarFactoryTest {

    @Test
    public void shouldCreateFromFile() throws IOException {
        Path path = Paths.get("data","blips.json");

        JsonFromFile loader = new JsonFromFile(path);
        JsonParser parser = new JsonParser();
        RadarFactory factor = new RadarFactory(loader, parser);
        Radars result = factor.loadRadar();

        assertEquals(1460,result.getBlips().size());

        result.getBlips().forEach(blip -> assertTrue(blip.getHistory().size()>0));
    }

}