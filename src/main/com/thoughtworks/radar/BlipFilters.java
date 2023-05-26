package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.Blip;
import com.thoughtworks.radar.domain.BlipLifetime;
import com.thoughtworks.radar.domain.Quadrant;
import com.thoughtworks.radar.domain.Ring;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BlipFilters implements BlipLifeTimeFilter, BlipFilter {
    private final List<Ring> rings;
    private final List<Quadrant> quadrants;
    private final boolean firstRing;

    public BlipFilters(boolean firstRing) {
        this.firstRing = firstRing;
        rings = new LinkedList<>();
        quadrants = new LinkedList<>();
    }

    public static BlipFilters All() {
        return new BlipFilters(true).allow(Quadrant.values()).allow(Ring.values());
    }

    public BlipFilters allow(Quadrant...toAdd) {
        quadrants.addAll(Arrays.asList(toAdd));
        return this;
    }

    public BlipFilters allow(Ring...toAdd) {
        rings.addAll(Arrays.asList(toAdd));
        return this;
    }

    @Override
    public boolean filter(BlipLifetime item) {
        if (!quadrants.contains(item.getQuadrant())) {
            return false;
        }
        if (!rings.contains(item.getFinalRing())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean filter(Blip item) {
        if (!quadrants.contains(item.getFirstQuadrant())) {
            return false;
        }
        return ringFilter(item);
    }

    private boolean ringFilter(Blip item) {
        Ring ring = firstRing ? item.firstRing() : item.lastRing();
        if (!rings.contains(ring)) {
            return false;
        }
        return true;
    }
}
