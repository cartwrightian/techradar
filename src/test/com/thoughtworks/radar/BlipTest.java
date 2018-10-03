package com.thoughtworks.radar;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlipTest {

    @Test
    public void shouldCreateHistory() {
        LocalDate firstDate = LocalDate.of(2010, 12, 24);
        LocalDate secondDate = LocalDate.of(2011, 12, 24);
        LocalDate thirdDate = LocalDate.of(2018, 11, 23);

        Blip blip = new Blip(BlipId.from(42), "LifeTheUniverseEtc", Quadrant.tools);
        BlipHistory historyA = new BlipHistory(firstDate, Ring.Assess, "descA", 1);
        BlipHistory historyB = new BlipHistory(secondDate, Ring.Trial, "descB", 33);
        BlipHistory historyC = new BlipHistory(thirdDate, Ring.Adopt, "descC", 12);

        blip.addHistory(historyA);
        blip.addHistory(historyB);
        blip.addHistory(historyC);

        assertEquals(firstDate, blip.appearedDate());
        assertEquals(thirdDate, blip.lastDate());
        assertEquals(Ring.Adopt, blip.lastRing());
        assertEquals(Ring.Assess, blip.firstRing());
        assertEquals("descC", blip.getDescription());

        assertTrue(blip.visibleOn(firstDate));
        assertTrue(blip.visibleOn(thirdDate));

        assertFalse(blip.visibleOn(firstDate.minusDays(1)));
        assertFalse(blip.visibleOn(thirdDate.plusDays(1)));

        assertEquals(33, blip.idOnRadar(secondDate));
        assertEquals(1, blip.idOnRadar(firstDate));
    }

    @Test
    public void shouldKeepPreviousDescriptionIfNewOneIsEmpty() {
        Blip blip = new Blip(BlipId.from(42), "LifeTheUniverseEtc", Quadrant.tools);
        LocalDate firstDate = LocalDate.of(2010, 12, 24);
        BlipHistory historyA = new BlipHistory(firstDate, Ring.Assess, "textPresent", 1);
        BlipHistory historyB = new BlipHistory(firstDate.plusDays(42), Ring.Trial, "", 33);

        blip.addHistory(historyA);
        blip.addHistory(historyB);

        assertEquals("textPresent", blip.getDescription());
    }
}
