package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.BlipId;
import junit.framework.TestCase;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class BlipIdTest {

    @Test
    public void shouldHaveEquality() {
        BlipId blipIdA = BlipId.from("a");
        BlipId blipIdB = BlipId.from("b");

        assertEquals(blipIdA, blipIdA);
        assertNotEquals(blipIdA, blipIdB);

        BlipId blipIdC = BlipId.from("b");
        assertEquals(blipIdB, blipIdC);

        BlipId blipIdD = BlipId.from(42);
        BlipId blipIdE = BlipId.from(43);
        assertEquals(blipIdD, blipIdD);
        assertNotEquals(blipIdD, blipIdE);
        assertNotEquals(blipIdA, blipIdD);

        BlipId blipIdF = BlipId.from(42);
        assertEquals(blipIdD, blipIdF);

        BlipId blipIdG = BlipId.from("42");
        assertEquals(blipIdG, blipIdF);

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
