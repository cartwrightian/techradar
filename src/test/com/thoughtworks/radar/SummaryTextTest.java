package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.Quadrant;
import com.thoughtworks.radar.domain.Ring;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SummaryTextTest {

    @Test
    public void shouldCreate() {
        SummaryText text = new SummaryText("name", LocalDate.of(1962,12,1), Ring.Assess,
                Quadrant.platforms,"description",12);

        assertEquals("name", text.getName());
        assertEquals("Dec 1962", text.getDate());
        assertEquals("Assess", text.getRing());
        assertEquals("platforms", text.getQuadrant());
        assertEquals("\"description\"", text.getDescription());
        assertEquals("12", text.getRadarId());
    }

    @Test
    public void shouldRemoveMarkup() {
        SummaryText text = new SummaryText("name", LocalDate.of(1962,12,1),
                Ring.Assess, Quadrant.platforms,
                "<p><strong>without</strong> <a href=\"https://somewhere.net/place\">markup</a> <p>text</p></p>", 12);
        assertEquals("\"without markup text\"", text.getDescription());
    }

    @Test
    public void shouldEscapeEmbeddedQuotes() {
        SummaryText text = new SummaryText("name", LocalDate.of(1962,12,1),Ring.Assess,
                Quadrant.platforms,"description with \"quotes\" inside",12);
        assertEquals("\"description with 'quotes' inside\"", text.getDescription());

    }

    @Test
    public void shouldHaveEmptyStringForRadarIdIfMissing() {
        SummaryText text = new SummaryText("name", LocalDate.of(1962,12,1),Ring.Assess,
                Quadrant.platforms,"description",-1);
        assertEquals("\"\"", text.getRadarId());
    }
}
