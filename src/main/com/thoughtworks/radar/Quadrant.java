package com.thoughtworks.radar;

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
}
