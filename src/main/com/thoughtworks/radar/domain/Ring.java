package com.thoughtworks.radar.domain;

import java.util.function.Consumer;

public enum Ring {
    Trial,
    Assess,
    Adopt,
    Hold;

    public static void foreach(Consumer<Ring> action) {
        for(Ring ring :Ring.values()) {
            action.accept(ring);
        }
    }
}
