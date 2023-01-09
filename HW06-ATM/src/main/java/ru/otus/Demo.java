package ru.otus;

import ru.otus.atm.AtmImpl;
import ru.otus.cashHandler.CashHandlerCassette;

import java.util.Arrays;
import java.util.List;

public class Demo {

    public static void main(String[] args) {

        List<Banknote> cash = Arrays.asList(Banknote.VALUE_10, Banknote.VALUE_50, Banknote.VALUE_2000,
                Banknote.VALUE_2000, Banknote.VALUE_10, Banknote.VALUE_100, Banknote.VALUE_50, Banknote.VALUE_10);

        var cashHandler = new CashHandlerCassette();
        var atm = new AtmImpl(cashHandler);

        atm.receive(cash);
        System.out.println("atm.reportBalance() - " + atm.reportBalance());

        System.out.println("Снимаем 100 у.е");
        List<Banknote> issue = atm.issue(100);
        issue.forEach(System.out::println);

        System.out.println("atm.reportBalance() - " + atm.reportBalance());

        System.out.println("Снимаем 2050 у.е");
        List<Banknote> issue2 = atm.issue(2050);
        issue2.forEach(System.out::println);

        System.out.println("atm.reportBalance() - " + atm.reportBalance());

        // при попытке снять невозможную сумму выбрасывается исключение
        System.out.println("Снимаем 2023 у.е");
        List<Banknote> issue3 = atm.issue(2023);

    }
}
