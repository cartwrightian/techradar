package com.thoughtworks.radar.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public enum Ring {

    // note: new format changed ring names, hence the text here
    Trial("trial"),
    Assess("assess"),
    Adopt("adopt"),
    Hold("hold");

    private static final Map<String, Ring> theMap = new HashMap<>();

    static {
        Arrays.stream(Ring.values()).forEach(item -> theMap.put(item.text, item));
    }

    private final String text;

    Ring(String text) {
        this.text = text;
    }

    public static void foreach(Consumer<Ring> action) {
        for(Ring ring :Ring.values()) {
            action.accept(ring);
        }
    }

    public static Ring parse(String text) {
        if (!theMap.containsKey(text)) {
            throw new RuntimeException("Missing ring " + text);
        }
        return theMap.get(text);
    }
}
