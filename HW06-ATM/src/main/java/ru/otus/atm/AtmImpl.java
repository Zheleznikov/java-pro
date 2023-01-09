package ru.otus.atm;

import ru.otus.Banknote;
import ru.otus.cashHandler.CashHandlerCassette;

import java.util.List;

public class AtmImpl implements Atm {

    private final CashHandlerCassette cashHandler;

    public AtmImpl(CashHandlerCassette cashHandler) {
        this.cashHandler = cashHandler;
    }

    @Override
    public void receive(List<Banknote> amount) {
        cashHandler.handleCash(amount);
    }

    @Override
    public List<Banknote> issue(int requestedAmount) {
        return cashHandler.issueCashFromCassettes(requestedAmount);
    }

    @Override
    public int reportBalance() {
        return cashHandler.giveBalance();
    }
}
