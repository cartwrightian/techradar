package com.thoughtworks.radar;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class SummaryTestTest {

    @Test
    public void shouldCreate() {
        SummaryText text = new SummaryText(42,LocalDate.of(1962,12,1),Ring.Assess,
                Quadrant.platforms,"description",12);

        assertEquals("42", text.getId());
        assertEquals("Dec 1962", text.getDate());
        assertEquals("Assess", text.getRing());
        assertEquals("platforms", text.getQuadrant());
        assertEquals("description", text.getDescription());
        assertEquals("12", text.getRadarId());
    }

    @Test
    public void shouldRemoveMarkup() {
        SummaryText text = new SummaryText(42,LocalDate.of(1962,12,1),Ring.Assess, Quadrant.platforms,
                "<p><strong>without</strong> <a href=\"https://somewhere.net/place\">markup</a> <p>text</p></p>", 12);
        assertEquals("without markup text", text.getDescription());
    }

    @Test
    public void shouldHaveEmptyStringForRadarIdIfMissing() {
        SummaryText text = new SummaryText(42,LocalDate.of(1962,12,1),Ring.Assess,
                Quadrant.platforms,"description",-1);
        assertEquals("\"\"", text.getRadarId());
    }
}
