package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.Volume;
import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class ResultsWriter {
    private final Path path;
    private final String lineSep;

    public ResultsWriter(Path path)  {
        lineSep = System.getProperty("line.separator");
        this.path = path;

        if (Files.exists(path)){
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException("Cannot delete file"+path.toAbsolutePath(), e);
            }
        }
    }

    public void write(List<? extends ToCSV> items) {
        if (items.size()==0) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        String header = items.get(0).getHeader();
        if (!header.isEmpty()) {
            builder.append(header).append(lineSep);
        }
        items.forEach(item -> builder.append(item.toCSV()).append(lineSep));
        writeBytesToFile(builder);
    }

    private void writeBytesToFile(StringBuilder builder)  {
        String string = builder.toString();
        writeStringToFile(string);
    }

    public void writeStringToFile(String string) {
        byte[] bytes = string.getBytes(Charsets.UTF_8);
        try {
            Files.write(path, bytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write to file" +path.toAbsolutePath(), e);
        }
    }

    public void write(Map<Volume, List<Integer>> decays) {
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

    public void writeSummary(Map<Integer, Quartiles> halfLives) {
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

    public void writeFigures(Map<Integer, Double> percentageNew)  {
        StringBuilder builder = new StringBuilder();
        percentageNew.forEach((index,value) -> builder.append(format("%s,%.3f", index,value)).append(lineSep));
        writeBytesToFile(builder);
    }
}
