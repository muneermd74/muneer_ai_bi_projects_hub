package com.example.finance.statements;

import com.example.finance.accounting.AccountType;
import com.example.finance.accounting.Ledger;
import com.example.finance.util.Money;

import java.time.LocalDate;
import java.util.Map;

public class BalanceSheet {
    public static void print(Ledger ledger, LocalDate asOf) {
        Map<String, Money> balances = ledger.calculateBalances(null, asOf);
        Money totalAssets = ledger.sumByTypes(balances, AccountType.ASSET);
        Money totalLiabilities = ledger.sumByTypes(balances, AccountType.LIABILITY);
        Money totalEquity = ledger.sumByTypes(balances, AccountType.EQUITY);

        System.out.println("==== Balance Sheet as of " + asOf + " ====");
        System.out.println("Assets:");
        for (String acc : ledger.getAccounts()) {
            if (ledger.getAccountType(acc) == AccountType.ASSET) {
                System.out.printf("  %-25s %12s%n", acc, balances.get(acc));
            }
        }
        System.out.printf("  %-25s %12s%n", "Total Assets", totalAssets);
        System.out.println();

        System.out.println("Liabilities:");
        for (String acc : ledger.getAccounts()) {
            if (ledger.getAccountType(acc) == AccountType.LIABILITY) {
                System.out.printf("  %-25s %12s%n", acc, balances.get(acc));
            }
        }
        System.out.printf("  %-25s %12s%n", "Total Liabilities", totalLiabilities);
        System.out.println();

        System.out.println("Equity:");
        for (String acc : ledger.getAccounts()) {
            if (ledger.getAccountType(acc) == AccountType.EQUITY) {
                System.out.printf("  %-25s %12s%n", acc, balances.get(acc));
            }
        }
        System.out.printf("  %-25s %12s%n", "Total Equity", totalEquity);
        System.out.println();

        System.out.printf("%-27s %12s%n", "Assets", totalAssets);
        System.out.printf("%-27s %12s%n", "Liabilities + Equity", totalLiabilities.add(totalEquity));
        System.out.println("===============================\n");
    }
}