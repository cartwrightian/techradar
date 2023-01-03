package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.UniqueBlipId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class BlipIdTest {

    @Test
    public void shouldHaveEquality() {

        UniqueBlipId blipIdD = UniqueBlipId.from(42);
        UniqueBlipId blipIdE = UniqueBlipId.from(43);
        assertEquals(blipIdD, blipIdD);
        assertNotEquals(blipIdD, blipIdE);

        UniqueBlipId blipIdF = UniqueBlipId.from(42);
        assertEquals(blipIdD, blipIdF);

        UniqueBlipId blipIdG = UniqueBlipId.from("42");
        assertEquals(blipIdG, blipIdF);

    }

    @Test
    public void shouldParseIds() {
        UniqueBlipId blipIdA = UniqueBlipId.parse("3343");
        assertEquals(UniqueBlipId.from(3343), blipIdA);
    }

    @Test
    public void shouldCompareBlipsIdsWhenBothInt() {
        UniqueBlipId blipIdA = UniqueBlipId.from(5);
        UniqueBlipId blipIdB = UniqueBlipId.from(500);

        assertEquals(Integer.compare(5,500), UniqueBlipId.compare(blipIdA, blipIdB));
        assertEquals(Integer.compare(500,5), UniqueBlipId.compare(blipIdB, blipIdA));
        assertEquals(0, UniqueBlipId.compare(blipIdA, blipIdA));
    }

}
