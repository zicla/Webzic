package com.logzc.webzic.orm.field;

import com.logzc.webzic.orm.support.ConnectionSource;

import java.lang.reflect.Field;
import java.sql.Types;

/**
 * Created by lishuang on 2016/8/23.
 */
public class ColumnType {


    private final ConnectionSource connectionSource;
    private final String tableName;
    private final Field field;
    private final Class<?> containClass;

    public ColumnType(ConnectionSource connectionSource, String tableName, Field field, Class<?> containClass) {
        this.connectionSource = connectionSource;
        this.tableName = tableName;

        this.field = field;
        this.containClass = containClass;

    }

    public static ColumnType createColumnType(ConnectionSource connectionSource, String tableName, Field field, Class<?> dataClass) {


        if (field.isAnnotationPresent(Column.class)) {
            return new ColumnType(connectionSource, tableName, field, dataClass);
        } else {
            return null;
        }


    }


    //TODO:Finish the sql type.
    public int getSqlType() {

        return Types.VARCHAR;

    }
}
