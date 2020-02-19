package com.thoughtworks.radar;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class RadarsTest {
    private LocalDate secondDate;
    private LocalDate firstDate;
    private LocalDate thirdDate;
    private LocalDate fourthDate;

    private Radars radar;
    private Parser.RawBlip rawA;
    private Parser.RawBlip rawB;
    private Parser.RawBlip rawC;
    private Parser.RawBlip rawD;

    private Parser.RawBlip rawE;
    private Parser.RawBlip rawF;
    private Parser.RawBlip rawG;

    @Before
    public void beforeEachTestRuns() {
        firstDate = LocalDate.of(2017, 6, 23);
        secondDate = firstDate.plusDays(20);
        thirdDate = firstDate.plusDays(100);
        fourthDate = firstDate.plusDays(110);

        radar = new Radars();
        rawA = new Parser.RawBlip(BlipId.from(1), "blipA", secondDate, Ring.Hold, Quadrant.tools, "desc", 11);
        rawB = new Parser.RawBlip(BlipId.from(1), "blipA", firstDate, Ring.Assess, Quadrant.tools, "desc", 22);
        rawC = new Parser.RawBlip(BlipId.from(1), "blipA", thirdDate, Ring.Adopt, Quadrant.tools, "desc", 33);

        rawD = new Parser.RawBlip(BlipId.from(4), "blipA", thirdDate, Ring.Adopt, Quadrant.tools, "desc", 44);

        rawE = new Parser.RawBlip(BlipId.from(5), "blipC", secondDate, Ring.Trial, Quadrant.techniques, "desc", 51);
        rawF = new Parser.RawBlip(BlipId.from(5), "blipC", thirdDate, Ring.Adopt, Quadrant.techniques, "desc", 52);
        rawG = new Parser.RawBlip(BlipId.from(5), "blipC", fourthDate, Ring.Hold, Quadrant.techniques, "desc", 53);

        radar.add(rawA);
        radar.add(rawB);
        radar.add(rawC);
        radar.add(rawD);
        radar.add(rawE);
        radar.add(rawF);
        radar.add(rawG);

        radar.updateBlipHistories();
    }

    @Test
    public void shouldFindMostMovedAndNonMovers() {
        // not a move, same ring
        Parser.RawBlip rawE = new Parser.RawBlip(BlipId.from(1), "blipA", thirdDate.plusDays(10), Ring.Adopt, Quadrant.tools, "desc", 88);
        radar.add(rawE);
        // lots of none moving history
        for (int i = 20; i <200; i=i+20) {
            radar.add(new Parser.RawBlip(BlipId.from(4), "blipA", thirdDate.plusDays(i), Ring.Adopt, Quadrant.tools, "desc", i));
        }
        radar.updateBlipHistories();

        List<Blip> mostMoves = radar.mostMoves(BlipFilters.All(), 2);

        assertEquals(2, mostMoves.size());
        Blip firstResult = mostMoves.get(0);
        Blip secondResult = mostMoves.get(1);

        assertEquals(BlipId.from(1), firstResult.getId());
        assertEquals(2, firstResult.getNumberBlipMoves().intValue());
        assertEquals(BlipId.from(5), secondResult.getId());
        assertEquals(2, secondResult.getNumberBlipMoves().intValue()); // never moved from adopt

        List<Blip> nonMovers = radar.nonMovers(BlipFilters.All());
        assertEquals(1, nonMovers.size());
        assertEquals(BlipId.from(4), nonMovers.get(0).getId());
    }

    @Test
    public void shouldGetLongestOnRadar() {
        List<Blip> result = radar.longestOnRadar(BlipFilters.All(), 2);
        assertEquals(2, result.size());
        assertEquals(22, result.get(0).idOnRadar(1));
        assertEquals(11, result.get(0).idOnRadar(2));
    }

    @Test
    public void shouldAddBlipsAndIndexRadars() {
        assertEquals(4, radar.numberOfRadars());

        assertEquals(1, radar.getEditionFrom(firstDate));
        assertEquals(2, radar.getEditionFrom(secondDate));
        assertEquals(3, radar.getEditionFrom(thirdDate));
        assertEquals(4, radar.getEditionFrom(fourthDate));

        assertEquals(firstDate, radar.dateOfEdition(1));
        assertEquals(secondDate, radar.dateOfEdition(2));
        assertEquals(thirdDate, radar.dateOfEdition(3));
        assertEquals(fourthDate, radar.dateOfEdition(4));

        List<Blip> blips = radar.getBlips();
        assertEquals(3, blips.get(0).getHistory().size());
        assertEquals(1, blips.get(1).getHistory().size());
        assertEquals(3, blips.get(2).getHistory().size());

    }

    @Test
    public void shouldGetByDate() {
        List<Blip> blips = radar.blipsVisibleOn(1);
        assertEquals(1, blips.size());
        assertEquals(Quadrant.tools, blips.get(0).getQuadrant());
        assertEquals(Ring.Assess, blips.get(0).firstRing());

        blips = radar.blipsVisibleOn(3);
        assertEquals(3, blips.size());
        assertEquals(BlipId.from(1), blips.get(0).getId());
        assertEquals(BlipId.from(4), blips.get(1).getId());
    }

    @Test
    public void shouldFindBlipsThatWereEverInAdoptButFadedOnHold() {
        List<Blip> blips = radar.everInAdoptToHold();

        assertFalse(blips.isEmpty());
        assertEquals(1, blips.size());
    }

}