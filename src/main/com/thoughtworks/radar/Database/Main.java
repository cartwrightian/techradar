package com.thoughtworks.radar.Database;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.thoughtworks.radar.Radars;
import com.thoughtworks.radar.file.RadarFileService;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    private final String dataFolder;
    private final String jdbcDriver;

    public Main(String dataFolder, String jdbcDriver) {

        this.dataFolder = dataFolder;
        this.jdbcDriver = jdbcDriver;
    }

    public static void main(String[] args) throws IOException {
        if (args.length!=2) {
            System.console().writer().write("Not enough args, expected dataFolder and jdbc url");
            System.exit(-1);
        }
        try {
            new Main(args[0], args[1]).persist();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void persist() throws Exception {
        JdbcPooledConnectionSource connectionSource = null;
        try {
            connectionSource = new JdbcPooledConnectionSource(jdbcDriver);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        try {
            persist(connectionSource);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            connectionSource.close();
            throw e;
        }
    }

    private void persist(JdbcPooledConnectionSource connectionSource) throws IOException, SQLException {
        RadarDatabaseService databaseService = new RadarDatabaseService(connectionSource);

        RadarFileService fileService = new RadarFileService(dataFolder);
        Radars radars = fileService.loadRadars();

        databaseService.clean();
        databaseService.save(radars);
    }
}
