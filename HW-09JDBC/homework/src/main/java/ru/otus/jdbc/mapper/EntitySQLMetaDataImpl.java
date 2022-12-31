package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private final String tableName;
    private final List<Field> fieldsWithoutId;
    private final String idField;

    private final String selectAllSql;
    private final String selectByIdSql;
    private final String insertSql;
    private final String updateSql;


    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {

        this.fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        this.idField = entityClassMetaData.getIdField().getName();
        this.tableName = entityClassMetaData.getName().toLowerCase();

        this.selectAllSql = createSelectAllSql();
        this.selectByIdSql = createSelectByIdSql();
        this.insertSql = createInsertSql();
        this.updateSql = createUpdateSql();
    }

    private String createSelectAllSql() {
        return "SELECT * FROM " + tableName;
    }

    private String createSelectByIdSql() {
        return "SELECT * FROM " + tableName + " WHERE " + idField + " = ?";
    }

    private String createInsertSql() {
        String columns = fieldsWithoutId.stream().map(Field::getName).collect(Collectors.joining(", "));
        String numberOfQuestions = createQuestions();

        return "INSERT INTO " + tableName + "(" + columns + ")" + " VALUES (" + numberOfQuestions + ")";
    }

    private String createQuestions() {
        String questions = "?, ".repeat(fieldsWithoutId.size());
        return questions.substring(0, questions.length() - 2);
    }

    private String createUpdateSql() {
        String params = fieldsWithoutId.stream().map(Field::getName).collect(Collectors.joining(" = ?, ")) + " = ? ";
        return "UPDATE " + tableName + " SET " + params + " WHERE " + idField + " = ?";
    }

    @Override
    public String getSelectAllSql() {
        return selectAllSql;
    }

    @Override
    public String getSelectByIdSql() {
        return selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        return updateSql;
    }


}
