package ru.otus.atm;

import ru.otus.Banknote;

import java.util.List;

public interface Atm {

    void receive(List<Banknote> amount);

    List<Banknote> issue(int requestedAmount);

    int reportBalance();

}
