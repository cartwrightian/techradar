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
        LocalDate secondDate = firstDate.plusDays(365); // LocalDate.of(2011, 12, 24);
        LocalDate thirdDate = secondDate.plusDays(1000);

        BlipId blipId = BlipId.from(42);
        Blip blip = new Blip(blipId, "LifeTheUniverseEtc", Quadrant.tools);
        BlipHistory historyA = new BlipHistory(blipId, firstDate, Ring.Assess, "descA", 1);
        BlipHistory historyB = new BlipHistory(blipId, secondDate, Ring.Trial, "descB", 33);
        BlipHistory historyC = new BlipHistory(blipId, thirdDate, Ring.Adopt, "descC", 12);

        blip.addHistory(1,historyA);
        blip.addHistory(2,historyB);
        blip.addHistory(3,historyC);

        assertEquals(firstDate, blip.appearedDate());
        assertEquals(thirdDate, blip.lastDate());
        assertEquals(Ring.Adopt, blip.lastRing());
        assertEquals(Ring.Assess, blip.firstRing());
        assertEquals("descC", blip.getDescription());

        assertTrue(blip.visibleOn(1));
        assertTrue(blip.visibleOn(2));
        assertTrue(blip.visibleOn(3));

        assertFalse(blip.visibleOn(5));

        assertEquals(33, blip.idOnRadar(2));
        assertEquals(1, blip.idOnRadar(1));
        assertEquals(1365, blip.getDuration().toDays());

        assertEquals(Ring.Adopt,blip.fadedRing());
        assertEquals(thirdDate, blip.firstFadedDate());

        blip.addHistory(5,new BlipHistory(blipId, thirdDate.plusDays(100), Ring.Adopt, "descC", 12));
        blip.addHistory(6,new BlipHistory(blipId, thirdDate.plusDays(200), Ring.Adopt, "descC", 12));

        assertEquals(blip.fadedRing(),Ring.Adopt);
        assertEquals(thirdDate, blip.firstFadedDate());

        // duration only when on radar
        assertEquals(1365+100, blip.getDuration().toDays());
    }

    @Test
    public void shouldKeepPreviousDescriptionIfNewOneIsEmpty() {
        BlipId blipId = BlipId.from(42);
        Blip blip = new Blip(blipId, "LifeTheUniverseEtc", Quadrant.tools);
        LocalDate firstDate = LocalDate.of(2010, 12, 24);
        BlipHistory historyA = new BlipHistory(blipId, firstDate, Ring.Assess, "textPresent", 1);
        BlipHistory historyB = new BlipHistory(blipId, firstDate.plusDays(42), Ring.Trial, "", 33);

        blip.addHistory(1,historyA);
        blip.addHistory(2,historyB);

        assertEquals("textPresent", blip.getDescription());
    }
}
