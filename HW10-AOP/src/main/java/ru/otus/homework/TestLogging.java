package ru.otus.homework;

public class TestLogging implements TestLoggingInterface {
    @Log
    public void calculation(int param1){
        System.out.println("This calculation with one param\n");
    }

    public void calculation(int param1, int param2) {
        System.out.println("This calculation with two params\n");
    }

    @Log
    public void calculation(int param1, int param2, String param3) {
        System.out.println("This calculation with three params\n");
    }

}
