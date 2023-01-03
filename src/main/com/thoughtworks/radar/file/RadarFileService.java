package com.thoughtworks.radar.file;

import com.thoughtworks.radar.JsonFromFile;
import com.thoughtworks.radar.Parser;
import com.thoughtworks.radar.RadarFactory;
import com.thoughtworks.radar.Radars;

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
        Parser parser = new Parser();

        return new RadarFactory(jsonFromFile,parser).loadRadar();
    }
}
