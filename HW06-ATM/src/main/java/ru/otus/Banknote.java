package ru.otus;

public enum Banknote {

    VALUE_10(10),
    VALUE_50(50),
    VALUE_100(100),
    VALUE_500(500),
    VALUE_1000(1000),
    VALUE_2000(2000),
    VALUE_5000(5000);

    private final int denomination;

    Banknote(int denomination) {
        this.denomination = denomination;
    }

    public int getDenomination() {
        return denomination;
    }

}

