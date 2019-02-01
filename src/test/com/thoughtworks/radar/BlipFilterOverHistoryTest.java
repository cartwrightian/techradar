package com.thoughtworks.radar;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static junit.framework.TestCase.assertEquals;

public class BlipFilterOverHistoryTest {

    private Radars radar;

    @Before
    public void beforeEachTestRuns() {
        LocalDate firstDate = LocalDate.of(2017, 6, 23);
        LocalDate secondDate = firstDate.plusDays(20);
        LocalDate thirdDate = firstDate.plusDays(100);

        radar = new Radars();
        Parser.RawBlip rawA = new Parser.RawBlip(BlipId.from(1), "blipA", secondDate, Ring.Hold, Quadrant.tools, "desc", 11);
        Parser.RawBlip rawB = new Parser.RawBlip(BlipId.from(1), "blipA", firstDate, Ring.Assess, Quadrant.tools, "desc", 22);
        Parser.RawBlip rawC = new Parser.RawBlip(BlipId.from(1), "blipA", thirdDate, Ring.Adopt, Quadrant.tools, "desc", 33);
        Parser.RawBlip rawD = new Parser.RawBlip(BlipId.from(4), "blipA", thirdDate, Ring.Adopt, Quadrant.tools, "desc", 44);

        radar.add(rawA);
        radar.add(rawB);
        radar.add(rawC);
        radar.add(rawD);

        radar.updateBlipHistories();
    }

    @Test
    public void shouldApplyFilterToBlipHistoryMatching() {
        BlipFilterOverHistory filter = new BlipFilterOverHistory().allow(Ring.Hold);
        long count = radar.getBlips().stream().filter(filter::filter).count();
        assertEquals(1, count);

        filter.allow(Ring.Adopt);
        count = radar.getBlips().stream().filter(filter::filter).count();
        assertEquals(2, count);
    }

    @Test
    public void shouldApplyFilterToBlipHistoryNotMatching() {
        BlipFilterOverHistory inTrial = new BlipFilterOverHistory().allow(Ring.Trial);
        long everInTrial = radar.getBlips().stream().filter(inTrial::filter).count();
        assertEquals(0,everInTrial);
    }
}
