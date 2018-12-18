package com.thoughtworks.radar;

import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class ResultsWriter {
    private Path path;
    private final String lineSep;

    public ResultsWriter(Path path) throws IOException {
        lineSep = System.getProperty("line.separator");
        this.path = path;

        if (Files.exists(path)){
            Files.delete(path);
        }
    }

    public void write(List<? extends ToCSV> items) throws IOException {
        StringBuilder builder = new StringBuilder();
        items.forEach(item -> builder.append(item.toCSV()).append(lineSep));
        writeBytesToFile(builder);
    }

    private void writeBytesToFile(StringBuilder builder) throws IOException {
        byte[] bytes = builder.toString().getBytes(Charsets.UTF_8);
        Files.write(path, bytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
    }

    public void write(Map<Integer, List<Integer>> decays) throws IOException {
        StringBuilder builder = new StringBuilder();
        // header
        builder.append("\"\"");
        for (int i = 1; i <= decays.size(); i++) {
            builder.append(",").append(i);
        }
        builder.append(lineSep);
        // values
        decays.forEach((edition,blipDecay) -> builder.append(edition).append(',').append(formatList(blipDecay)).append(lineSep));
        writeBytesToFile(builder);
    }

    private String formatList(List<Integer> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach(value -> {
            if (builder.length()>0) {
                builder.append(',');
            }
            if (value>0) {
                builder.append(value);
            } else {
                builder.append("\"\"");
            }
        });
        return builder.toString();
    }

    public void writeSummary(Map<Integer, Quartiles> halfLives) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("Released,100%,75%,50%,25%").append(lineSep);
        halfLives.forEach((day,quartiles) -> {
            builder.append(format("%s", day));
            for (int quarter = 1; quarter <=4; quarter++) {
                long halflife = quartiles.get(quarter);
                builder.append(",");
                if (halflife!=Integer.MAX_VALUE) {
                    builder.append(halflife);
                } else {
                    builder.append("\"\"");
                }
            }

            builder.append(lineSep);
        });


        writeBytesToFile(builder);
    }

    public void writeFigures(Map<Integer, Double> percentageNew) throws IOException {
        StringBuilder builder = new StringBuilder();
        percentageNew.forEach((index,value) -> builder.append(format("%s,%.3f", index,value)).append(lineSep));
        writeBytesToFile(builder);
    }
}
