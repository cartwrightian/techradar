package com.thoughtworks.radar.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.thoughtworks.radar.domain.Volume;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VolumeDatabaseTest {

    private JdbcPooledConnectionSource connectionSource;

    @BeforeEach
    void beforeEachTest() throws SQLException {
         connectionSource = new JdbcPooledConnectionSource("jdbc:h2:mem:myDb");

         TableUtils.dropTable(connectionSource, Volume.class, true);
         TableUtils.createTable(connectionSource, Volume.class);
    }

    @AfterEach
    void afterEachTest() throws Exception {
        TableUtils.dropTable(connectionSource, Volume.class, true);
        connectionSource.close();
    }

    @Test
    void saveVolume() throws SQLException {

        Volume volume = new Volume(2, LocalDate.of(2022,1,31));

        Dao<Volume, Integer> volumesDao = DaoManager.createDao(connectionSource, Volume.class);

        volumesDao.create(volume);

        Volume result = volumesDao.queryForId(2);

        assertEquals(volume.getNumber(), result.getNumber());
        assertEquals(volume.getPublicationDate(), result.getPublicationDate());
    }
}
