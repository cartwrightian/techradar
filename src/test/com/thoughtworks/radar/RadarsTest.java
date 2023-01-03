package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RadarsTest {
    private LocalDate secondDate;
    private LocalDate firstDate;
    private LocalDate thirdDate;
    private LocalDate fourthDate;
    private LocalDate fifthDate;

    private Radars radar;
    private Parser.RawBlip rawA;
    private Parser.RawBlip rawB;
    private Parser.RawBlip rawC;
    private Parser.RawBlip rawD;

    private Parser.RawBlip rawE;
    private Parser.RawBlip rawF;
    private Parser.RawBlip rawG;

    private VolumeRepository volumeRepository;

    @BeforeEach
    public void beforeEachTestRuns() {
        firstDate = LocalDate.of(2017, 6, 5);
        secondDate = LocalDate.of(2017, 7, 10);
        thirdDate = LocalDate.of(2017, 8, 15);
        fourthDate = LocalDate.of(2017, 11, 20);
        fifthDate = LocalDate.of(2022, 1, 31);

        Set<LocalDate> dates = Set.of(firstDate, secondDate, thirdDate, fourthDate, fifthDate);
        volumeRepository = new VolumeRepository(dates);

        radar = new Radars(volumeRepository);
        rawA = new Parser.RawBlip(UniqueBlipId.from(1), "blipA", secondDate, Ring.Hold, Quadrant.tools, "desc", 11);
        rawB = new Parser.RawBlip(UniqueBlipId.from(1), "blipA", firstDate, Ring.Assess, Quadrant.tools, "desc", 22);
        rawC = new Parser.RawBlip(UniqueBlipId.from(1), "blipA", thirdDate, Ring.Adopt, Quadrant.tools, "desc", 33);

        rawD = new Parser.RawBlip(UniqueBlipId.from(4), "blipA", thirdDate, Ring.Adopt, Quadrant.tools, "desc", 44);

        rawE = new Parser.RawBlip(UniqueBlipId.from(5), "blipC", secondDate, Ring.Adopt, Quadrant.techniques, "desc", 51);
        rawF = new Parser.RawBlip(UniqueBlipId.from(5), "blipC", thirdDate, Ring.Hold, Quadrant.techniques, "desc", 52);
        rawG = new Parser.RawBlip(UniqueBlipId.from(5), "blipC", fourthDate, Ring.Hold, Quadrant.techniques, "desc", 53);

        radar.add(rawA);
        radar.add(rawB);
        radar.add(rawC);
        radar.add(rawD);
        radar.add(rawE);
        radar.add(rawF);
        radar.add(rawG);

        //radar.updateBlipHistories();
    }

    @Test
    void shouldHaveExpectedMoveCount() {
        Blip blip1 = radar.getBlip(UniqueBlipId.from(1));
        assertEquals(2, blip1.getNumberBlipMoves());

        Blip blip2 = radar.getBlip(UniqueBlipId.from(4));
        assertEquals(0, blip2.getNumberBlipMoves());

        Blip blip3 = radar.getBlip(UniqueBlipId.from(5));
        assertEquals(1, blip3.getNumberBlipMoves());
    }

    @Test
    public void shouldHaveExpectedVolumeNumbers() {
        assertEquals(1, volumeRepository.getVolumeFor(firstDate).getNumber());
        assertEquals(2, volumeRepository.getVolumeFor(secondDate).getNumber());
        assertEquals(3, volumeRepository.getVolumeFor(thirdDate).getNumber());
        assertEquals(4, volumeRepository.getVolumeFor(fourthDate).getNumber());
    }

    @Test
    public void shouldFindMostMovedAndNonMovers() {
        // not a move, same ring
        Parser.RawBlip rawX = new Parser.RawBlip(UniqueBlipId.from(1), "blipA", fifthDate, Ring.Adopt, Quadrant.tools, "desc", 33);
        radar.add(rawX);

        // 5 moves
        UniqueBlipId blipId = UniqueBlipId.from(52);
        radar.add(new Parser.RawBlip(blipId, "blipZ", firstDate, Ring.Adopt, Quadrant.tools, "desc", 71));
        radar.add(new Parser.RawBlip(blipId, "blipZ", secondDate, Ring.Trial, Quadrant.tools, "desc", 72));
        radar.add(new Parser.RawBlip(blipId, "blipZ", thirdDate, Ring.Assess, Quadrant.tools, "desc", 73));
        radar.add(new Parser.RawBlip(blipId, "blipZ", fourthDate, Ring.Hold, Quadrant.tools, "desc", 74));
        radar.add(new Parser.RawBlip(blipId, "blipZ", fifthDate, Ring.Assess, Quadrant.tools, "desc", 75));

        //radar.updateBlipHistories();

        Blip added = radar.getBlip(blipId);
        assertEquals(4, added.getNumberBlipMoves());

        List<UniqueBlipId> mostMoves = radar.mostMoves(BlipFilters.All(), 10).stream().map(Blip::getId).collect(Collectors.toList());

        assertEquals(4, mostMoves.size());
        UniqueBlipId firstResult = mostMoves.get(0);
        UniqueBlipId secondResult = mostMoves.get(1);

        assertEquals(blipId, firstResult, mostMoves.toString());
        //assertEquals(2, firstResult.getNumberBlipMoves().intValue());

        assertEquals(UniqueBlipId.from(1), secondResult, mostMoves.toString());
        //assertEquals(2, secondResult.getNumberBlipMoves().intValue()); // never moved from adopt

        List<Blip> nonMovers = radar.nonMovers(BlipFilters.All());
        assertEquals(1, nonMovers.size());
        assertEquals(UniqueBlipId.from(4), nonMovers.get(0).getId());
    }

    @Test
    public void shouldGetLongestOnRadar() {
        List<Blip> result = radar.longestOnRadar(BlipFilters.All(), 2);
        assertEquals(2, result.size());

        assertEquals(UniqueBlipId.from(5), result.get(0).getId(), result.toString());
        assertEquals(UniqueBlipId.from(1), result.get(1).getId(), result.toString());
    }

    @Test
    public void shouldAddBlipsAndIndexRadars() {
        assertEquals(5, radar.numberOfRadars());

        assertEquals(1, radar.getEditionFrom(firstDate).getNumber());
        assertEquals(2, radar.getEditionFrom(secondDate).getNumber());
        assertEquals(3, radar.getEditionFrom(thirdDate).getNumber());
        assertEquals(4, radar.getEditionFrom(fourthDate).getNumber());
        assertEquals(5, radar.getEditionFrom(fifthDate).getNumber());

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
        Volume volume1 = volumeRepository.getVolumeFor(firstDate);
        Volume volume3 = volumeRepository.getVolumeFor(thirdDate);

        List<Blip> blips = radar.blipsVisibleOn(volume1);
        assertEquals(1, blips.size());
        assertEquals(Quadrant.tools, blips.get(0).getFirstQuadrant());
        assertEquals(Ring.Assess, blips.get(0).firstRing());

        blips = radar.blipsVisibleOn(volume3);
        assertEquals(3, blips.size());
        assertEquals(UniqueBlipId.from(1), blips.get(0).getId());
        assertEquals(UniqueBlipId.from(4), blips.get(1).getId());
    }

    @Test
    public void shouldFindBlipsThatWereEverInAdoptButFadedOnHold() {
        List<Blip> blips = radar.everInAdoptToHold();

        assertFalse(blips.isEmpty());
        assertEquals(1, blips.size());
    }

}