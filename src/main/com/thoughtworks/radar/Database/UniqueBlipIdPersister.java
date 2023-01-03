package com.thoughtworks.radar.Database;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;
import com.thoughtworks.radar.domain.UniqueBlipId;

import java.sql.SQLException;

public class UniqueBlipIdPersister extends BaseDataType {

    private static final UniqueBlipIdPersister singleTon = new UniqueBlipIdPersister();

    public UniqueBlipIdPersister() {
        super(SqlType.INTEGER, new Class[]{UniqueBlipId.class});
    }

    public static UniqueBlipIdPersister getSingleton() {
        return singleTon;
    }

    public UniqueBlipIdPersister(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String s) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults databaseResults, int columnPos) throws SQLException {
        return databaseResults.getInt(columnPos);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        Integer integer = (Integer) sqlArg;
        return UniqueBlipId.from(integer);
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        UniqueBlipId blipId = (UniqueBlipId) javaObject;
        return blipId.getUnderlyingId();
    }

}
