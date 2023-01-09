package ru.otus.cashHandler;

import ru.otus.Banknote;
import ru.otus.exception.NoDenominationException;

import java.util.*;

/*
Реализация cashHandler без использования ячейки банкомата. Оставил, как пример более простой версии
 */
public class CashHandlerMap implements CashHandler {

    private final Map<Banknote, Integer> atmCassetteMap = new HashMap<>();


    @Override
    public void handleCash(List<Banknote> cash) {
        cash.forEach(banknote -> {

            if (atmCassetteMap.containsKey(banknote)) {
                int count = atmCassetteMap.get(banknote);
                atmCassetteMap.put(banknote, count + 1);
            } else {
                atmCassetteMap.put(banknote, 1);
            }

        });
    }

    @Override
    public int giveBalance() {
        return atmCassetteMap.entrySet()
                .stream()
                .map(entry -> entry.getKey().getDenomination() * entry.getValue())
                .reduce(Integer::sum)
                .get();
    }

    @Override
    public List<Banknote> issueCashFromCassettes(int amount) {
        List<Banknote> cash = new ArrayList<>();

        while (amount > 0) {
            Banknote closest = getClosest(amount);
            int count = atmCassetteMap.get(closest);
            atmCassetteMap.put(closest, count - 1);

            cash.add(closest);

            amount = amount - closest.getDenomination();
        }


        return cash;

    }

    private Banknote getClosest(int amount) {
        return atmCassetteMap.entrySet().stream()
                .filter(entry -> entry.getValue() != 0)
                .filter(entry -> amount - entry.getKey().getDenomination() >= 0)
                .min(Comparator.comparingInt(entry -> amount - entry.getKey().getDenomination()))
                .orElseThrow(() -> new NoDenominationException("No requested denomination in ATM"))
                .getKey();
    }



}
