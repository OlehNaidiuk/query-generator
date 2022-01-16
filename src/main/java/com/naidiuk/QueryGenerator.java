package com.naidiuk;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class QueryGenerator {
    public String getAll(Class<?> clazz) {
        StringBuilder selectAllQueryToSQL = new StringBuilder("SELECT ");
        String tableName = getTableName(clazz);
        StringJoiner columnNames = new StringJoiner(", ");
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = declaredField.getAnnotation(Column.class);
                String columnName = columnAnnotation.name().isEmpty() ?
                        declaredField.getName() : columnAnnotation.name();
                columnNames.add(columnName);
            }
        }
        selectAllQueryToSQL.append(columnNames);
        selectAllQueryToSQL.append(" FROM ");
        selectAllQueryToSQL.append(tableName);
        selectAllQueryToSQL.append(";");
        return selectAllQueryToSQL.toString();
    }

    public String insert(Object value) {
        StringBuilder insertQueryToSQL = new StringBuilder("INSERT INTO ");
        String tableName = getTableName(value.getClass());
        StringJoiner columnNames = new StringJoiner(", ", "(", ")");
        StringJoiner columnValues = new StringJoiner(", ", "(", ")");
        for (Field declaredField : value.getClass().getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                declaredField.setAccessible(true);
                Column columnAnnotation = declaredField.getAnnotation(Column.class);
                String columnName = columnAnnotation.name().isEmpty() ?
                        declaredField.getName() : columnAnnotation.name();
                Object columnValue;
                try {
                    columnValue = declaredField.get(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("No access to field");
                }
                columnNames.add(columnName);
                if (declaredField.getType().equals(String.class)) {
                    String attribute = String.format("'%s'", columnValue);
                    columnValues.add(attribute);
                } else {
                    columnValues.add(String.valueOf(columnValue));
                }
                declaredField.setAccessible(false);
            }
        }
        insertQueryToSQL.append(tableName);
        insertQueryToSQL.append(" ");
        insertQueryToSQL.append(columnNames);
        insertQueryToSQL.append(" VALUES ");
        insertQueryToSQL.append(columnValues);
        insertQueryToSQL.append(";");
        return insertQueryToSQL.toString();
    }

    public String update(Object value) {
        StringBuilder updateQueryToSQL = new StringBuilder("UPDATE ");
        String tableName = getTableName(value.getClass());
        StringJoiner columnNames = new StringJoiner(", ");
        String primaryKey = "";
        for (Field declaredField : value.getClass().getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                declaredField.setAccessible(true);
                Column columnAnnotation = declaredField.getAnnotation(Column.class);
                String columnName = columnAnnotation.name().isEmpty() ?
                        declaredField.getName() : columnAnnotation.name();
                Object columnValue;
                try {
                    columnValue = declaredField.get(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("No access to field");
                }
                if (columnName.equals("id")) {
                    primaryKey = columnName + "=" + columnValue;
                } else if (declaredField.getType().equals(String.class)) {
                    String attribute = String.format("'%s'", columnValue);
                    columnNames.add(columnName + "=" + attribute);
                } else {
                    columnNames.add(columnName + "=" + columnValue);
                }
                declaredField.setAccessible(false);
            }
        }
        updateQueryToSQL.append(tableName);
        updateQueryToSQL.append(" SET ");
        updateQueryToSQL.append(columnNames);
        updateQueryToSQL.append(" WHERE ");
        updateQueryToSQL.append(primaryKey);
        updateQueryToSQL.append(";");
        return updateQueryToSQL.toString();
    }

    public String getById(Class<?> clazz, Object id) {
        StringBuilder selectByIdQueryToSQL = new StringBuilder("SELECT ");
        String tableName = getTableName(clazz);
        StringJoiner columnNames = new StringJoiner(", ");
        String primaryKey = "";
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = declaredField.getAnnotation(Column.class);
                String columnName = columnAnnotation.name().isEmpty() ?
                        declaredField.getName() : columnAnnotation.name();
                if (columnName.equals("id")) {
                    primaryKey = columnName + "=" + id;
                } else {
                    columnNames.add(columnName);
                }
            }
        }
        selectByIdQueryToSQL.append(columnNames);
        selectByIdQueryToSQL.append(" FROM ");
        selectByIdQueryToSQL.append(tableName);
        selectByIdQueryToSQL.append(" WHERE ");
        selectByIdQueryToSQL.append(primaryKey);
        selectByIdQueryToSQL.append(";");
        return selectByIdQueryToSQL.toString();
    }

    public String delete(Class<?> clazz, Object id) {
        StringBuilder deleteQueryToSQL = new StringBuilder("DELETE FROM ");
        String tableName = getTableName(clazz);
        String primaryKey = "";
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                Column columnAnnotation = declaredField.getAnnotation(Column.class);
                String columnName = columnAnnotation.name().isEmpty() ?
                        declaredField.getName() : columnAnnotation.name();
                if (columnName.equals("id")) {
                    primaryKey = columnName + "=" + id;
                }
            }
        }
        deleteQueryToSQL.append(tableName);
        deleteQueryToSQL.append(" WHERE ");
        deleteQueryToSQL.append(primaryKey);
        deleteQueryToSQL.append(";");
        return deleteQueryToSQL.toString();
    }

    private String getTableName(Class<?> clazz) {
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }
        return tableAnnotation.name().isEmpty() ?
                clazz.getSimpleName() : tableAnnotation.name();
    }
}
