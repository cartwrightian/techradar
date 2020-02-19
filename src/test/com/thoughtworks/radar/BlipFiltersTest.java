package com.thoughtworks.radar;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class BlipFiltersTest {

    private LocalDate date;
    private BlipFilters filter;
    BlipLifetime item = new BlipLifetime("name",BlipId.from(42), Quadrant.LanguagesAndFrameworks, date, date, 1,18, Ring.Adopt);

    @Before
    public void beforeEachTestRuns() {
        date = LocalDate.now();
        filter = new BlipFilters(true);
    }

    @Test
    public void shouldFilterInByQuadrant() {
        filter.allow(Quadrant.LanguagesAndFrameworks).allow(Ring.values());
        assertTrue(filter.filter(item));
    }

    @Test
    public void shouldFilterInByQuadrants() {
        filter.allow(Quadrant.values()).allow(Ring.values());
        assertTrue(filter.filter(item));
    }

    @Test
    public void shouldFilterOutByQuadrant() {
        filter.allow(Quadrant.techniques).allow(Ring.values());
        assertFalse(filter.filter(item));
    }

    @Test
    public void shouldFilterInByRing() {
        filter.allow(Ring.Adopt).allow(Quadrant.values());
        assertTrue(filter.filter(item));
    }

    @Test
    public void shouldFilterOutByRing() {
        filter.allow(Ring.Hold).allow(Quadrant.values());
        assertFalse(filter.filter(item));
    }

}
