package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.UniqueBlipId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class BlipIdTest {

    @Test
    public void shouldHaveEquality() {
        UniqueBlipId blipIdA = UniqueBlipId.from("a");
        UniqueBlipId blipIdB = UniqueBlipId.from("b");

        assertEquals(blipIdA, blipIdA);
        assertNotEquals(blipIdA, blipIdB);

        UniqueBlipId blipIdC = UniqueBlipId.from("b");
        assertEquals(blipIdB, blipIdC);

        UniqueBlipId blipIdD = UniqueBlipId.from(42);
        UniqueBlipId blipIdE = UniqueBlipId.from(43);
        assertEquals(blipIdD, blipIdD);
        assertNotEquals(blipIdD, blipIdE);
        assertNotEquals(blipIdA, blipIdD);

        UniqueBlipId blipIdF = UniqueBlipId.from(42);
        assertEquals(blipIdD, blipIdF);

        UniqueBlipId blipIdG = UniqueBlipId.from("42");
        assertEquals(blipIdG, blipIdF);

    }

    @Test
    public void shouldParseIds() {
        UniqueBlipId blipIdA = UniqueBlipId.parse("3343");
        assertEquals(blipIdA.from(3343), blipIdA);

        UniqueBlipId blipIdB = UniqueBlipId.parse("text");
        assertEquals(blipIdA.from("text"), blipIdB);
    }

    @Test
    public void shouldCompareBlipsIdsWhenBothInt() {
        UniqueBlipId blipIdA = UniqueBlipId.from(5);
        UniqueBlipId blipIdB = UniqueBlipId.from(500);

        assertEquals(Integer.compare(5,500), UniqueBlipId.compare(blipIdA, blipIdB));
        assertEquals(Integer.compare(500,5), UniqueBlipId.compare(blipIdB, blipIdA));
        assertEquals(0, UniqueBlipId.compare(blipIdA, blipIdA));
    }

    @Test
    public void shouldCompareBlipsIdsWhenBothString() {
        UniqueBlipId blipIdA = UniqueBlipId.from("a");
        UniqueBlipId blipIdB = UniqueBlipId.from("b");

        assertEquals("a".compareTo("b"), UniqueBlipId.compare(blipIdA, blipIdB));
        assertEquals("b".compareTo("a"), UniqueBlipId.compare(blipIdB, blipIdA));
        assertEquals(0, UniqueBlipId.compare(blipIdA, blipIdA));
    }

    @Test
    public void shouldCompareBlipsIdsWhenMixed() {
        UniqueBlipId blipIdA = UniqueBlipId.from("a");
        UniqueBlipId blipIdB = UniqueBlipId.from(42);

        // fall back to string comparison
        assertEquals("a".compareTo("42"), UniqueBlipId.compare(blipIdA, blipIdB));
        assertEquals("42".compareTo("a"), UniqueBlipId.compare(blipIdB, blipIdA));
        assertEquals(0, UniqueBlipId.compare(blipIdA, blipIdA));
    }
}
