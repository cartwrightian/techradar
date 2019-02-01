package com.thoughtworks.radar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BlipFilterOverHistory {
    private List<Ring> rings;

    public BlipFilterOverHistory() {
        rings = new LinkedList<>();
    }

    public BlipFilterOverHistory allow(Ring...toAdd) {
        rings.addAll(Arrays.asList(toAdd));
        return this;
    }


    public boolean filter(Blip blip) {
        return blip.getHistory().stream().filter(blipHistory -> filterHistory(blipHistory)).count() > 0;
    }

    private boolean filterHistory(BlipHistory history) {
        return  rings.contains(history.getRing());
    }
}
