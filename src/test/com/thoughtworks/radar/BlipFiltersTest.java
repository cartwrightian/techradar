package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.UniqueBlipId;
import com.thoughtworks.radar.domain.Quadrant;
import com.thoughtworks.radar.domain.Ring;
import com.thoughtworks.radar.domain.Volume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class BlipFiltersTest {

    private LocalDate date;
    private BlipFilters filter;
    private BlipLifetime blipLifetime;

    @BeforeEach
    public void beforeEachTestRuns() {
        date = LocalDate.now();
        filter = new BlipFilters(true);

        Volume volume1 = new Volume(1, date);
        Volume volume18 = new Volume(18, date.plusYears(10));
        blipLifetime = new BlipLifetime("name", UniqueBlipId.from(42), Quadrant.LanguagesAndFrameworks, date,
                date, volume1,volume18, Ring.Adopt);
    }

    @Test
    public void shouldFilterInByQuadrant() {
        filter.allow(Quadrant.LanguagesAndFrameworks).allow(Ring.values());
        assertTrue(filter.filter(blipLifetime));
    }

    @Test
    public void shouldFilterInByQuadrants() {
        filter.allow(Quadrant.values()).allow(Ring.values());
        assertTrue(filter.filter(blipLifetime));
    }

    @Test
    public void shouldFilterOutByQuadrant() {
        filter.allow(Quadrant.techniques).allow(Ring.values());
        assertFalse(filter.filter(blipLifetime));
    }

    @Test
    public void shouldFilterInByRing() {
        filter.allow(Ring.Adopt).allow(Quadrant.values());
        assertTrue(filter.filter(blipLifetime));
    }

    @Test
    public void shouldFilterOutByRing() {
        filter.allow(Ring.Hold).allow(Quadrant.values());
        assertFalse(filter.filter(blipLifetime));
    }

}
