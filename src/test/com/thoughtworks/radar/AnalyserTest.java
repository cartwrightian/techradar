package com.thoughtworks.radar;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class AnalyserTest {

    private Radars radar;
    private Analyser analyser;
    private LocalDate firstDate = LocalDate.of(2000, 7, 17);
    private LocalDate secondDate = LocalDate.of(2017, 6, 23);
    private LocalDate thirdDate = LocalDate.of(2017, 11, 24);
    private LocalDate fourthDate = LocalDate.of(2018, 10, 22);
    private LocalDate fifthDate = LocalDate.of(2019, 8, 19);
    private BlipFilter blipFilter;

    @Before
    public void beforeEachTestRuns() {
        radar = new Radars();
        // when blips fades all previous marked as faded
        Parser.RawBlip rawA = new Parser.RawBlip(BlipId.from(42), "blipA", thirdDate, Ring.Hold, Quadrant.tools, "descA", 30, true);
        Parser.RawBlip rawB = new Parser.RawBlip(BlipId.from(42), "blipA", secondDate, Ring.Assess, Quadrant.tools, "descA", 11, true);
        Parser.RawBlip rawC = new Parser.RawBlip(BlipId.from(42), "blipA", fourthDate, Ring.Adopt, Quadrant.tools, "descA", 40, false);

        Parser.RawBlip rawD = new Parser.RawBlip(BlipId.from(52), "blipB", fifthDate, Ring.Adopt, Quadrant.techniques, "later text", 50, false);
        Parser.RawBlip rawE = new Parser.RawBlip(BlipId.from(52), "blipB", firstDate, Ring.Assess, Quadrant.techniques, "init text", 1, false);

        Parser.RawBlip rawF = new Parser.RawBlip(BlipId.from(53), "blipC", secondDate, Ring.Hold, Quadrant.LanguagesAndFrameworks, "descC", 10, false);

        radar.add(rawA);
        radar.add(rawB);
        radar.add(rawC);
        radar.add(rawD);
        radar.add(rawE);
        radar.add(rawF);

        radar.updateBlipHistories();

        analyser = new Analyser(radar);
        blipFilter = new BlipFilter();
    }

    @Test
    public void shouldSpotFadeAndThenReappear() {
        // TODO can this be done from the JSON
        fail("TODO");
    }

    @Test
    public void shouldCreateSummaryOfText() {
        List <SummaryText> summaryTexts = analyser.createSummaryText();

        assertEquals(6,summaryTexts.size());

        // only one thing on first radar, so one summary line
        SummaryText summaryText = summaryTexts.get(0);
        assertEquals("Jul 2000", summaryText.getDate());
        assertEquals("Assess", summaryText.getRing());
        assertEquals("techniques", summaryText.getQuadrant());
        assertEquals("\"later text\"", summaryText.getDescription());

        // should be 3 lines for second radar
        // summary text ordered by radar date then, if present, radarID
        assertEquals("10", summaryTexts.get(1).getRadarId());
        assertEquals("11", summaryTexts.get(2).getRadarId());

    }

    @Test
    public void shouldSummariseAmountNewPerRadar() {
        Map<Integer, Double> amounts = analyser.summaryOfNew(BlipFilter.All());

        assertEquals((Double) 1.0, amounts.get(1));
        assertEquals((Double) (2.0/3.0), amounts.get(2));
        assertEquals((Double) 0.0, amounts.get(3));
        assertEquals((Double) 0.0, amounts.get(4));
        assertEquals((Double) 0.0, amounts.get(5));
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
    public void shouldFindLifeTimesAndRadarNumbers() {
        List<BlipLifetime> result = analyser.lifeTimes();

        assertEquals(3, result.size());

        BlipLifetime blipLifetimeA = result.get(0);
        assertEquals(LocalDate.of(2017,6,23), blipLifetimeA.getAppearedDate());
        assertEquals(LocalDate.of(2018,10,22), blipLifetimeA.getLastSeen());
        assertEquals(2, blipLifetimeA.getFirstRadarNum());
        assertEquals(4, blipLifetimeA.getLastRadarNum());
        assertEquals("blipA", blipLifetimeA.getName());
        assertEquals(BlipId.from(42), blipLifetimeA.getId());
        assertEquals(Quadrant.tools, blipLifetimeA.getQuadrant());

        BlipLifetime blipLifetimeB = result.get(2);
        assertEquals(secondDate, blipLifetimeB.getAppearedDate());
        assertEquals(secondDate, blipLifetimeB.getLastSeen());
        assertEquals(2, blipLifetimeB.getFirstRadarNum());
        assertEquals(2, blipLifetimeB.getLastRadarNum());
        assertEquals("blipC", blipLifetimeB.getName());
        assertEquals(BlipId.from(53), blipLifetimeB.getId());
        assertEquals(Quadrant.LanguagesAndFrameworks, blipLifetimeB.getQuadrant());
    }

    @Test
    public void shouldCalcHalfLifeForBlips() {
        Radars radar = new Radars();
        for (int blipNumber = 0; blipNumber < 100; blipNumber++) {
            Parser.RawBlip rawA = new Parser.RawBlip(BlipId.from(blipNumber), "blip"+blipNumber, firstDate,
                    Ring.Assess, Quadrant.tools, "desc", blipNumber, false);
            radar.add(rawA);
            if (blipNumber<49) {
                Parser.RawBlip later = new Parser.RawBlip(BlipId.from(blipNumber), "blip"+blipNumber,
                        firstDate.plusDays(100), Ring.Adopt,
                        Quadrant.tools, "desc", 41-blipNumber, false);
                radar.add(later);
            }
            if (blipNumber<24) {
                Parser.RawBlip later = new Parser.RawBlip(BlipId.from(blipNumber), "blip"+blipNumber,
                        firstDate.plusDays(200), Ring.Adopt,
                        Quadrant.tools, "desc", 41-blipNumber, false);
                radar.add(later);
            }
        }
        radar.updateBlipHistories();
        analyser = new Analyser(radar);
        Map<Integer,Quartiles> halfLife = analyser.findHalfLife(BlipFilter.All());

        // half gone by 100 days
        int firstRadarKey = Math.toIntExact(firstDate.toEpochDay());
        Quartiles quartiles = halfLife.get(firstRadarKey);
        assertEquals(200L, quartiles.get(1));
        assertEquals(100L, quartiles.get(2));

    }

}