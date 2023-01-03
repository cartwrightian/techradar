package com.thoughtworks.radar.Database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.thoughtworks.radar.Radars;
import com.thoughtworks.radar.repository.BlipRepository;
import com.thoughtworks.radar.repository.VolumeRepository;
import com.thoughtworks.radar.domain.Blip;
import com.thoughtworks.radar.domain.BlipEntry;
import com.thoughtworks.radar.domain.Volume;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RadarDatabaseService {

    private final JdbcPooledConnectionSource connectionSource;

    public RadarDatabaseService(JdbcPooledConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    public void save(Radars radars) throws SQLException {
        TableUtils.createTable(connectionSource, Volume.class);
        TableUtils.createTable(connectionSource, Blip.class);
        TableUtils.createTable(connectionSource, BlipEntry.class);

        VolumeRepository volumeRepos = radars.getVolumeRepository();
        saveVolumes(volumeRepos.getVolumes());
        saveBlips(radars.getBlips());
        saveBlipEntries(radars.getBlipEntries());
    }

    public void clean() throws SQLException {
        TableUtils.dropTable(connectionSource, BlipEntry.class, false);
        TableUtils.dropTable(connectionSource, Blip.class, false);
        TableUtils.dropTable(connectionSource, Volume.class, false);
    }

    private void saveBlipEntries(List<BlipEntry> blipEntries) throws SQLException {
        Dao<BlipEntry, Long> entryDao = getEntryDao();
        entryDao.create(blipEntries);
    }

    private void saveVolumes(List<Volume> volumes) throws SQLException {
        Dao<Volume, Integer> volumeDao = getVolumeDao();
        volumeDao.create(volumes);
    }

    private void saveBlips(List<Blip> blips) throws SQLException {
        Dao<Blip, Integer> dao = getBlipDao();
        dao.create(blips);
    }

    public Radars load() throws SQLException {
        VolumeRepository volumeRepository = loadRepository();

        BlipRepository blipRepository = loadBlips();

        loadBlipHistoryInto(volumeRepository, blipRepository);

        return new Radars(volumeRepository, blipRepository);
    }

    private void loadBlipHistoryInto(VolumeRepository volumeRepository, BlipRepository blipRepository) throws SQLException {
        List<BlipEntry> allHistory = getEntryDao().queryForAll();

        blipRepository.updateHistory(allHistory, volumeRepository);
    }

    private BlipRepository loadBlips() throws SQLException {
        List<Blip> blipsWithoutHistory = getBlipDao().queryForAll();
        return new BlipRepository(blipsWithoutHistory);
    }

    private VolumeRepository loadRepository() throws SQLException {
        Dao<Volume, Integer> dao = getVolumeDao();
        List<Volume> volumes = dao.queryForAll();

        Set<LocalDate> volumeDates = volumes.stream().map(Volume::getPublicationDate).collect(Collectors.toSet());

        return new VolumeRepository(volumeDates);
    }

    private Dao<Volume, Integer> getVolumeDao() throws SQLException {
        return DaoManager.createDao(connectionSource, Volume.class);
    }

    private Dao<Blip, Integer> getBlipDao() throws SQLException {
        return DaoManager.createDao(connectionSource, Blip.class);
    }

    private Dao<BlipEntry, Long> getEntryDao() throws SQLException {
        return DaoManager.createDao(connectionSource, BlipEntry.class);
    }


}
