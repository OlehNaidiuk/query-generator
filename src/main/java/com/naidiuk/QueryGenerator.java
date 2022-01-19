package com.naidiuk;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class QueryGenerator {

    public String getAll(Class<?> clazz) {
        String tableName = getTableName(clazz);
        StringBuilder querySelectAll = new StringBuilder("SELECT ");
        StringJoiner columnNames = new StringJoiner(", ");
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                String columnName = getColumnName(declaredField);
                columnNames.add(columnName);
            }
        }
        querySelectAll.append(columnNames);
        querySelectAll.append(" FROM ");
        querySelectAll.append(tableName);
        querySelectAll.append(";");
        return querySelectAll.toString();
    }

    public String insert(Object value) {
        String tableName = getTableName(value.getClass());
        StringBuilder queryInsert = new StringBuilder("INSERT INTO ");
        StringJoiner columnNames = new StringJoiner(", ", "(", ")");
        StringJoiner columnValues = new StringJoiner(", ", "(", ")");
        for (Field declaredField : value.getClass().getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                String columnName = getColumnName(declaredField);
                columnNames.add(columnName);
                Object columnValue;
                try {
                    declaredField.setAccessible(true);
                    columnValue = declaredField.get(value);
                    declaredField.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("No access to field");
                }
                if (declaredField.getType().equals(String.class)) {
                    columnValues.add(String.format("'%s'", columnValue));
                } else {
                    columnValues.add(String.valueOf(columnValue));
                }

            }
        }
        queryInsert.append(tableName);
        queryInsert.append(" ");
        queryInsert.append(columnNames);
        queryInsert.append(" VALUES ");
        queryInsert.append(columnValues);
        queryInsert.append(";");
        return queryInsert.toString();
    }

    public String update(Object value) {
        String tableName = getTableName(value.getClass());
        StringBuilder queryUpdate = new StringBuilder("UPDATE ");
        StringJoiner columnNames = new StringJoiner(", ");
        String condition = "";
        for (Field declaredField : value.getClass().getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                String columnName = getColumnName(declaredField);
                Object columnValue;
                try {
                    declaredField.setAccessible(true);
                    columnValue = declaredField.get(value);
                    declaredField.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("No access to field");
                }
                if (declaredField.isAnnotationPresent(Id.class)) {
                    condition = getIdName(declaredField) + "=" + columnValue;
                } else if (declaredField.getType().equals(String.class)) {
                    columnNames.add(columnName + "=" + String.format("'%s'", columnValue));
                } else {
                    columnNames.add(columnName + "=" + columnValue);
                }

            }
        }
        queryUpdate.append(tableName);
        queryUpdate.append(" SET ");
        queryUpdate.append(columnNames);
        queryUpdate.append(" WHERE ");
        queryUpdate.append(condition);
        queryUpdate.append(";");
        return queryUpdate.toString();
    }

    public String getById(Class<?> clazz, Object id) {
        String tableName = getTableName(clazz);
        StringBuilder querySelectById = new StringBuilder("SELECT ");
        StringJoiner columnNames = new StringJoiner(", ");
        String condition = "";
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                if (declaredField.isAnnotationPresent(Id.class)) {
                    condition = getIdName(declaredField) + "=" + id;
                } else {
                    String columnName = getColumnName(declaredField);
                    columnNames.add(columnName);
                }
            }
        }
        querySelectById.append(columnNames);
        querySelectById.append(" FROM ");
        querySelectById.append(tableName);
        querySelectById.append(" WHERE ");
        querySelectById.append(condition);
        querySelectById.append(";");
        return querySelectById.toString();
    }

    public String delete(Class<?> clazz, Object id) {
        String tableName = getTableName(clazz);
        StringBuilder queryDelete = new StringBuilder("DELETE FROM ");
        String condition = "";
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class) && declaredField.isAnnotationPresent(Id.class)) {
                condition = getIdName(declaredField) + "=" + id;
            }
        }
        queryDelete.append(tableName);
        queryDelete.append(" WHERE ");
        queryDelete.append(condition);
        queryDelete.append(";");
        return queryDelete.toString();
    }

    private String getTableName(Class<?> clazz) {
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }
        return tableAnnotation.name().isEmpty() ?
                clazz.getSimpleName() : tableAnnotation.name();
    }

    private String getColumnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        return columnAnnotation.name().isEmpty() ?
                field.getName() : columnAnnotation.name();
    }

    private String getIdName(Field field) {
        Id idAnnotation = field.getAnnotation(Id.class);
        return idAnnotation.name().isEmpty() ?
                field.getName() : idAnnotation.name();
    }
}
