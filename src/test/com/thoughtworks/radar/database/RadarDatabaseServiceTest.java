package com.thoughtworks.radar.database;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.thoughtworks.radar.Database.RadarDatabaseService;
import com.thoughtworks.radar.Radars;
import com.thoughtworks.radar.domain.Blip;
import com.thoughtworks.radar.domain.BlipEntry;
import com.thoughtworks.radar.domain.UniqueBlipId;
import com.thoughtworks.radar.domain.Volume;
import com.thoughtworks.radar.file.RadarFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RadarDatabaseServiceTest {

    private JdbcPooledConnectionSource connectionSource;

    @BeforeEach
    void beforeEachTest() throws SQLException {
        connectionSource = new JdbcPooledConnectionSource("jdbc:h2:mem:myDb");

        TableUtils.dropTable(connectionSource, BlipEntry.class, true);
        TableUtils.dropTable(connectionSource, Blip.class, true);
        TableUtils.dropTable(connectionSource, Volume.class, true);

    }

    @BeforeEach
    void afterEachTest() throws Exception {
        connectionSource = new JdbcPooledConnectionSource("jdbc:h2:mem:myDb");

        TableUtils.dropTable(connectionSource, BlipEntry.class, true);
        TableUtils.dropTable(connectionSource, Blip.class, true);
        TableUtils.dropTable(connectionSource, Volume.class, true);

        connectionSource.close();
    }

    @Test
    void shouldSaveAndThenRetrieve() throws IOException, SQLException {
        RadarDatabaseService databaseService = new RadarDatabaseService(connectionSource);

        RadarFileService fileService = new RadarFileService("data");
        Radars radars = fileService.loadRadars();

        databaseService.save(radars);

        Radars result = databaseService.load();

        assertEquals(radars.numberOfRadars(), radars.numberOfRadars());
        assertEquals(radars.getVolumeRepository().getVolumes(), result.getVolumeRepository().getVolumes());
        List<Blip> expectedBlips = radars.getBlips();
        List<Blip> resultBlips = result.getBlips();
        assertEquals(expectedBlips.size(), resultBlips.size());

        Set<UniqueBlipId> expectedBlipIds = expectedBlips.stream().map(Blip::getId).collect(Collectors.toSet());
        Set<UniqueBlipId> resultBlipIds = resultBlips.stream().map(Blip::getId).collect(Collectors.toSet());
        assertEquals(expectedBlipIds, resultBlipIds);

        List<BlipEntry> expectedBlipEntries = radars.getBlipEntries();
        List<BlipEntry> resultBlipEntries = result.getBlipEntries();
        assertEquals(expectedBlipEntries.size(), resultBlipEntries.size());

        Blip expectedBlip = expectedBlips.get(expectedBlips.size() / 2);
        Optional<Blip> find = resultBlips.stream().filter(blip -> blip.getId().equals(expectedBlip.getId())).findFirst();

        assertTrue(find.isPresent());
        Blip found = find.get();
        assertEquals(expectedBlip.getName(), found.getName());

        assertEquals(expectedBlip.getHistory(), found.getHistory());
    }
}
