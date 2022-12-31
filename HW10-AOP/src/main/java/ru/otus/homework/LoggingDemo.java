package ru.otus.homework;

public class LoggingDemo {
    public static void main(String[] args) {
        TestLoggingInterface testLogging = Ioc.createMyClass();

        testLogging.calculation(5);
        testLogging.calculation(1, 2);
        testLogging.calculation("param1", "param2");
        testLogging.calculation(6,7,"Hello");
        testLogging.calculation(12);
        testLogging.calculation(7, 8);

        testLogging.a(1,2);
        testLogging.a("str1", "str2");
        testLogging.b("str1", "str2");


    }
}
