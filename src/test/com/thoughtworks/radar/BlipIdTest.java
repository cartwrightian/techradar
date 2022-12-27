package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.BlipId;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BlipIdTest {

    @Test
    public void shouldHaveEquality() {
        BlipId blipIdA = BlipId.from("a");
        BlipId blipIdB = BlipId.from("b");

        assertTrue(blipIdA.equals(blipIdA));
        assertFalse(blipIdA.equals(blipIdB));

        BlipId blipIdC = BlipId.from("b");
        assertTrue(blipIdB.equals(blipIdC));

        BlipId blipIdD = BlipId.from(42);
        BlipId blipIdE = BlipId.from(43);
        assertTrue(blipIdD.equals(blipIdD));
        assertFalse(blipIdD.equals(blipIdE));
        assertFalse(blipIdA.equals(blipIdD));

        BlipId blipIdF = BlipId.from(42);
        assertTrue(blipIdD.equals(blipIdF));

        BlipId blipIdG = BlipId.from("42");
        assertTrue(blipIdG.equals(blipIdF));

    }

    @Test
    public void shouldParseIds() {
        BlipId blipIdA = BlipId.parse("3343");
        assertEquals(blipIdA.from(3343), blipIdA);

        BlipId blipIdB = BlipId.parse("text");
        assertEquals(blipIdA.from("text"), blipIdB);
    }

    @Test
    public void shouldCompareBlipsIdsWhenBothInt() {
        BlipId blipIdA = BlipId.from(5);
        BlipId blipIdB = BlipId.from(500);

        assertEquals(Integer.compare(5,500), BlipId.compare(blipIdA, blipIdB));
        assertEquals(Integer.compare(500,5), BlipId.compare(blipIdB, blipIdA));
        assertEquals(0, BlipId.compare(blipIdA, blipIdA));
    }

    @Test
    public void shouldCompareBlipsIdsWhenBothString() {
        BlipId blipIdA = BlipId.from("a");
        BlipId blipIdB = BlipId.from("b");

        assertEquals("a".compareTo("b"), BlipId.compare(blipIdA, blipIdB));
        assertEquals("b".compareTo("a"), BlipId.compare(blipIdB, blipIdA));
        assertEquals(0, BlipId.compare(blipIdA, blipIdA));
    }

    @Test
    public void shouldCompareBlipsIdsWhenMixed() {
        BlipId blipIdA = BlipId.from("a");
        BlipId blipIdB = BlipId.from(42);

        // fall back to string comparison
        assertEquals("a".compareTo("42"), BlipId.compare(blipIdA, blipIdB));
        assertEquals("42".compareTo("a"), BlipId.compare(blipIdB, blipIdA));
        assertEquals(0, BlipId.compare(blipIdA, blipIdA));
    }
}
