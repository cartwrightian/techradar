package com.thoughtworks.radar.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.thoughtworks.radar.domain.Blip;
import com.thoughtworks.radar.domain.UniqueBlipId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlipDatabaseTest {
    private JdbcPooledConnectionSource connectionSource;

    @BeforeEach
    void beforeEachTest() throws SQLException {
        connectionSource = new JdbcPooledConnectionSource("jdbc:h2:mem:myDb");

        TableUtils.dropTable(connectionSource, Blip.class, true);
        TableUtils.createTable(connectionSource, Blip.class);
    }

    @AfterEach
    void afterEachTest() throws Exception {
        TableUtils.dropTable(connectionSource, Blip.class, true);
        connectionSource.close();
    }

    @Test
    void persistBlip() throws SQLException {

        UniqueBlipId uniqueId = UniqueBlipId.from("1232");
        Blip blip = new Blip(uniqueId, "blip name");

        Dao<Blip, UniqueBlipId> volumesDao = DaoManager.createDao(connectionSource, Blip.class);

        volumesDao.create(blip);

        Blip result = volumesDao.queryForId(uniqueId);

        assertEquals(blip.getId(), result.getId());
        assertEquals(blip.getName(), result.getName());
    }
}
