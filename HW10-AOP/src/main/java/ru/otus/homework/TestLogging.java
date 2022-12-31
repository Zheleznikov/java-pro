package ru.otus.homework;

public class TestLogging implements TestLoggingInterface {

    public void calculation(int param1) {
        System.out.println("This calculation with one param\n");
    }

    @Log
    public void calculation(int param1, int param2) {
        System.out.println("This calculation with two params\n");
    }

    public void calculation(String param1, String param2) {
        System.out.println(param1);
        System.out.println(param2);
        System.out.println("This calculation with two strings params\n");
    }

    public void calculation(int param1, int param2, String param3) {
        System.out.println("This calculation with three params\n");
    }

    public void a(int param1, int param2) {
        System.out.println("method a with two ints params");
    }

    public void a(String param1, String param2) {
        System.out.println("method a with two string params");
    }

    @Log
    public void b(String param1, String param2) {
        System.out.println("method b with two string params");
    }


}
