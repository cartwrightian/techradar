package com.thoughtworks.radar;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AnalyserTest {

    private Radar radar;
    private Analyser analyser;
    private LocalDate firstDate = LocalDate.of(2000, 7, 17);
    private LocalDate secondDate = LocalDate.of(2017, 6, 23);
    private LocalDate thirdDate = LocalDate.of(2017, 11, 24);
    private LocalDate fourthDate = LocalDate.of(2018, 10, 22);
    private LocalDate fifthDate = LocalDate.of(2019, 8, 19);
    private BlipFilter blipFilter;

    @Before
    public void beforeEachTestRuns() {
        radar = new Radar();
        Parser.RawBlip rawA = new Parser.RawBlip(42, "blipA", thirdDate, Ring.Hold, Quadrant.tools);
        Parser.RawBlip rawB = new Parser.RawBlip(42, "blipA", secondDate, Ring.Assess, Quadrant.tools);
        Parser.RawBlip rawC = new Parser.RawBlip(42, "blipA", fourthDate, Ring.Adopt, Quadrant.tools);

        Parser.RawBlip rawD = new Parser.RawBlip(52, "blipB", fifthDate, Ring.Adopt, Quadrant.techniques);
        Parser.RawBlip rawE = new Parser.RawBlip(52, "blipB", firstDate, Ring.Assess, Quadrant.techniques);

        Parser.RawBlip rawF = new Parser.RawBlip(53, "blipC", secondDate, Ring.Hold, Quadrant.LanguagesAndFrameworks);

        radar.add(rawA);
        radar.add(rawB);
        radar.add(rawC);
        radar.add(rawD);
        radar.add(rawE);
        radar.add(rawF);

        analyser = new Analyser(radar);
        blipFilter = new BlipFilter();
    }

    @Test
    public void shouldSummariseBlipDecayFilterByFinalRing() {
        Map<Integer,List<Integer>> decay = analyser.summaryOfDecay(blipFilter.allow(Quadrant.values()).allow(Ring.Adopt));

        assertEquals(5,decay.size());
        assertEquals(5, decay.get(1).size());

        List<Integer> first = decay.get(2);
        assertEquals(new Integer(0), first.get(0));
        assertEquals(new Integer(1), first.get(1));
        assertEquals(new Integer(1), first.get(2));
        assertEquals(new Integer(1), first.get(3));
        assertEquals(new Integer(0), first.get(4));

    }

    @Test
    public void shouldSummariseBlipDecayAllQuadrants() {
        Map<Integer,List<Integer>> decay = analyser.summaryOfDecay(BlipFilter.All());

        assertEquals(5,decay.size());
        assertEquals(5, decay.get(1).size());
        List<Integer> secondRadar = decay.get(2);
        assertEquals(5, secondRadar.size());
        assertEquals(5, decay.get(3).size());
        assertEquals(5, decay.get(4).size());
        assertEquals(5, decay.get(5).size());

        assertEquals(new Integer(0), secondRadar.get(0));
        assertEquals(new Integer(2), secondRadar.get(1));
        assertEquals(new Integer(1), secondRadar.get(2));
        assertEquals(new Integer(1), secondRadar.get(3));
        assertEquals(new Integer(0), secondRadar.get(4));

        List<Integer> first = decay.get(1);
        assertEquals(new Integer(1), first.get(0));
        assertEquals(new Integer(1), first.get(1));
        assertEquals(new Integer(1), first.get(2));
        assertEquals(new Integer(1), first.get(3));
        assertEquals(new Integer(1), first.get(4));
    }

    @Test
    public void shouldSummariseBlipDecayFilterByQuad() {
        Map<Integer, List<Integer>> decay = analyser.summaryOfDecay(
                blipFilter.allow(Quadrant.tools).allow(Ring.values()));

        assertEquals(5, decay.get(1).size());
        assertEquals(5, decay.size());
        List<Integer> first = decay.get(1);
        assertEquals(new Integer(0), first.get(0));
        assertEquals(new Integer(0), first.get(1));
        assertEquals(new Integer(0), first.get(2));
        assertEquals(new Integer(0), first.get(3));
        assertEquals(new Integer(0), first.get(4));
    }

    @Test
    public void shouldIndexRadars() {
        Map<LocalDate, Integer> result = analyser.getDateToNumberIndex();

        assertEquals(5, result.size());
        assertEquals(result.get(firstDate),new Integer(1));
        assertEquals(result.get(secondDate),new Integer(2));
        assertEquals(result.get(thirdDate),new Integer(3));
        assertEquals(result.get(fourthDate),new Integer(4));
        assertEquals(result.get(fifthDate),new Integer(5));
    }

    @Test
    public void shouldFindLifeTimesAndRadarNumbers() {
        List<BlipLifetime> result = analyser.lifeTimes();

        assertEquals(3, result.size());

        BlipLifetime blipLifetimeA = result.get(0);
        assertEquals(LocalDate.of(2017,6,23), blipLifetimeA.getAppearedDate());
        assertEquals(LocalDate.of(2018,10,22), blipLifetimeA.getLastSeen());
        assertEquals(2, blipLifetimeA.getFirstRadarNum());
        assertEquals(4, blipLifetimeA.getLastRadarNum());
        assertEquals("blipA", blipLifetimeA.getName());
        assertEquals(42, blipLifetimeA.getId());
        assertEquals(Quadrant.tools, blipLifetimeA.getQuadrant());

        BlipLifetime blipLifetimeB = result.get(2);
        assertEquals(secondDate, blipLifetimeB.getAppearedDate());
        assertEquals(secondDate, blipLifetimeB.getLastSeen());
        assertEquals(2, blipLifetimeB.getFirstRadarNum());
        assertEquals(2, blipLifetimeB.getLastRadarNum());
        assertEquals("blipC", blipLifetimeB.getName());
        assertEquals(53, blipLifetimeB.getId());
        assertEquals(Quadrant.LanguagesAndFrameworks, blipLifetimeB.getQuadrant());
    }

    @Test
    public void shouldCalcHalfLifeForBlips() {
        Radar radar = new Radar();
        for (int i = 0; i < 100; i++) {
            Parser.RawBlip rawA = new Parser.RawBlip(i, "blip"+i, firstDate, Ring.Assess, Quadrant.tools);
            radar.add(rawA);
            if (i<40) {
                Parser.RawBlip rawB = new Parser.RawBlip(i, "blip"+i, firstDate.plusDays(100), Ring.Adopt, Quadrant.tools);
                radar.add(rawB);
            }
        }
        analyser = new Analyser(radar);
        Map<Integer,Integer> halfLife = analyser.findHalfLife(BlipFilter.All());

        // half gone by 100 days
        int key = Math.toIntExact(firstDate.toEpochDay());
        assertEquals(new Integer(100),halfLife.get(key));

    }

}