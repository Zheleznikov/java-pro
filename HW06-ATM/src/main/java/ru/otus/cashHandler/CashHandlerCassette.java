package ru.otus.cashHandler;

import ru.otus.AtmCassette;
import ru.otus.Banknote;
import ru.otus.exception.NoDenominationException;

import java.util.*;

public class CashHandlerCassette implements CashHandler {


    private final List<AtmCassette> cassettes = new ArrayList<>();

    @Override
    public void handleCash(List<Banknote> cash) {
        cash.forEach(banknote -> {

            if (hasBanknote(cassettes, banknote)) {
                var currentCassette = getCassette(cassettes, banknote);
                int count = currentCassette.getCount();
                currentCassette.setCount(count + 1);

            } else {
                cassettes.add(new AtmCassette(banknote, 1));
            }

        });
    }

    private boolean hasBanknote(List<AtmCassette> cassettes, Banknote banknote) {
        return cassettes.stream()
                .anyMatch(current -> current.getType().equals(banknote));
    }

    private AtmCassette getCassette(List<AtmCassette> cassettes, Banknote banknote) {
        return cassettes.stream()
                .filter(current -> current.getType().equals(banknote))
                .findFirst()
                .get();
    }

    @Override
    public int giveBalance() {
        return cassettes.stream()
                .map(cassette -> cassette.getType().getDenomination() * cassette.getCount())
                .reduce(Integer::sum)
                .get();
    }

    @Override
    public List<Banknote> issueCashFromCassettes(int amount) {
        List<Banknote> cash = new ArrayList<>();

        while (amount > 0) {
            Banknote closest = getClosestBanknote(amount);
            cash.add(closest);

            AtmCassette currentCassette = getCassette(cassettes, closest);
            int count = currentCassette.getCount();
            currentCassette.setCount(count - 1);

            amount = amount - closest.getDenomination();
        }

        return cash;
    }

    private Banknote getClosestBanknote(int amount) {
        return cassettes.stream()
                .filter(cassette -> cassette.getCount() > 0
                        && amount - cassette.getType().getDenomination() >= 0)
                .min(Comparator.comparingInt(cassette -> amount - cassette.getType().getDenomination()))
                .orElseThrow(() -> new NoDenominationException("No requested denomination in ATM"))
                .getType();
    }
}
