package com.thoughtworks.radar.Database;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;


public class LocalDatePersister extends BaseDataType {

    private static final LocalDatePersister singleTon = new LocalDatePersister();
    private final LocalTime localTime = LocalTime.of(0,0);

    public static LocalDatePersister getSingleton() {
        return singleTon;
    }

    private LocalDatePersister() {
        super(SqlType.DATE, new Class[]{LocalDate.class});
    }

    protected LocalDatePersister(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getTimestamp(columnPos);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        Timestamp value = (Timestamp)sqlArg;
        return value.toLocalDateTime().toLocalDate();
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultText) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        LocalDate localDate = (LocalDate) javaObject;
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        long millis = localDateTime.toEpochSecond(ZoneOffset.UTC) * 1000;
        return new Timestamp(millis);
    }
}
