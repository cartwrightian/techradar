package com.thoughtworks.radar;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class BlipTest {

    @Test
    public void shouldCreateHistory() {
        Blip blip = new Blip(42, "LifeTheUniverseEtc", Quadrant.tools);
        BlipHistory historyA = new BlipHistory(LocalDate.of(2010,12,24), Ring.Assess, "descA");
        BlipHistory historyB = new BlipHistory(LocalDate.of(2002,11,23), Ring.Trial, "descB");
        BlipHistory historyC = new BlipHistory(LocalDate.of(2018,11,23), Ring.Adopt, "descC");

        blip.addHistory(historyA);
        blip.addHistory(historyB);
        blip.addHistory(historyC);

        assertEquals(LocalDate.of(2002,11,23), blip.appearedDate());
        assertEquals(LocalDate.of(2018,11,23), blip.lastDate());
        assertEquals(Ring.Adopt, blip.lastRing());
        assertEquals(Ring.Trial, blip.firstRing());
        assertEquals("descB", blip.getDescription());
    }
}
