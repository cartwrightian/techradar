package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.UniqueBlipId;
import com.thoughtworks.radar.domain.Quadrant;
import com.thoughtworks.radar.domain.Ring;
import com.thoughtworks.radar.domain.Volume;
import com.thoughtworks.radar.repository.BlipRepository;
import com.thoughtworks.radar.repository.VolumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AnalyserTest {

    private Analyser analyser;
    private final LocalDate firstDate = LocalDate.of(2000, 7, 17);
    private final LocalDate secondDate = LocalDate.of(2017, 6, 23);
    private final LocalDate thirdDate = LocalDate.of(2017, 11, 24);
    private final LocalDate fourthDate = LocalDate.of(2018, 10, 22);
    private final LocalDate fifthDate = LocalDate.of(2019, 8, 19);
    private BlipFilters blipFilters;
    private VolumeRepository volumeRepository;
    private Volume volume1;
    private Volume volume2;
    private Volume volume3;
    private Volume volume4;
    private Volume volume5;

    @BeforeEach
    public void beforeEachTestRuns() {
        Set<LocalDate> dates = Set.of(firstDate, secondDate, thirdDate, fourthDate, fifthDate);
        volumeRepository = new VolumeRepository(dates);
        Radars radar = new Radars(volumeRepository, new BlipRepository());

        volume1 = volumeRepository.getVolumeFor(firstDate);
        volume2 = volumeRepository.getVolumeFor(secondDate);
        volume3 = volumeRepository.getVolumeFor(thirdDate);
        volume4 = volumeRepository.getVolumeFor(fourthDate);
        volume5 = volumeRepository.getVolumeFor(fifthDate);

        Parser.RawBlip rawA = new Parser.RawBlip(UniqueBlipId.from(42), "blipA", thirdDate, Ring.Hold, Quadrant.tools, "descA1", 30);
        Parser.RawBlip rawB = new Parser.RawBlip(UniqueBlipId.from(42), "blipA", secondDate, Ring.Assess, Quadrant.tools, "descA2", 11);
        Parser.RawBlip rawC = new Parser.RawBlip(UniqueBlipId.from(42), "blipA", fourthDate, Ring.Adopt, Quadrant.tools, "descA3", 40);

        Parser.RawBlip rawD = new Parser.RawBlip(UniqueBlipId.from(52), "blipB", fifthDate, Ring.Adopt, Quadrant.techniques, "later text1", 50);
        Parser.RawBlip rawE = new Parser.RawBlip(UniqueBlipId.from(52), "blipB", firstDate, Ring.Assess, Quadrant.techniques, "init text", 51);
        Parser.RawBlip rawF = new Parser.RawBlip(UniqueBlipId.from(52), "blipB", secondDate, Ring.Assess, Quadrant.techniques, "later text2", 52);
        Parser.RawBlip rawG = new Parser.RawBlip(UniqueBlipId.from(52), "blipB", thirdDate, Ring.Assess, Quadrant.techniques, "later text3 <ignore this/>", 53);
        Parser.RawBlip rawH = new Parser.RawBlip(UniqueBlipId.from(52), "blipB", fourthDate, Ring.Assess, Quadrant.techniques, "later text4", 54);

        Parser.RawBlip rawI = new Parser.RawBlip(UniqueBlipId.from(53), "blipC", secondDate, Ring.Hold, Quadrant.LanguagesAndFrameworks, "descC", 10);

        radar.add(rawA);
        radar.add(rawB);
        radar.add(rawC);
        radar.add(rawD);
        radar.add(rawE);
        radar.add(rawF);
        radar.add(rawG);
        radar.add(rawH);

        radar.add(rawI);

        //radar.updateBlipHistories();

        analyser = new Analyser(radar);
        blipFilters = new BlipFilters(true);
    }

    @Test
    public void shouldCreateTextFromAllDescriptions() {
        String allTheWords = analyser.allWordsFromDescriptions(BlipFilters.All());

        String[] words = allTheWords.split(" ");

        List<String> listOfWords = Arrays.asList(words);
        assertTrue(listOfWords.contains("text1"));
        assertTrue(listOfWords.contains("text2"));
        assertTrue(listOfWords.contains("text3"));
        assertTrue(listOfWords.contains("text4"));

        assertTrue(listOfWords.contains("descA1"));
        assertTrue(listOfWords.contains("descA2"));
        assertTrue(listOfWords.contains("descA2"));

        assertFalse(listOfWords.contains("<ignore"));
        assertFalse(listOfWords.contains("this/>"));

    }

    @Test
    public void shouldCreateSummaryOfText() {
        List <SummaryText> summaryTexts = analyser.createSummaryText();

        assertEquals(9,summaryTexts.size());

        // only one thing on first radar, so one summary line
        SummaryText summaryText = summaryTexts.get(0);
        assertEquals("Jul 2000", summaryText.getDate());
        assertEquals("Assess", summaryText.getRing());
        assertEquals("techniques", summaryText.getQuadrant());
        assertEquals("\"later text1\"", summaryText.getDescription());

        // should be 3 lines for second radar
        // summary text ordered by radar date then, if present, radarID
        assertEquals("10", summaryTexts.get(1).getRadarId());
        assertEquals("11", summaryTexts.get(2).getRadarId());

    }

    @Test
    public void shouldSummariseAmountNewPerRadar() {
        // Integer -> Ratio new to existing
        Map<Integer, Double> amounts = analyser.summaryOfNew(BlipFilters.All());

        assertEquals((Double) 1.0, amounts.get(1));
        assertEquals((Double) (2.0/3.0), amounts.get(2));
        assertEquals((Double) 0.0, amounts.get(3));
        assertEquals((Double) 0.0, amounts.get(4));
        assertEquals((Double) 0.0, amounts.get(5));
    }

    @Test
    public void shouldSummariseBlipDecayFilterByFinalRing() {
        Map<Volume,List<Integer>> decay = analyser.summaryOfDecay(blipFilters.allow(Quadrant.values()).allow(Ring.Adopt));

        assertEquals(5,decay.size());
        assertEquals(5, decay.get(volume1).size());

        List<Integer> first = decay.get(volume2);
        assertEquals(Integer.valueOf(0), first.get(0));
        assertEquals(Integer.valueOf(1), first.get(1));
        assertEquals(Integer.valueOf(1), first.get(2));
        assertEquals(Integer.valueOf(1), first.get(3));
        assertEquals(Integer.valueOf(0), first.get(4));

    }

    @Test
    public void shouldSummariseBlipDecayAllQuadrants() {
        Map<Volume,List<Integer>> decay = analyser.summaryOfDecay(BlipFilters.All());

        assertEquals(5,decay.size());
        assertEquals(5, decay.get(volume1).size());
        List<Integer> secondRadar = decay.get(volume2);
        assertEquals(5, secondRadar.size());
        assertEquals(5, decay.get(volume3).size());
        assertEquals(5, decay.get(volume4).size());
        assertEquals(5, decay.get(volume5).size());

        assertEquals(Integer.valueOf(0), secondRadar.get(0));
        assertEquals(Integer.valueOf(2), secondRadar.get(1));
        assertEquals(Integer.valueOf(1), secondRadar.get(2));
        assertEquals(Integer.valueOf(1), secondRadar.get(3));
        assertEquals(Integer.valueOf(0), secondRadar.get(4));

        List<Integer> first = decay.get(volume1);
        assertEquals(Integer.valueOf(1), first.get(0));
        assertEquals(Integer.valueOf(1), first.get(1));
        assertEquals(Integer.valueOf(1), first.get(2));
        assertEquals(Integer.valueOf(1), first.get(3));
        assertEquals(Integer.valueOf(1), first.get(4));
    }

    @Test
    public void shouldSummariseBlipDecayFilterByQuad() {
        Map<Volume, List<Integer>> decay = analyser.summaryOfDecay(
                blipFilters.allow(Quadrant.tools).allow(Ring.values()));

        assertEquals(5, decay.get(volume1).size());
        assertEquals(5, decay.size());
        List<Integer> first = decay.get(volume1);
        assertEquals(Integer.valueOf(0), first.get(0));
        assertEquals(Integer.valueOf(0), first.get(1));
        assertEquals(Integer.valueOf(0), first.get(2));
        assertEquals(Integer.valueOf(0), first.get(3));
        assertEquals(Integer.valueOf(0), first.get(4));
    }

    @Test
    public void shouldFindLifeTimesAndRadarNumbers() {
        List<BlipLifetime> result = analyser.lifeTimes();

        assertEquals(3, result.size());

        BlipLifetime blipLifetimeA = result.get(0);
        assertEquals(LocalDate.of(2017,6,23), blipLifetimeA.getAppearedDate());
        assertEquals(LocalDate.of(2018,10,22), blipLifetimeA.getLastSeen());
        assertEquals(volume2, blipLifetimeA.getFirstVolume());
        assertEquals(volume4, blipLifetimeA.getLastRadarNum());
        assertEquals("blipA", blipLifetimeA.getName());
        assertEquals(UniqueBlipId.from(42), blipLifetimeA.getId());
        assertEquals(Quadrant.tools, blipLifetimeA.getQuadrant());

        BlipLifetime blipLifetimeB = result.get(2);
        assertEquals(secondDate, blipLifetimeB.getAppearedDate());
        assertEquals(secondDate, blipLifetimeB.getLastSeen());
        assertEquals(volume2, blipLifetimeB.getFirstVolume());
        assertEquals(volume2, blipLifetimeB.getLastRadarNum());
        assertEquals("blipC", blipLifetimeB.getName());
        assertEquals(UniqueBlipId.from(53), blipLifetimeB.getId());
        assertEquals(Quadrant.LanguagesAndFrameworks, blipLifetimeB.getQuadrant());
    }

    @Test
    public void shouldCalcHalfLifeForBlips() {
        LocalDate otherDateA = firstDate.plusDays(100);
        LocalDate otherDateB = firstDate.plusDays(200);

        Set<LocalDate> dates = Set.of(firstDate, secondDate, thirdDate, fourthDate, fifthDate, otherDateA, otherDateB);
        VolumeRepository otherVolumeRepository = new VolumeRepository(dates);

        Radars radar = new Radars(otherVolumeRepository, new BlipRepository());

        for (int blipNumber = 0; blipNumber < 100; blipNumber++) {
            Parser.RawBlip rawA = new Parser.RawBlip(UniqueBlipId.from(blipNumber), "blip"+blipNumber, firstDate,
                    Ring.Assess, Quadrant.tools, "desc", blipNumber);
            radar.add(rawA);
            if (blipNumber<49) {
                Parser.RawBlip later = new Parser.RawBlip(UniqueBlipId.from(blipNumber), "blip"+blipNumber,
                        otherDateA, Ring.Adopt,
                        Quadrant.tools, "desc", 41-blipNumber);
                radar.add(later);
            }
            if (blipNumber<24) {
                Parser.RawBlip later = new Parser.RawBlip(UniqueBlipId.from(blipNumber), "blip"+blipNumber,
                        otherDateB, Ring.Adopt,
                        Quadrant.tools, "desc", 41-blipNumber);
                radar.add(later);
            }
        }
        //radar.updateBlipHistories();
        analyser = new Analyser(radar);
        Map<Integer,Quartiles> halfLife = analyser.findHalfLife(BlipFilters.All());

        // half gone by 100 days
        int firstRadarKey = Math.toIntExact(firstDate.toEpochDay());
        Quartiles quartiles = halfLife.get(firstRadarKey);
        assertEquals(200L, quartiles.get(1));
        assertEquals(100L, quartiles.get(2));

    }

}