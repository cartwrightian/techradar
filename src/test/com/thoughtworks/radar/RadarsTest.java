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
        secondDate = LocalDate.of(2017, 11, 24);
        thirdDate = LocalDate.of(2018, 10, 22);

        radar = new Radars();
        rawA = new Parser.RawBlip(1, "blipA", secondDate, Ring.Hold, Quadrant.tools, "desc");
        rawB = new Parser.RawBlip(1, "blipA", firstDate, Ring.Assess, Quadrant.tools, "desc");
        rawC = new Parser.RawBlip(1, "blipA", thirdDate, Ring.Adopt, Quadrant.tools, "desc");
        rawD = new Parser.RawBlip(4, "blipA", thirdDate, Ring.Adopt, Quadrant.tools, "desc");

        radar.add(rawA);
        radar.add(rawB);
        radar.add(rawC);
        radar.add(rawD);
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
        List<Blip> blips = radar.getBlipForRadarOn(firstDate);
        assertEquals(1, blips.size());
        assertEquals(Quadrant.tools, blips.get(0).getQuadrant());
        assertEquals(Ring.Assess, blips.get(0).firstRing());

        blips = radar.getBlipForRadarOn(thirdDate);
        assertEquals(1, blips.size());
        assertEquals(4, blips.get(0).getId());
    }

}