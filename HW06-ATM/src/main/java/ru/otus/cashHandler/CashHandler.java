package ru.otus.cashHandler;

import ru.otus.Banknote;

import java.util.List;

public interface CashHandler {
    void handleCash(List<Banknote> cash);

    int giveBalance();

    List<Banknote> issueCashFromCassettes(int amount);

}
