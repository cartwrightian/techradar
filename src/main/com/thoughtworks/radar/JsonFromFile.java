package com.thoughtworks.radar;

import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonFromFile {
    private Path path;

    public JsonFromFile(Path path) {
        this.path = path;
    }

    public String load() throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        String result = new String(bytes, Charsets.US_ASCII);
        return result;
    }
}
