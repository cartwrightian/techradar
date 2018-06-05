package com.thoughtworks.radar;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlipTest {

    @Test
    public void shouldCreateHistory() {
        Blip blip = new Blip(42, "LifeTheUniverseEtc", Quadrant.tools);
        BlipHistory historyA = new BlipHistory(LocalDate.of(2010,12,24), Ring.Assess, "descA", 1);
        BlipHistory historyB = new BlipHistory(LocalDate.of(2011,12,24), Ring.Trial, "descB", 33);
        BlipHistory historyC = new BlipHistory(LocalDate.of(2018,11,23), Ring.Adopt, "descC", 12);

        blip.addHistory(historyA);
        blip.addHistory(historyB);
        blip.addHistory(historyC);

        assertEquals(LocalDate.of(2010,12,24), blip.appearedDate());
        assertEquals(LocalDate.of(2018,11,23), blip.lastDate());
        assertEquals(Ring.Adopt, blip.lastRing());
        assertEquals(Ring.Assess, blip.firstRing());
        assertEquals("descA", blip.getDescription());

        assertTrue(blip.visibleOn(LocalDate.of(2010,12,24)));
        assertTrue(blip.visibleOn(LocalDate.of(2018,11,23)));

        assertFalse(blip.visibleOn(LocalDate.of(2010,12,24).minusDays(1)));
        assertFalse(blip.visibleOn(LocalDate.of(2018,11,23).plusDays(1)));

        assertEquals(33, blip.idOnRadar(LocalDate.of(2011,12,24)));
        assertEquals(1, blip.idOnRadar(LocalDate.of(2010,12,24)));

    }
}
