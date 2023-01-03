package com.thoughtworks.radar;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// https://www.thoughtworks.com/internal/api/radar/blips
// https://www.thoughtworks.com/caas/api/insights/radar/blips.en.json

public class JsonFromFileTest {

    @Test
    public void shouldLoadJson() throws IOException {

        Path path = Paths.get("data","blips.json");

        JsonFromFile loader = new JsonFromFile(path);
        String result = loader.load();

        assertFalse(result.isEmpty());
        assertTrue(result.contains("blips"));
    }

}