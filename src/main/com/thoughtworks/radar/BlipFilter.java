package com.thoughtworks.radar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BlipFilter {
    private List<Ring> rings;
    private List<Quadrant> quadrants;

    public BlipFilter() {
        rings = new LinkedList<>();
        quadrants = new LinkedList<>();
    }

    public static BlipFilter All() {
        return new BlipFilter().allow(Quadrant.values()).allow(Ring.values());
    }

    public BlipFilter allow(Quadrant...toAdd) {
        quadrants.addAll(Arrays.asList(toAdd));
        return this;
    }

    public BlipFilter allow(Ring...toAdd) {
        rings.addAll(Arrays.asList(toAdd));
        return this;
    }

    public boolean filter(BlipLifetime item) {
        if (!quadrants.contains(item.getQuadrant())) {
            return false;
        }
        if (!rings.contains(item.getFinalRing())) {
            return false;
        }

        return true;
    }
}
