package com.thoughtworks.radar;

import java.io.IOException;
import java.time.LocalDate;

public interface RadarJsonParser {
    Radars parse(String rawJson) throws IOException;

    LocalDate parseDate(String string);
}
