package ru.otus.jdbc.mapper;

import ru.otus.annotation.Id;
import ru.otus.core.repository.DataTemplateException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> clientClass;

    private final String name;
    private final Field idField;
    private final Constructor<T> constructor;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> clientClass) {
        this.clientClass = clientClass;
        this.name = clientClass.getSimpleName();

        this.idField = createIdField();
        this.constructor = createConstructor();
        this.allFields = createAllFields();
        this.fieldsWithoutId = createFieldsWithoutId();
    }

    private List<Field> createFieldsWithoutId() {
        return Arrays.stream(clientClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .toList();
    }

    private List<Field> createAllFields() {
        return Arrays.asList(clientClass.getDeclaredFields());
    }

    private Constructor<T> createConstructor() {
        try {
            return clientClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new DataTemplateException(e);
        }
    }

    public Field createIdField() {
        return Arrays.stream(clientClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst().orElse(null);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }


    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }
}
