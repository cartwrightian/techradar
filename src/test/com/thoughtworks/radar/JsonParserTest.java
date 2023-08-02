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
import static org.junit.jupiter.api.Assertions.assertTrue;


public class JsonParserTest {
    private static final String example = "[{\"ring\":\"assess\",\"blip_id\":202304063,\"quadrant\":\"tools\",\"volume_date\":\"2023-04\",\"description\":\"" +
            "Knowledge management is critical for tech workers, as we need to be constantly learning and staying up to date with the latest technology developments. " +
            "ecently, tools such as Obsidian and Logseq have emerged in the category of note-taking tools that support linking notes to form a knowledge graph, while " +
            "storing them in plain markdown files in a local directory, thus letting users own their data. These tools help users organize and link their notes in a " +
            "flexible, nonlinear way.\\n\\nObsidian has a rich repository of community plugins. Some that have caught our attention, in particular, are Canvas, akin to a " +
            "local version of Miro or Mural, and Dataview, which effectively treats your notes as a database and provides a query language for filtering, sorting and " +
            "extracting data from your markdown notes.\",\"blip_selector\":\"obsidian\",\"name\":\"Obsidian\",\"display_name\":\"Obsidian\"," +
            "\"url\":\"/radar/tools/obsidian\",\"isCurrentEdition\":true,\"isRecentEdition\":true,\"last_modified_date\":\"2023-08-01T10:34:30.917Z\"},{\"ring\":\"trial\"," +
            "\"blip_id\":202304037,\"quadrant\":\"tools\",\"volume_date\":\"2022-11\",\"description\":\"Within any organization, API producers and consumers need to stay in sync about " +
            "the schemas that will be used for communication among them. Especially as the number of APIs and related producers and consumers grow in the organization, " +
            "what may start with simply passing around schemas among teams will start to hit scaling challenges. Faced with this issue, some of our teams have turned to " +
            "Apicurio Registry, an open-source, centralized registry for various types of schemas and API artifacts, including OpenAPI specifications and Protobuf and Avro " +
            "schemas. Apicurio Registry allows users to interact with it through a UI as well as a REST API and a Maven plugin. It also has the option to enforce schema evolution " +
            "restrictions, such as backward compatibility. Moreover, when it comes to working with Kafka clients, Apicurio Registry is compatible with Confluent Schema Registry. " +
            "While our teams have found Confluent Schema Registry's documentation more helpful, Apicurio Registry meets their needs for a source of truth for various schemas.\"," +
            "\"blip_selector\":\"apicurio-registry\",\"name\":\"Apicurio Registry\",\"display_name\":\"Apicurio Registry\",\"url\":\"/radar/tools/apicurio-registry\"," +
            "\"isCurrentEdition\":false,\"isRecentEdition\":false,\"last_modified_date\":\"2023-08-01T10:34:30.917Z\"}," +
            "{\"ring\":\"trial\",\"blip_id\":202304063,\"quadrant\":\"tools\",\"volume_date\":\"2022-11\",\"description\":\"Knowledge management is critical for tech workers, " +
            "as we need to be constantly learning and staying up to date with the latest technology developments. Recently, tools such as Obsidian and Logseq have emerged in the " +
            "category of note-taking tools that support linking notes to form a knowledge graph, while storing them in plain markdown files in a local directory, thus letting users " +
            "own their data. These tools help users organize and link their notes in a flexible, nonlinear way.\\n\\nObsidian has a rich repository of community plugins. Some that have " +
            "caught our attention, in particular, are Canvas, akin to a local version of Miro or Mural, and Dataview, which effectively treats your notes as a database and provides a " +
            "query language for filtering, sorting and extracting data from your markdown notes.\",\"blip_selector\":\"obsidian\",\"name\":\"Obsidian\",\"display_name\":" +
            "\"Obsidian\",\"url\":\"/radar/tools/obsidian\",\"isCurrentEdition\":true,\"isRecentEdition\":true,\"last_modified_date\":\"2023-08-01T10:34:30.917Z\"}" +
            "]";

    private RadarJsonParser parser;

    @BeforeEach
    public void beforeEachTestRuns() {
        parser = new JsonParser();
    }

    @Test
    public void shouldParseBlips() throws IOException {
        Radars radar = parser.parse(example);

        VolumeRepository volumeRepository = radar.getVolumeRepository();

        List<Volume> volumes = volumeRepository.getVolumes();
        assertEquals(2, volumes.size());

        List<Blip> blips = radar.getBlips();

        assertEquals(2, blips.size());

        Blip firstBlip = blips.get(0);
        assertEquals("Apicurio Registry", firstBlip.getName());
        assertEquals(UniqueBlipId.from(202304037), firstBlip.getId());
        assertEquals(Quadrant.tools, firstBlip.getFirstQuadrant());

        Blip secondBlip = blips.get(1);
        assertEquals("Obsidian", secondBlip.getName());
        String expectedStart = "Knowledge management is critical for tech workers, as we need to be constantly learning and staying up to date with the latest technology developments.";
        assertTrue(secondBlip.getDescription().startsWith(expectedStart), "Unexpected description " + secondBlip.getDescription());

        // check parsed radar id, which is not on all blips
        LocalDate date = LocalDate.of(2010,8,1);
        Volume volume2 = new Volume(2, date);

        List<BlipEntry> blipEntry = new LinkedList<>(secondBlip.getHistory());

        assertEquals(2, blipEntry.size());
        BlipEntry entryA = blipEntry.get(0);
        assertEquals(Trial, entryA.getRing());
        assertEquals(2022, entryA.getDate().getYear());
        assertEquals(Month.NOVEMBER, entryA.getDate().getMonth());

        BlipEntry entryB = blipEntry.get(1);
        assertEquals(Ring.Assess, entryB.getRing());
        assertEquals(2023, entryB.getDate().getYear());
        assertEquals(Month.APRIL, entryB.getDate().getMonth());

    }

    @Test
    public void shouldParseDate() {
        String text = "2023-04";
        LocalDate result = parser.parseDate(text);

        assertEquals(2023, result.getYear());
        assertEquals(Month.APRIL, result.getMonth());
    }

}