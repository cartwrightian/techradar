package com.thoughtworks.radar.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import com.thoughtworks.radar.domain.BlipEntry;
import com.thoughtworks.radar.domain.Quadrant;
import com.thoughtworks.radar.domain.Ring;
import com.thoughtworks.radar.domain.UniqueBlipId;
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
        TableUtils.createTable(connectionSource, BlipEntry.class);
    }

    @AfterEach
    void afterEachTest() throws Exception {
        TableUtils.dropTable(connectionSource, BlipEntry.class, true);
        connectionSource.close();
    }

    @Test
    void persistBlip() throws SQLException {

        BlipEntry blipEntry = new BlipEntry(UniqueBlipId.from(212), LocalDate.of(2022,1,31),
                Quadrant.platforms, Ring.Assess, "blip description", 42);

        Dao<BlipEntry, Long> volumesDao = DaoManager.createDao(connectionSource, BlipEntry.class);

        volumesDao.create(blipEntry);

        BlipEntry result = volumesDao.queryForFirst();

        assertEquals(blipEntry.getDate(), result.getDate());
        assertEquals(blipEntry.getDescription(), result.getDescription());
        assertEquals(blipEntry.getIdOnThisRadar(), result.getIdOnThisRadar());
        assertEquals(blipEntry.getUniqueId(), result.getUniqueId());
        assertEquals(blipEntry.getRing(), blipEntry.getRing());
        assertEquals(blipEntry.getQuadrant(), blipEntry.getQuadrant());

    }
}
