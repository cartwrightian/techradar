package com.thoughtworks.radar;

import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class RadarTest {
    @Test
    public void shouldAddBlips() {
        Radar radar = new Radar();
        Parser.RawBlip rawA = new Parser.RawBlip(1, "blipA", LocalDate.of(2017,11,24), Ring.Hold, Quadrant.tools);
        Parser.RawBlip rawB = new Parser.RawBlip(1, "blipA", LocalDate.of(2017,6,23), Ring.Assess, Quadrant.tools);
        Parser.RawBlip rawC = new Parser.RawBlip(1, "blipA", LocalDate.of(2018,10,22), Ring.Adopt, Quadrant.tools);

        radar.add(rawA);
        radar.add(rawB);
        radar.add(rawC);

        List<Blip> blips = radar.getBlips();
        assertEquals(3, blips.get(0).getHistory().size());
    }

}