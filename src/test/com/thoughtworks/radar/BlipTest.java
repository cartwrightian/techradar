package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BlipTest {

    @Test
    public void shouldCreateHistory() {
        LocalDate firstDate = LocalDate.of(2010, 12, 24);
        LocalDate secondDate = firstDate.plusYears(1);
        LocalDate thirdDate = secondDate.plusYears(2);

        Volume volume1 = new Volume(1, firstDate);
        Volume volume2 = new Volume(2, secondDate);
        Volume volume3 = new Volume(3, thirdDate);
        Volume volume5 = new Volume(5, firstDate.plusYears(3));
        Volume volume6 = new Volume(6, firstDate.plusYears(4));

        UniqueBlipId blipId = UniqueBlipId.from(42);
        Quadrant quadrant = Quadrant.tools;
        Blip blip = new Blip(blipId, "LifeTheUniverseEtc");
        BlipEntry historyA = new BlipEntry(blipId, firstDate, quadrant, Ring.Assess, "descA", 1);
        BlipEntry historyB = new BlipEntry(blipId, secondDate, quadrant, Ring.Trial, "descB", 33);
        BlipEntry historyC = new BlipEntry(blipId, thirdDate, quadrant, Ring.Adopt, "descC", 12);

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

        assertFalse(blip.visibleOn(volume5));

        assertEquals(33, blip.idOnRadar(volume2));
        assertEquals(1, blip.idOnRadar(volume1));
        long firstUntilThird = thirdDate.toEpochDay() - firstDate.toEpochDay();
        assertEquals(firstUntilThird, blip.getDuration().toDays(), blip.getDuration().toString());

        assertEquals(Ring.Adopt,blip.fadedRing());
        assertEquals(thirdDate, blip.firstFadedDate());

        assertEquals(blip.fadedRing(),Ring.Adopt);
        assertEquals(thirdDate, blip.firstFadedDate());

        blip.addHistory(volume5, new BlipEntry(blipId, volume5.getPublicationDate(), quadrant, Ring.Adopt, "descC", 12));
        blip.addHistory(volume6, new BlipEntry(blipId, volume6.getPublicationDate(), quadrant, Ring.Adopt, "descC", 12));

        // still in same ring
        assertEquals(2, blip.getNumberBlipMoves());
        assertTrue(blip.visibleOn(volume5));
        assertTrue(blip.visibleOn(volume6));

        // first fade still the same
        assertEquals(blip.fadedRing(),Ring.Adopt);
        assertEquals(thirdDate, blip.firstFadedDate());

        // duration only when on radar
        long fiveUntilSix = volume6.getPublicationDate().toEpochDay()-volume5.getPublicationDate().toEpochDay();
        assertEquals(firstUntilThird + fiveUntilSix, blip.getDuration().toDays());
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

        BlipEntry historyA = new BlipEntry(blipId, firstDate, quadrant, Ring.Assess, "textPresent", 1);
        BlipEntry historyB = new BlipEntry(blipId, secondDate, quadrant, Ring.Trial, "", 33);

        blip.addHistory(volume1, historyA);
        blip.addHistory(volume2, historyB);

        assertEquals("textPresent", blip.getDescription());
    }
}
