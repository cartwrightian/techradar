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

    @Before
    public void beforeEachTestRuns() {
        radar = new Radar();
        Parser.RawBlip rawA = new Parser.RawBlip(42, "blipA", thirdDate, Ring.Hold, Quadrant.tools);
        Parser.RawBlip rawB = new Parser.RawBlip(42, "blipA", secondDate, Ring.Assess, Quadrant.tools);
        Parser.RawBlip rawC = new Parser.RawBlip(42, "blipA", fourthDate, Ring.Adopt, Quadrant.tools);

        Parser.RawBlip rawD = new Parser.RawBlip(52, "blipB", fifthDate, Ring.Adopt, Quadrant.techniques);
        Parser.RawBlip rawE = new Parser.RawBlip(52, "blipB", firstDate, Ring.Adopt, Quadrant.techniques);

        Parser.RawBlip rawF = new Parser.RawBlip(53, "blipC", secondDate, Ring.Adopt, Quadrant.LanguagesAndFrameworks);

        radar.add(rawA);
        radar.add(rawB);
        radar.add(rawC);
        radar.add(rawD);
        radar.add(rawE);
        radar.add(rawF);

        analyser = new Analyser(radar);
    }

    @Test
    public void shouldSummariseBlipDecay() {
        Map<Integer,List<Integer>> decay = analyser.summaryOfDecay(Quadrant.tools,Quadrant.LanguagesAndFrameworks,
                Quadrant.LanguagesAndFrameworks,Quadrant.techniques);

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
    public void shouldSummariseBlipDecayFilter() {
        Map<Integer, List<Integer>> decay = analyser.summaryOfDecay(Quadrant.tools);

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
        Map<LocalDate, Integer> result = analyser.countRadars();

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
        assertEquals(2, blipLifetimeA.getFirst());
        assertEquals(4, blipLifetimeA.getLast());
        assertEquals("blipA", blipLifetimeA.getName());
        assertEquals(42, blipLifetimeA.getId());
        assertEquals(Quadrant.tools, blipLifetimeA.getQuadrant());

        BlipLifetime blipLifetimeB = result.get(2);
        assertEquals(secondDate, blipLifetimeB.getAppearedDate());
        assertEquals(secondDate, blipLifetimeB.getLastSeen());
        assertEquals(2, blipLifetimeB.getFirst());
        assertEquals(2, blipLifetimeB.getLast());
        assertEquals("blipC", blipLifetimeB.getName());
        assertEquals(53, blipLifetimeB.getId());
        assertEquals(Quadrant.LanguagesAndFrameworks, blipLifetimeB.getQuadrant());

    }

}