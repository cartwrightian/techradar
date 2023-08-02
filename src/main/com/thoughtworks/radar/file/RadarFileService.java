package com.thoughtworks.radar.file;

import com.thoughtworks.radar.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RadarFileService {

    private final String folder;

    public RadarFileService(String folder) {
        this.folder = folder;
    }

    public Radars loadRadars() throws IOException {
        Path path = Paths.get(folder,"blips.json");

        JsonFromFile jsonFromFile = new JsonFromFile(path);
        ParserNewFormat parser = new ParserNewFormat();

        return new RadarFactory(jsonFromFile,parser).loadRadar();
    }
}
