package com.thoughtworks.radar;

import java.util.function.Consumer;

public enum Quadrant {
    tools,
    LanguagesAndFrameworks,
    techniques,
    platforms;

    public static Quadrant fromString(String text) {
        String lower = text.toLowerCase();
        if ("languages-and-frameworks".equals(lower)) {
            return LanguagesAndFrameworks;
        }
        return Quadrant.valueOf(lower);
    }

    public static void foreach(Consumer<Quadrant> action) {
        for(Quadrant quadrant :Quadrant.values()) {
            action.accept(quadrant);
        }
    }
}
