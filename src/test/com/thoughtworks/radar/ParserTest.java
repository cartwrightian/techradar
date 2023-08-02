package com.thoughtworks.radar;

import com.thoughtworks.radar.domain.*;
import com.thoughtworks.radar.repository.VolumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;

import static com.thoughtworks.radar.domain.Ring.Trial;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ParserTest {
    private static final String example = "[{\"date\":\"2010-01\",\"blips\":[{\"urlLabel\":\"\",\"radius\":\"\",\"quadrant\":\"" +
            "platforms\",\"lastModified\":\"\",\"description\":\"\",\"quadrantSortOrder\":\"3\",\"ring\":\"Trial\",\"" +
            "ringSortOrder\":\"2\",\"blip_id\":\"6661\",\"faded\":\"\",\"movement\":\"c\",\"name\":\"Android\",\"" +
            "editStatus\":\"Include w/o Write Up\",\"type\":\"Blip\",\"theta\":\"\"},{\"urlLabel\":\"\",\"radius\":\"\",\"" +
            "quadrant\":\"tools\",\"lastModified\":\"\",\"description\":\"\",\"quadrantSortOrder\":\"2\",\"ring\":\"" +
            "Adopt\",\"ringSortOrder\":\"1\",\"blip_id\":\"6663\",\"faded\":\"\",\"movement\":\"c\",\"name\":\"ASP.NET MVC\",\"" +
            "editStatus\":\"Include w/o Write Up\",\"type\":\"Blip\",\"theta\":\"\"}]}," +
            "{\"date\":\"2010-08\",\"blips\":[{\"urlLabel\":\"\",\"radius\":\"\",\"quadrant\":\"platforms\"," +
            "\"lastModified\":\"2010-08\",\"description\":\"While .NET has proven itself as a solid platform, many practitioners are dissatisfied with many of the default Microsoft tools and practices. This has led to the growth of the Alt.NET community, which champions techniques that we find more effective along with (usually opensource) tools that better support them.\",\"" +
            "quadrantSortOrder\":\"3\",\"ring\":\"Adopt\",\"ringSortOrder\":\"1\",\"blip_id\":\"6662\",\"faded\":\"\",\"" +
            "movement\":\"c\",\"name\":\"ALT.NET\",\"editStatus\":\"Include w/o Write Up\",\"type\":\"Blip\",\"theta\":\"\"}," +
            "{\"urlLabel\":\"\",\"radius\":\"\",\"quadrant\":\"" +
            "platforms\",\"lastModified\":\"\",\"description\":\"\",\"quadrantSortOrder\":\"3\",\"ring\":\"Adopt\",\"" +
            "ringSortOrder\":\"2\",\"blip_id\":\"6661\",\"faded\":\"\",\"movement\":\"c\",\"name\":\"Android\",\"" +
            "editStatus\":\"Include w/o Write Up\",\"type\":\"Blip\",\"theta\":\"\"}," +
            "{\"radarId\":\"105\",\"name\":\"WebAssembly\",\"radius\":\"290\",\"quadrant\":\"languages-and-frameworks\",\"" +
            "description\":\"<p><a href=\\\"http://webassembly.org/\\\"><strong>WebAssembly</strong></a> is a big step forward in " +
            "the capabilities of the browser as a code execution environment. Supported by all major browsers and backward compatible, " +
            "it's a binary compilation format designed to run in the browser at near native speeds. It opens up the range of languages " +
            "you can use to write front-end functionality, with early focus on C, C++ and Rust, and it's also an LLVM compilation target. " +
            "When run in the sandbox, it can interact with JavaScript and shares the same permissions and security model. " +
            "When used with <a href=\\\"http://hacks.mozilla.org/2018/01/making-webassembly-even-faster-firefoxs-new-streaming-and-tiering-compiler" +
            "/\\\">Firefox’s new streaming compiler</a>, it also results in faster page initialization. Although it's still early days, this W3C " +
            "standard is definitely one to start exploring.</p>\",\"ring\":\"Assess\",\"blip_id\":\"9999\",\"movement\":\"t\",\"theta\":\"355\"}" +
            "]," +
            "\"translated_locale\":\"en\",\"" +
            "last_modified\":\"2017-09-07T11:19:36+00:00\",\"last_modified_by\":\"wwwsuperuser\",\"id\":\"" +
            "2018-05/blips/2018_05\",\"content_category\":\"\"}]";

    private RadarJsonParser parser;

    @BeforeEach
    public void beforeEachTestRuns() {
        parser = new LegacyParser();
    }

    @Test
    public void shouldParseBlips() throws IOException {
        Radars radar = parser.parse(example);

        VolumeRepository volumeRepository = radar.getVolumeRepository();

        List<Volume> volumes = volumeRepository.getVolumes();
        assertEquals(2, volumes.size());

        List<Blip> blips = radar.getBlips();

        assertEquals(4, blips.size());

        Blip blip = blips.get(0);
        assertEquals("Android", blip.getName());
        assertEquals(UniqueBlipId.from(6661), blip.getId());
        assertEquals(Quadrant.platforms, blip.getFirstQuadrant());

        assertEquals("ALT.NET", blips.get(1).getName());
        String expected = "While .NET has proven itself as a solid platform, many practitioners are dissatisfied with " +
                "many of the default Microsoft tools and practices. This has led to the growth of the Alt.NET " +
                "community, which champions techniques that we find more effective along with (usually opensource) " +
                "tools that better support them.";
        assertEquals(expected, blips.get(1).getDescription());

        assertEquals("ASP.NET MVC", blips.get(2).getName());

        assertEquals("WebAssembly", blips.get(3).getName());

        // check parsed radar id, which is not on all blips
        LocalDate date = LocalDate.of(2010,8,1);
        Volume volume2 = new Volume(2, date);

        assertEquals(105, blips.get(3).idOnRadar(volume2));

        List<BlipEntry> blipEntry = new LinkedList<>(blip.getHistory());

        assertEquals(2, blipEntry.size());
        BlipEntry entryA = blipEntry.get(0);
        assertEquals(Trial, entryA.getRing());
        assertEquals(2010, entryA.getDate().getYear());
        assertEquals(Month.JANUARY, entryA.getDate().getMonth());

        BlipEntry entryB = blipEntry.get(1);
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