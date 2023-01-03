package com.thoughtworks.radar.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import com.thoughtworks.radar.domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlipEntryDatabaseTest {

    private JdbcPooledConnectionSource connectionSource;

    @BeforeEach
    void beforeEachTest() throws SQLException {
        connectionSource = new JdbcPooledConnectionSource("jdbc:h2:mem:myDb");

        TableUtils.dropTable(connectionSource, BlipEntry.class, true);
        TableUtils.dropTable(connectionSource, Volume.class, true);

        TableUtils.createTable(connectionSource, Volume.class);
        TableUtils.createTable(connectionSource, BlipEntry.class);
    }

    @AfterEach
    void afterEachTest() throws Exception {
        TableUtils.dropTable(connectionSource, BlipEntry.class, true);
        TableUtils.dropTable(connectionSource, Volume.class, true);
        connectionSource.close();
    }

    @Test
    void persistBlip() throws SQLException {

        Volume volume = new Volume(9, LocalDate.of(2022,1,31));

        Dao<Volume, Integer> volumesDao = DaoManager.createDao(connectionSource, Volume.class);
        volumesDao.create(volume);

        Volume verifyFromDb = volumesDao.queryForId(9);
        assertEquals(9, verifyFromDb.getNumber());

        BlipEntry blipEntry = new BlipEntry(UniqueBlipId.from(212), volume, Quadrant.platforms,
                Ring.Assess, "blip description", 42);

        Dao<BlipEntry, Long> blipEntyrDao = DaoManager.createDao(connectionSource, BlipEntry.class);

        blipEntyrDao.create(blipEntry);

        BlipEntry result = blipEntyrDao.queryForFirst();

        assertEquals(blipEntry.getDescription(), result.getDescription());
        assertEquals(blipEntry.getIdOnThisRadar(), result.getIdOnThisRadar());
        assertEquals(blipEntry.getUniqueId(), result.getUniqueId());
        assertEquals(blipEntry.getRing(), blipEntry.getRing());
        assertEquals(blipEntry.getQuadrant(), blipEntry.getQuadrant());

        assertEquals(blipEntry.getDate(), result.getDate());

    }
}
