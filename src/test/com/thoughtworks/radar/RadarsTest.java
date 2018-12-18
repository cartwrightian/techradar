package com.thoughtworks.radar;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class RadarsTest {

    private Radars radar;
    private Parser.RawBlip rawA;
    private Parser.RawBlip rawB;
    private Parser.RawBlip rawC;
    private Parser.RawBlip rawD;
    private LocalDate secondDate;
    private LocalDate firstDate;
    private LocalDate thirdDate;

    @Before
    public void beforeEachTestRuns() {
        firstDate = LocalDate.of(2017, 6, 23);
        secondDate = firstDate.plusDays(20);
        thirdDate = firstDate.plusDays(100);

        radar = new Radars();
        rawA = new Parser.RawBlip(BlipId.from(1), "blipA", secondDate, Ring.Hold, Quadrant.tools, "desc", 11);
        rawB = new Parser.RawBlip(BlipId.from(1), "blipA", firstDate, Ring.Assess, Quadrant.tools, "desc", 22);
        rawC = new Parser.RawBlip(BlipId.from(1), "blipA", thirdDate, Ring.Adopt, Quadrant.tools, "desc", 33);
        rawD = new Parser.RawBlip(BlipId.from(4), "blipA", thirdDate, Ring.Adopt, Quadrant.tools, "desc", 44);

        radar.add(rawA);
        radar.add(rawB);
        radar.add(rawC);
        radar.add(rawD);

        radar.updateBlipHistories();
    }

    @Test
    public void shouldGetLongestOnRadar() {
        List<Blip> result = radar.longestOnRadar(BlipFilter.All(), 2);
        assertEquals(2, result.size());
        assertEquals(22, result.get(0).idOnRadar(1));
        assertEquals(11, result.get(0).idOnRadar(2));
    }

    @Test
    public void shouldAddBlipsAndIndexRadars() {
        assertEquals(3, radar.numberOfRadars());

        assertEquals(1, radar.getEditionFrom(firstDate));
        assertEquals(2, radar.getEditionFrom(secondDate));
        assertEquals(3, radar.getEditionFrom(thirdDate));

        assertEquals(firstDate, radar.dateOfEdition(1));
        assertEquals(secondDate, radar.dateOfEdition(2));
        assertEquals(thirdDate, radar.dateOfEdition(3));

        List<Blip> blips = radar.getBlips();
        assertEquals(3, blips.get(0).getHistory().size());
        assertEquals(1, blips.get(1).getHistory().size());
    }

    @Test
    public void shouldGetByDate() {
        List<Blip> blips = radar.blipsVisibleOn(1);
        assertEquals(1, blips.size());
        assertEquals(Quadrant.tools, blips.get(0).getQuadrant());
        assertEquals(Ring.Assess, blips.get(0).firstRing());

        blips = radar.blipsVisibleOn(3);
        assertEquals(2, blips.size());
        assertEquals(BlipId.from(1), blips.get(0).getId());
        assertEquals(BlipId.from(4), blips.get(1).getId());
    }

}