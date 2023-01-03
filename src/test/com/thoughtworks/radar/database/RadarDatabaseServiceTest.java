package com.thoughtworks.radar.database;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.thoughtworks.radar.Database.RadarDatabaseService;
import com.thoughtworks.radar.Radars;
import com.thoughtworks.radar.domain.Blip;
import com.thoughtworks.radar.domain.BlipEntry;
import com.thoughtworks.radar.domain.Volume;
import com.thoughtworks.radar.file.RadarFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        //assertEquals(radars.getBlips(), result.getBlips());
        //assertEquals(radars.getBlipEntries(), result.getBlipEntries());
    }
}
