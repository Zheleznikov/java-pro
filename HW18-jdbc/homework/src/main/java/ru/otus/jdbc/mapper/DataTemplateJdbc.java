package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Сохраняет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData entityClassMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        String query = entitySQLMetaData.getSelectByIdSql();
        List<Object> params = Collections.singletonList(id);

        return dbExecutor.executeSelect(connection, query, params, this::getTargetObject);
                
    }

    @Override
    public List<T> findAll(Connection connection) {
        String query = entitySQLMetaData.getSelectAllSql();
        List<Object> params = Collections.emptyList();

        return dbExecutor.executeSelect(connection, query, params, rs -> getTargetList(rs))
                .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T clientClass) {
        List<Object> params = getParams(clientClass);
        String query = entitySQLMetaData.getInsertSql();

        try {
            return dbExecutor.executeStatement(connection, query, params);
        } catch (Exception exception) {
            throw new DataTemplateException(exception);
        }
    }

    @Override
    public void update(Connection connection, T clientClass) {
        String query = entitySQLMetaData.getUpdateSql();
        List<Object> params = getParams(clientClass);
        long id = getId(clientClass);
        params.add(id);
        
        dbExecutor.executeStatement(connection, query, params);
    }


    private List<Object> getParams(T clientClass) {
        List<Object> params = new ArrayList<>();
        List<Field> fields = entityClassMetaData.getFieldsWithoutId();

        fields.forEach(field -> {
            try {
                Field currentField = clientClass.getClass().getDeclaredField(field.getName());
                currentField.setAccessible(true);
                Object value = currentField.get(clientClass);
                params.add(value);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new DataTemplateException(e);
            }
        });

        return params;
    }

    private long getId(T clientClass) {
        Field idField = entityClassMetaData.getIdField();

        try {
            Field currentField = clientClass.getClass().getDeclaredField(idField.getName());
            currentField.setAccessible(true);
            return (long) currentField.get(clientClass);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new DataTemplateException(e);
        }

    }

    private T getTargetObject(ResultSet rs) {
        try {
            List<Field> fields = entityClassMetaData.getAllFields();

            if (rs.next()) {
                T t = (T) entityClassMetaData.getConstructor().newInstance();
                return createObjectWithSetFields(fields, rs, t);
            }

        } catch (SQLException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
        throw new DataTemplateException(new Exception());
    }

    private List<T> getTargetList(ResultSet rs) {
        List<T> targetList = new ArrayList<>();

        try {
            List<Field> fields = entityClassMetaData.getAllFields();

            while (rs.next()) {
                T t = (T) entityClassMetaData.getConstructor().newInstance();
                targetList.add(createObjectWithSetFields(fields, rs, t));
            }
            return targetList;

        } catch (SQLException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }

    private T createObjectWithSetFields(List<Field> fields, ResultSet rs, T t) {
        fields.forEach(field -> {
            try {
                field.setAccessible(true);
                Object object = rs.getObject(field.getName());
                field.set(t, object);

            } catch (SQLException | IllegalAccessException e) {
                throw new DataTemplateException(e);
            }
        });
        return t;
    }
}
