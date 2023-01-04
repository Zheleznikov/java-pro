package ru.otus.core.hibernate.exception;

public class DataTemplateException extends RuntimeException {
    public DataTemplateException(Exception ex) {
        super(ex);
    }
}
