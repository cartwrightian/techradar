package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BlipTest {

    @Test
    public void shouldCreateHistory() {
        LocalDate firstDate = LocalDate.of(2010, 12, 24);
        LocalDate secondDate = firstDate.plusDays(365); // LocalDate.of(2011, 12, 24);
        LocalDate thirdDate = secondDate.plusDays(1000);

        Volume volume1 = new Volume(1, firstDate);
        Volume volume2 = new Volume(2, secondDate);
        Volume volume3 = new Volume(3, thirdDate);

        UniqueBlipId blipId = UniqueBlipId.from(42);
        Quadrant quadrant = Quadrant.tools;
        Blip blip = new Blip(blipId, "LifeTheUniverseEtc");
        BlipHistory historyA = new BlipHistory(blipId, firstDate, quadrant, Ring.Assess, "descA", 1);
        BlipHistory historyB = new BlipHistory(blipId, secondDate, quadrant, Ring.Trial, "descB", 33);
        BlipHistory historyC = new BlipHistory(blipId, thirdDate, quadrant, Ring.Adopt, "descC", 12);

        blip.addHistory(volume1, historyA);
        blip.addHistory(volume2, historyB);
        blip.addHistory(volume3, historyC);

        assertEquals(firstDate, blip.appearedDate());
        assertEquals(thirdDate, blip.lastDate());
        assertEquals(2, blip.getNumberBlipMoves());
        assertEquals(Ring.Adopt, blip.lastRing());
        assertEquals(Ring.Assess, blip.firstRing());
        assertEquals("descC", blip.getDescription());

        assertTrue(blip.visibleOn(volume1));
        assertTrue(blip.visibleOn(volume2));
        assertTrue(blip.visibleOn(volume3));

        Volume volume5 = new Volume(5, thirdDate.plusDays(1000));
        assertFalse(blip.visibleOn(volume5));

        assertEquals(33, blip.idOnRadar(volume2));
        assertEquals(1, blip.idOnRadar(volume1));
        assertEquals(1365, blip.getDuration().toDays());

        assertEquals(Ring.Adopt,blip.fadedRing());
        assertEquals(thirdDate, blip.firstFadedDate());

        Volume volume6 = new Volume(6, thirdDate.plusDays(2000));

        blip.addHistory(volume5, new BlipHistory(blipId, thirdDate.plusDays(100), quadrant, Ring.Adopt, "descC", 12));
        blip.addHistory(volume6, new BlipHistory(blipId, thirdDate.plusDays(200), quadrant, Ring.Adopt, "descC", 12));

        assertEquals(2, blip.getNumberBlipMoves());

        assertEquals(blip.fadedRing(),Ring.Adopt);
        assertEquals(thirdDate, blip.firstFadedDate());

        // duration only when on radar
        assertEquals(1365+100, blip.getDuration().toDays());
    }

    @Test
    public void shouldKeepPreviousDescriptionIfNewOneIsEmpty() {
        UniqueBlipId blipId = UniqueBlipId.from(42);
        Quadrant quadrant = Quadrant.tools;
        Blip blip = new Blip(blipId, "LifeTheUniverseEtc");

        LocalDate firstDate = LocalDate.of(2010, 12, 24);
        LocalDate secondDate = firstDate.plusDays(42);

        Volume volume1 = new Volume(1, firstDate);
        Volume volume2 = new Volume(2, secondDate);

        BlipHistory historyA = new BlipHistory(blipId, firstDate, quadrant, Ring.Assess, "textPresent", 1);
        BlipHistory historyB = new BlipHistory(blipId, secondDate, quadrant, Ring.Trial, "", 33);

        blip.addHistory(volume1, historyA);
        blip.addHistory(volume2, historyB);

        assertEquals("textPresent", blip.getDescription());
    }
}
