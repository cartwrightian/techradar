package com.thoughtworks.radar;

import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static com.thoughtworks.radar.Ring.Trial;
import static org.junit.Assert.assertEquals;


public class ParserTest {
    private static String example = "[{\"date\":\"2010-01\",\"blips\":[{\"urlLabel\":\"\",\"radius\":\"\",\"quadrant\":\"" +
            "platforms\",\"lastModified\":\"\",\"description\":\"\",\"quadrantSortOrder\":\"3\",\"ring\":\"Trial\",\"" +
            "ringSortOrder\":\"2\",\"id\":\"6661\",\"faded\":\"\",\"movement\":\"c\",\"name\":\"Android\",\"" +
            "editStatus\":\"Include w/o Write Up\",\"type\":\"Blip\",\"theta\":\"\"},{\"urlLabel\":\"\",\"radius\":\"\",\"" +
            "quadrant\":\"tools\",\"lastModified\":\"\",\"description\":\"\",\"quadrantSortOrder\":\"2\",\"ring\":\"" +
            "Adopt\",\"ringSortOrder\":\"1\",\"id\":\"6663\",\"faded\":\"\",\"movement\":\"c\",\"name\":\"ASP.NET MVC\",\"" +
            "editStatus\":\"Include w/o Write Up\",\"type\":\"Blip\",\"theta\":\"\"}]}," +
            "{\"date\":\"2010-08\",\"blips\":[{\"urlLabel\":\"\",\"radius\":\"\",\"quadrant\":\"platforms\"," +
            "\"lastModified\":\"2010-08\",\"description\":\"While .NET has proven itself as a solid platform, many practitioners are dissatisfied with many of the default Microsoft tools and practices. This has led to the growth of the Alt.NET community, which champions techniques that we find more effective along with (usually opensource) tools that better support them.\",\"" +
            "quadrantSortOrder\":\"3\",\"ring\":\"Adopt\",\"ringSortOrder\":\"1\",\"id\":\"6662\",\"faded\":\"\",\"" +
            "movement\":\"c\",\"name\":\"ALT.NET\",\"editStatus\":\"Include w/o Write Up\",\"type\":\"Blip\",\"theta\":\"\"}," +
            "{\"urlLabel\":\"\",\"radius\":\"\",\"quadrant\":\"" +
            "platforms\",\"lastModified\":\"\",\"description\":\"\",\"quadrantSortOrder\":\"3\",\"ring\":\"Adopt\",\"" +
            "ringSortOrder\":\"2\",\"id\":\"6661\",\"faded\":\"\",\"movement\":\"c\",\"name\":\"Android\",\"" +
            "editStatus\":\"Include w/o Write Up\",\"type\":\"Blip\",\"theta\":\"\"}" +
            "]" +
            "\"translated_locale\":\"en\",\"" +
            "last_modified\":\"2017-09-07T11:19:36+00:00\",\"last_modified_by\":\"wwwsuperuser\",\"id\":\"" +
            "2018-05/blips/2018_05\",\"content_category\":\"\"}]";

    private Parser parser;

    @Before
    public void beforeEachTestRuns() {
        parser = new Parser();
    }

    @Test
    public void shouldParseBlips() throws ParseException {
        Radar radar = parser.parse(example);

        List<Blip> blips = radar.getBlips();

        assertEquals(3, blips.size());

        Blip blip = blips.get(0);
        assertEquals("Android", blip.getName());
        assertEquals(6661, blip.getId());

        assertEquals("ASP.NET MVC", blips.get(2).getName());
        assertEquals("ALT.NET", blips.get(1).getName());

        List<BlipHistory> blipHistory = blip.getHistory();
        assertEquals(2, blipHistory.size());
        BlipHistory entryA = blipHistory.get(0);
        assertEquals(Trial, entryA.getRing());
        assertEquals(2010, entryA.getDate().getYear());
        assertEquals(Month.JANUARY, entryA.getDate().getMonth());
        BlipHistory entryB = blipHistory.get(1);
        assertEquals(Ring.Adopt, entryB.getRing());
        assertEquals(2010, entryB.getDate().getYear());
        assertEquals(Month.AUGUST, entryB.getDate().getMonth());
    }

    @Test
    public void shouldParseDate() {
        String text = "2018-11";
        LocalDate result = parser.parseDate(text);

        assertEquals(2018, result.getYear());
        assertEquals(Month.NOVEMBER, result.getMonth());
    }

}