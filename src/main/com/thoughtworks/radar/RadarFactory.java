package com.thoughtworks.radar;


import java.io.IOException;

public class RadarFactory {
    private JsonFromFile jsonFromFile;
    private Parser parser;

    public RadarFactory(JsonFromFile jsonFromFile, Parser parser) {
        this.jsonFromFile = jsonFromFile;
        this.parser = parser;
    }

    public Radars loadRadar() throws IOException {
        String text = jsonFromFile.load();
        return parser.parse(text);
    }
}
